package skoala.io.daocloud.init;

import com.alibaba.csp.sentinel.cluster.flow.rule.ClusterFlowRuleManager;
import com.alibaba.csp.sentinel.cluster.flow.rule.ClusterParamFlowRuleManager;
import com.alibaba.csp.sentinel.cluster.server.config.ClusterServerConfigManager;
import com.alibaba.csp.sentinel.cluster.server.config.ServerTransportConfig;
import com.alibaba.csp.sentinel.datasource.ReadableDataSource;
import com.alibaba.csp.sentinel.datasource.nacos.NacosDataSource;
import com.alibaba.csp.sentinel.init.InitFunc;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowRule;
import com.alibaba.csp.sentinel.util.StringUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import java.util.List;
import java.util.Set;

public class InitServer implements InitFunc {
    private static final String groupId = "SENTINEL_GROUP";
    private static final String namespaceSetDataId = "cluster-server-namespace-set";
    private static final String serverTransportDataId = "cluster-server-transport-config";

    public static final String FLOW_POSTFIX = "-flow-rules";
    public static final String PARAM_FLOW_POSTFIX = "-param-rules";

    @Override
    public void init() throws Exception {
        String nacosAddress = System.getProperty("NACOS_ADDRESS");
        if (StringUtil.isBlank(nacosAddress)){
            throw new RuntimeException("NACOS_ADDRESS env must be set");
        }
        System.out.printf("nacos address: %s\n", nacosAddress);
        ClusterFlowRuleManager.setPropertySupplier(namespace -> {
            ReadableDataSource<String, List<FlowRule>> ds = new NacosDataSource<>(nacosAddress, groupId,
                    namespace + FLOW_POSTFIX,
                    source -> JSON.parseObject(source, new TypeReference<List<FlowRule>>() {}));
            return ds.getProperty();
        });
        ClusterParamFlowRuleManager.setPropertySupplier(namespace -> {
            ReadableDataSource<String, List<ParamFlowRule>> ds = new NacosDataSource<>(nacosAddress, groupId,
                    namespace + PARAM_FLOW_POSTFIX,
                    source -> JSON.parseObject(source, new TypeReference<List<ParamFlowRule>>() {}));
            return ds.getProperty();
        });

        ReadableDataSource<String, Set<String>> namespaceDs = new NacosDataSource<>(nacosAddress, groupId,
                namespaceSetDataId,
                source -> JSON.parseObject(source, new TypeReference<Set<String>>() {}));
        ClusterServerConfigManager.registerNamespaceSetProperty(namespaceDs.getProperty());
        ReadableDataSource<String, ServerTransportConfig> transportConfigDs = new NacosDataSource<>(nacosAddress, groupId,
                serverTransportDataId,
                source -> JSON.parseObject(source, new TypeReference<ServerTransportConfig>() {}));
        ClusterServerConfigManager.registerServerTransportProperty(transportConfigDs.getProperty());
    }
}

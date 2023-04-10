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
import com.alibaba.nacos.api.PropertyKeyConst;

import java.util.List;
import java.util.Properties;
import java.util.Set;

public class InitServer implements InitFunc {
    private static final String namespaceSetDataId = "cluster-server-namespace-set";
    private static final String serverTransportDataId = "cluster-server-transport-config";

    public static final String FLOW_POSTFIX = "-flow-rules";
    public static final String PARAM_FLOW_POSTFIX = "-param-rules";
    private static final String DEFAULT_GROUP = "SENTINEL_GROUP";
    public static final String DEFAULT_NACOS_USERNAME = "skoala";
    public static final String DEFAULT_NACOS_PASSWORD = "98985ba0-da90-41f6-b6dc-96f2ec49d973";

    @Override
    public void init() throws Exception {
        String nacosAddress = System.getProperty("NACOS_ADDRESS");
        if (StringUtil.isBlank(nacosAddress)){
            throw new RuntimeException("NACOS_ADDRESS env must be set");
        }
        System.out.printf("nacos address: %s\n", nacosAddress);

        ClusterFlowRuleManager.setPropertySupplier(namespace -> {
            String[] apps = parseAppName(namespace);
            Properties properties = new Properties();
            properties.put(PropertyKeyConst.SERVER_ADDR, nacosAddress);
            properties.put(PropertyKeyConst.NAMESPACE, apps[0]);
            properties.put(PropertyKeyConst.USERNAME, DEFAULT_NACOS_USERNAME);
            properties.put(PropertyKeyConst.PASSWORD, DEFAULT_NACOS_PASSWORD);

            ReadableDataSource<String, List<FlowRule>> ds = new NacosDataSource<>(properties, apps[1],
                    apps[2] + FLOW_POSTFIX,
                    source -> JSON.parseObject(source, new TypeReference<List<FlowRule>>() {}));
            return ds.getProperty();
        });
        ClusterParamFlowRuleManager.setPropertySupplier(namespace -> {
            String[] apps = parseAppName(namespace);
            Properties properties = new Properties();
            properties.put(PropertyKeyConst.SERVER_ADDR, nacosAddress);
            properties.put(PropertyKeyConst.NAMESPACE, apps[0]);
            properties.put(PropertyKeyConst.USERNAME, DEFAULT_NACOS_USERNAME);
            properties.put(PropertyKeyConst.PASSWORD, DEFAULT_NACOS_PASSWORD);

            ReadableDataSource<String, List<ParamFlowRule>> ds = new NacosDataSource<>(properties, apps[1],
                    apps[2] + PARAM_FLOW_POSTFIX,
                    source -> JSON.parseObject(source, new TypeReference<List<ParamFlowRule>>() {}));
            return ds.getProperty();
        });

        ReadableDataSource<String, Set<String>> namespaceDs = new NacosDataSource<>(nacosAddress, DEFAULT_GROUP,
                namespaceSetDataId,
                source -> JSON.parseObject(source, new TypeReference<Set<String>>() {}));
        ClusterServerConfigManager.registerNamespaceSetProperty(namespaceDs.getProperty());
        ReadableDataSource<String, ServerTransportConfig> transportConfigDs = new NacosDataSource<>(nacosAddress, DEFAULT_GROUP,
                serverTransportDataId,
                source -> JSON.parseObject(source, new TypeReference<ServerTransportConfig>() {}));
        ClusterServerConfigManager.registerServerTransportProperty(transportConfigDs.getProperty());
    }

    public String[] parseAppName(String appName) {
        String[] apps = appName.split("@@");
        if (apps.length != 3) {
            throw new RuntimeException("app name format must be set like this: {{namespaceId}}@@{{groupName}}@@{{appName}}");
        } else if (StringUtil.isBlank(apps[1])){
            throw new RuntimeException("group name cannot be empty");
        }

        return apps;
    }
}

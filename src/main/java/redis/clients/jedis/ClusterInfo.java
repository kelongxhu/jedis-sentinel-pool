package redis.clients.jedis;

import org.apache.log4j.Logger;
import redis.client.util.XMLParse;

import java.util.Map;

/**
 * @author kelong
 * @date 12/11/14
 */
public class ClusterInfo {
    Logger LOG = Logger.getLogger(ClusterInfo.class);
    private final static String DEFAUL_PATH = "redis-cluster.xml";

    private Map<String, Cluster> clusterMap;

    public ClusterInfo() {
        this.config();
    }

    public void config() {
        XMLParse parse = new XMLParse();
        clusterMap = parse.load(DEFAUL_PATH);
        if (clusterMap != null && clusterMap.size() > 0) {
            for (Map.Entry<String, Cluster> entry : clusterMap.entrySet()) {
                Cluster cluster = entry.getValue();
                cluster.wire();//初始化集群
                LOG.info("init cluster：" + entry.getKey());
            }
        }
    }

    public Cluster getCluster(String name) {
        return (clusterMap == null) ? null : clusterMap.get(name);
    }
}

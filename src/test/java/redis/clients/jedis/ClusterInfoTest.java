package redis.clients.jedis;

import org.junit.Before;
import org.junit.Test;

/**
 * @author kelong
 * @date 12/11/14
 */
public class ClusterInfoTest {
    @Before
    public void init() {

    }

    @Test
    public void clusterTest() {
        ClusterInfo clusterInfo = new ClusterInfo();
        Cluster cluster = clusterInfo.getCluster("redis");
        cluster.set("test", "test");
        System.out.println(cluster.get("test"));
    }
}

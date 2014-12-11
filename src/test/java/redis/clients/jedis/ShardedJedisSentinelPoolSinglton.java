package redis.clients.jedis;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author kelong
 * @date 12/10/14
 */
public class ShardedJedisSentinelPoolSinglton {
    public static ShardedJedisSentinelPool pool = null;

    private ShardedJedisSentinelPoolSinglton() {

    }

    public static ShardedJedisSentinelPool getPool() {
        if (pool == null) {
            GenericObjectPoolConfig config = new GenericObjectPoolConfig();

            List<String> masters = new ArrayList<String>();
            masters.add("mymaster1");
            masters.add("mymaster2");

            Set<String> sentinels = new HashSet<String>();
            sentinels.add("127.0.0.1:26379");
            sentinels.add("127.0.0.1:26389");
            sentinels.add("127.0.0.1:26399");

            pool = new ShardedJedisSentinelPool(masters, sentinels, config, 60000);
        }
        return pool;
    }
}

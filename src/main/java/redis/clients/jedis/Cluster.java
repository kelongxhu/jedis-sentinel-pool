package redis.clients.jedis;


import java.util.List;
import java.util.Set;


public class Cluster extends Command {

    private String name;
    private JedisPoolConfig config;
    private List<String> masters;
    private Set<String> sentinels;

    public Cluster() {
    }

    /**
     * config sentinelPool
     */
    public void wire() {
        if (null == pool) {
            synchronized (this) {
                if (null == pool) {
                    this.pool = new ShardedJedisSentinelPool(masters, sentinels, config, 60000);
                }
            }
        }
    }

    /**
     * destory shard cluster
     */
    public void destory() {
        this.pool.destroy();
    }

    public void setConfig(JedisPoolConfig config) {
        this.config = config;
    }

    public void setMasters(List<String> masters) {
        this.masters = masters;
    }

    public void setSentinels(Set<String> sentinels) {
        this.sentinels = sentinels;
    }

    public void setName(String name) {
        this.name = name;
    }
}
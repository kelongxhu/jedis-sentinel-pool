package redis.clients.jedis;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import redis.clients.jedis.exceptions.JedisConnectionException;

public class ShardedJedisSentinelPoolTest {

    Logger LOG=Logger.getLogger(ShardedJedisSentinelPoolTest.class);

    ShardedJedisSentinelPool pool;

    @Before
    public void init() {
//        GenericObjectPoolConfig config = new GenericObjectPoolConfig();
//
//        List<String> masters = new ArrayList<String>();
//        masters.add("mymaster1");
//        masters.add("mymaster2");
//
//        Set<String> sentinels = new HashSet<String>();
//        sentinels.add("127.0.0.1:26379");
//        sentinels.add("127.0.0.1:26389");
//        sentinels.add("127.0.0.1:26399");
//
//        pool = new ShardedJedisSentinelPool(masters, sentinels, config, 60000);
        pool=ShardedJedisSentinelPoolSinglton.getPool();
    }

    @Test
    public void test()throws Exception{
        LOG.info("==================test==========================");
        for(int i=0;i<100;i++){
            testSet(i);
            Thread.sleep(1000 * 30);
        }
    }

    public void testSet(int i) throws Exception {
        System.out.println("=====" + pool.getCurrentHostMaster().toString());
        ShardedJedis j = null;
            try {
                j = pool.getResource();
                j.set("KEY: " + i, "" + i);
                System.out.println(" " + i);
                pool.returnResource(j);
            } catch (JedisConnectionException e) {
                e.printStackTrace();
            }
    }

    @Test
    public void failOver()throws Exception{
        ShardedJedis j = null;
        for (int i = 0; i < 10; i++) {
            try {
                j = pool.getResource();
                j.set("KEY: " + i, "" + i);
                System.out.print(i);
                System.out.print(" ");
                Thread.sleep(500);
                pool.returnResource(j);
            } catch (JedisConnectionException e) {
                e.printStackTrace();
                System.out.print("x");
                i--;
                Thread.sleep(1000);
            }
        }
    }
    @Test
    public void get()throws Exception{
        ShardedJedis j = null;
        for (int i = 0; i < 10; i++) {
            try {
                j = pool.getResource();
                System.out.println("get value:" + j.get("KEY: " + i));
                System.out.print(".");
                Thread.sleep(500);
                pool.returnResource(j);
            } catch (JedisConnectionException e) {
                System.out.print("x");
                i--;
                Thread.sleep(1000);
            }
        }
    }
}

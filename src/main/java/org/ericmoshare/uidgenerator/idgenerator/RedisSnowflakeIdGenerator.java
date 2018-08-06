package org.ericmoshare.uidgenerator.idgenerator;

import org.ericmoshare.uidgenerator.utils.NetUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * @author eric.mo
 * @since 2018/7/11
 */
public class RedisSnowflakeIdGenerator extends AbstractSnowflakeIdGenerator {

    private static final Logger logger = LoggerFactory.getLogger(RedisSnowflakeIdGenerator.class);

    private static final String SCRIPT_ID_AUTO_INCREMENT = "local pk=1;if (redis.call('exists', KEYS[1]) == 0) then redis.call('hset', KEYS[1], ARGV[1], 1);" +
            " pk=redis.call('hget', KEYS[1], ARGV[1] ); redis.call('hset', KEYS[1], ARGV[2], pk); return redis.call('hget', KEYS[1], ARGV[2] ); end; " +
            "if (redis.call('hexists', KEYS[1], ARGV[2]) == 1) then return redis.call('hget', KEYS[1], ARGV[2] ); " +
            "else  redis.call('hincrby', KEYS[1], ARGV[1], 1);  pk=redis.call('hget', KEYS[1], ARGV[1] ); " +
            "redis.call('hset', KEYS[1], ARGV[2], pk);     return redis.call('hget', KEYS[1], ARGV[2] );  end; " +
            "return redis.call('hget', KEYS[1], ARGV[2] );";

    private static final String HASHKEY = "xppay_snowflake_servers";

    private static final String AUTO_INCREMENT_PRIMARY_KEY = "auto_increment_primary_key";

    private static volatile Long serverNo = 0L;

    private static final String NONCE = "1000";

    private JedisPool jedisPool;
    private String appName;

    public RedisSnowflakeIdGenerator(JedisPool jedisPool, String appName) {
        this.jedisPool = jedisPool;
        this.appName = appName;
        init();
    }

    @Override
    protected long getWorkerId() {
        return serverNo;
    }

    private void init() {
        Jedis jedis = jedisPool.getResource();
        Object result = jedis.eval(SCRIPT_ID_AUTO_INCREMENT, 2, HASHKEY + ":" + appName, NONCE, AUTO_INCREMENT_PRIMARY_KEY, NetUtils.getMacAndIp());
        serverNo = Long.valueOf(String.valueOf(result));
        logger.info("获取当前服务器机器的serverNo:[{}]", serverNo);
    }
}

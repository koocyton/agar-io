package com.doopp.agar.server.configuration;

import com.doopp.agar.server.util.ShardedJedisHelper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import redis.clients.jedis.JedisPoolConfig;

public class RedisConfiguration {

    @Bean(name = "redisCache")
    public ShardedJedisHelper redisCache(@Value("${agar-server.redis.servers}") String redisServers, JedisPoolConfig jedisPoolConfig) {
        return new ShardedJedisHelper(redisServers, jedisPoolConfig);
    }

    @Bean
    public JedisPoolConfig jedisPoolConfig(@Value("${redis.pool.maxTotal}") int maxTotal,
                                           @Value("${redis.pool.maxIdle}") int maxIdle,
                                           @Value("${redis.pool.minIdle}") int minIdle,
                                           @Value("${redis.pool.maxWaitMillis}") int maxWaitMillis,
                                           @Value("${redis.pool.lifo}") boolean lifo,
                                           @Value("${redis.pool.testOnBorrow}") boolean testOnBorrow) {
        // Jedis池配置
        JedisPoolConfig config = new JedisPoolConfig();
        // 最大分配的对象数
        config.setMaxTotal(maxTotal);
        // 最大能够保持idel状态的对象数
        config.setMaxIdle(maxIdle);
        // 最小空闲的对象数。2.5.1以上版本有效
        config.setMinIdle(minIdle);
        // 当池内没有返回对象时，最大等待时间
        config.setMaxWaitMillis(maxWaitMillis);
        // 是否启用Lifo。如果不设置，默认为true。2.5.1以上版本有效
        config.setLifo(lifo);
        // 当调用borrow Object方法时，是否进行有效性检查
        config.setTestOnBorrow(testOnBorrow);
        // return
        return config;
    }

    //    @Bean
    //    public JedisConnectionFactory jedisConnectionFactory(@Qualifier("jedisPoolConfig") JedisPoolConfig jedisPoolConfig)
    //    {
    //        // JedisConnectionFactory setting
    //        JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory();
    //        jedisConnectionFactory.setPoolConfig(jedisPoolConfig);
    //        jedisConnectionFactory.setHostName("127.0.0.1");
    //        jedisConnectionFactory.setPort(6379);
    //        jedisConnectionFactory.setPassword("");
    //        jedisConnectionFactory.setDatabase(7);
    //        jedisConnectionFactory.setTimeout(2000);
    //        return jedisConnectionFactory;
    //    }

    //    @Bean
    //    public RedisTemplate templateRedis(@Qualifier("jedisConnectionFactory") JedisConnectionFactory connectionFactory)
    //    {
    //        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
    //        redisTemplate.setConnectionFactory(connectionFactory);
    //        redisTemplate.setKeySerializer(new StringRedisSerializer());
    //        redisTemplate.setValueSerializer(new JdkSerializationRedisSerializer());
    //        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
    //        return redisTemplate;
    //    }
}

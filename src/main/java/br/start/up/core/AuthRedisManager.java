package br.start.up.core;

import lombok.Builder;
import org.jspecify.annotations.Nullable;
import org.springframework.data.redis.cache.RedisCache;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import java.util.Map;


public class AuthRedisManager extends RedisCacheManager {

    public AuthRedisManager(RedisConnectionFactory connectionFactory,
                            RedisCacheConfiguration defaultConfig, Map<String, RedisCacheConfiguration> cacheConfigurationMap) {
        super(RedisCacheWriter.nonLockingRedisCacheWriter(connectionFactory), defaultConfig, cacheConfigurationMap);
    }

    @Override
    protected RedisCache createRedisCache(String name, @Nullable RedisCacheConfiguration cacheConfiguration) {
        return new AuthRedisCache(name, this.getCacheWriter(), cacheConfiguration);
    }
}

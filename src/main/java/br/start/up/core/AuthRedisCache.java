package br.start.up.core;

import br.start.up.dtos.IpcaResponseDTO;
import br.start.up.dtos.cache.AuthCacheDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.cache.RedisCache;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheWriter;

import java.math.BigDecimal;

public class AuthRedisCache extends RedisCache {

    private final ObjectMapper mapper = new ObjectMapper();

    protected AuthRedisCache(String name, RedisCacheWriter cacheWriter, RedisCacheConfiguration cacheConfiguration) {
        super(name, cacheWriter, cacheConfiguration);
    }

    @Override
    public ValueWrapper get(Object key) {
        ValueWrapper wrapper = super.get(key);
        if (wrapper == null) return null;

        Object value = wrapper.get();

        Object converted = switch (getName()) {
            case "user-auth-cache" -> mapper.convertValue(value, AuthCacheDTO.class);
            case "ipca" -> mapper.convertValue(value, IpcaResponseDTO.class);
            default     -> value;
        };
        return () -> converted;
    }
}

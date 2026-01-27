package br.start.up.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Random;

@Service
public class CodeResetPasswordService {

    private final String CARACTERES_SEQUENCE = "0123456789";
    private final String CODE_PREFIX = "code:";

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public void setCode(String email, String code) {
        redisTemplate.opsForValue().set("attempts:" + email, String.valueOf(0), Duration.ofMinutes(15));
        redisTemplate.opsForValue().set(CODE_PREFIX + email, code, Duration.ofMinutes(15));
    }

    public boolean verifyCode(String email, String code) {
        Long attempts = redisTemplate.opsForValue().increment("attempts:" + email);
        var redisCode = redisTemplate.opsForValue().get(CODE_PREFIX + email);

        if (redisCode != null && attempts <= 5 ) {
            return code.equals(redisCode);
        } else {
            redisTemplate.delete(CODE_PREFIX + email);
            redisTemplate.delete("attempts:" + email);
        }

        return false;
    }

    public String generate() {
        StringBuilder builder = new StringBuilder();
        Random rand = new Random();

        while (builder.length() < 6) {
            builder.append(CARACTERES_SEQUENCE.charAt(rand.nextInt(CARACTERES_SEQUENCE.length())));
        }

        return builder.toString();
    }
}

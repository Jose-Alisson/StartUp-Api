package br.start.up.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.temporal.WeekFields;

import static br.start.up.services.DashboardMetricsService.FEATURE_PREFIX;
import static br.start.up.ws.StompConnectionListener.PREFIX;
import static br.start.up.ws.StompConnectionListener.SESSION_PREFIX;

@Controller
public class StompController {

    @Autowired
    private RedisTemplate<String, Object> redis;

    @MessageMapping("/ping")
    public void ping(SimpMessageHeaderAccessor accessor){
        Principal principal = accessor.getUser();

        if(principal == null) return;

        redis.expire(
                SESSION_PREFIX + ":" + principal.getName(),
                Duration.ofSeconds(30)
        );
    }

    @MessageMapping("/feature/click/{businessesId}")
    public void feature(@DestinationVariable("businessesId") String businessesId){
        OffsetDateTime date = OffsetDateTime.now(ZoneId.of("America/Sao_Paulo"));
        int weekOfYear = date.get(WeekFields.ISO.weekOfWeekBasedYear());
        int year = date.get(WeekFields.ISO.weekBasedYear());

        String weekKey = year + "-W" + String.format("%02d", weekOfYear);

        redis.opsForZSet()
                .incrementScore(
                        FEATURE_PREFIX + ":click:week:" + weekKey,
                        "business:" + businessesId,
                        1
                );
    }
}

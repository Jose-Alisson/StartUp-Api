package br.start.up.ws;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import java.security.Principal;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@EnableAsync
@Configuration
@Component
public class StompConnectionListener {

    public static final String PREFIX = "startup:dashboard:metrics";

    public static final String SESSION_PREFIX = "startup:session";


    @Autowired
    private SimpMessagingTemplate messageTemplate;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @EventListener
    public void onConnect(SessionConnectedEvent session) {
        Principal user = session.getUser();
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(session.getMessage());
        String sessionId = accessor.getSessionId();

        if (sessionId == null || user == null) return;

        processarConnection(user.getName());
    }

    @EventListener
    public void onSubscribe(SessionSubscribeEvent event) {
        Principal user = event.getUser();
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());

        if (Objects.equals(accessor.getDestination(), "/user/queue/sessions")) {
            Authentication auth = (Authentication) user;

            if (auth.getAuthorities().stream().anyMatch(auto -> {
                var authority = auto.getAuthority();
                return authority.equals("admin") || authority.equals("manager");
            })) {
                processarSessionsSub(auth.getName());
            }
        }
    }

    @EventListener
    public void onDisconnect(SessionDisconnectEvent session) {}

    @Scheduled(fixedRate = 10000)
    public void cleanupSessions() {
        Set<Object> raw = redisTemplate.opsForSet().members(PREFIX + ":sessions");
        if (raw == null || raw.isEmpty()) return;

        Set<String> sessions = raw.stream()
                .map(e -> (String) e)
                .collect(Collectors.toSet());

        for (String sessionId : sessions) {
            Boolean alive = redisTemplate.hasKey(SESSION_PREFIX + ":" + sessionId);
            if (Boolean.FALSE.equals(alive)) {
                redisTemplate.opsForSet().remove(PREFIX + ":sessions", sessionId);

                var metrics = new HashMap<String, Object>();
                metrics.put("size", redisTemplate.opsForSet().size(PREFIX + ":sessions"));
                metrics.put("accesses", redisTemplate.opsForValue().get(PREFIX + ":accesses"));
                metrics.put("count", redisTemplate.opsForValue().get(PREFIX + ":count"));

                sendMetrics(metrics);
            }
        }
    }


    @Async
    public void processarConnection(String user) {
        redisTemplate.opsForValue().set(SESSION_PREFIX + ":" + user, "1", Duration.ofSeconds(30));
        redisTemplate.opsForValue().increment(PREFIX + ":accesses");
        redisTemplate.opsForSet().add(PREFIX + ":sessions", user);

        var metrics = new HashMap<String, Object>();
        metrics.put("size", redisTemplate.opsForSet().size(PREFIX + ":sessions"));
        metrics.put("accesses", redisTemplate.opsForValue().get(PREFIX + ":accesses"));
        metrics.put("count", redisTemplate.opsForValue().get(PREFIX + ":count"));

        sendMetrics(metrics);
    }

    @Async
    public void processarSessionsSub(String user){
        var metrics = new HashMap<>();
        metrics.put("size", redisTemplate.opsForSet().size(PREFIX + ":sessions"));
        metrics.put("accesses", redisTemplate.opsForValue().get(PREFIX + ":accesses"));
        metrics.put("count", redisTemplate.opsForValue().get(PREFIX + ":count"));

        messageTemplate.convertAndSendToUser(
                user,
                "/queue/sessions",
                metrics
        );
    }

    @Async
    public void sendMetrics(Map<String, Object> metrics) {
        messageTemplate.convertAndSend("/topic/admin", (Object) metrics);
        messageTemplate.convertAndSend("/topic/manager", (Object) metrics);
    }
}

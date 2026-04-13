package br.start.up.config;

import br.start.up.detail.UserAuthLoader;
import br.start.up.jwt.JwtService;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.annotation.web.socket.EnableWebSocketSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.messaging.access.intercept.MessageMatcherDelegatingAuthorizationManager;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer, ChannelInterceptor {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserAuthLoader authLoader;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws").setAllowedOrigins("*");
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic", "/queue");
        registry.setUserDestinationPrefix("/user");
        registry.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public @Nullable Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String auth = accessor.getFirstNativeHeader("Authorization");

            if (auth == null || !auth.startsWith("Bearer ")) {
                throw new AccessDeniedException("Sem token");
            }

            String token = auth.substring(7);
            var payload = jwtService.verify(token);

            var detail = authLoader.loadUserById(payload.getClaim("id").asLong());
            var user = new UsernamePasswordAuthenticationToken(detail.getPrincipal(), null, detail.getAuthorities().stream().map(SimpleGrantedAuthority::new).toList());
            accessor.setUser(user);
        }

        if(StompCommand.SUBSCRIBE.equals(accessor.getCommand())){
            Principal user = accessor.getUser();

            if (user == null) {
                throw new AccessDeniedException("Não autenticado");
            }

            String destination = accessor.getDestination();

            if(List.of("/topic/admin", "/topic/manager").contains(destination)){
                Authentication authentication = (Authentication) user;
                if(!includes(authentication, "admin") && !includes(authentication, "manager")){
                    throw new AccessDeniedException("Acesso negado");
                }
            }
        }

        return message;
    }

    private boolean includes(Authentication auth, String authority){
        return auth.getAuthorities()
                .stream()
                .anyMatch(aut -> aut.getAuthority().equals(authority));
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(this);
    }
}

package br.start.up.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.Map;

@Service
public class N8NService {

    private final WebClient n8nWebClient;

    public N8NService(
            @Value("${spring.n8n.user}") String user,
            @Value("${spring.n8n.password}") String password,
            @Value("${spring.n8n.host}") String host,
            @Value("${spring.n8n.port}") String port
            ){
        n8nWebClient = WebClient
                .builder()
                .baseUrl("http://%s:%s".formatted(host, port))
                .defaultHeaders(httpHeaders ->
                        httpHeaders.setBasicAuth(user, password)
                )
                .build();
    }

    public void send(String email, String username, String code) {
        Map<String, String> body = new HashMap<>();
        body.put("email", email);
        body.put("username", username);
        body.put("code", code);

        n8nWebClient.post()
                .uri("/webhook/send-code-to-email/")
                .bodyValue(body)
                .retrieve()
                .bodyToMono(String.class)
                .subscribe();
    }
}

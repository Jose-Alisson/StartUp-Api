package br.start.up.services;

import br.start.up.dtos.IpcaResponseDTO;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;

@Service
public class IndicadoresServices {

    private final WebClient webClient = WebClient.create();

    @Cacheable(value = "ipca", key = "'atual'")
    public IpcaResponseDTO getIPCA() {
        return webClient.get()
                .uri("https://api.bcb.gov.br/dados/serie/bcdata.sgs.13522/dados/ultimos/1?formato=json")
                .retrieve()
                .bodyToFlux(IpcaResponseDTO.class)
                .blockLast();
    }
}

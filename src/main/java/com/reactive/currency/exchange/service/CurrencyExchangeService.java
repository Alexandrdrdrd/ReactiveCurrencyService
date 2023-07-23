package com.reactive.currency.exchange.service;

import com.reactive.currency.exchange.entity.ExchangeRate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;


@Service
public class CurrencyExchangeService {
    @Value("${api.url.nbu}")
    private  String URL;


    private final WebClient webClient;

    @Autowired
    public CurrencyExchangeService(WebClient webClient) {
        this.webClient = webClient;
    }


    public Flux<ExchangeRate> getExchangeRate() {
        return webClient.get()
                .uri(URL)
                .retrieve()
                .bodyToFlux(ExchangeRate.class);
    }


}
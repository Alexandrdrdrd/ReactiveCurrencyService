package com.reactive.currency.exchange.service;

import com.reactive.currency.exchange.entity.ExchangeRate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;


@Service
public class CurrencyExchangeService {


    private final WebClient webClient;

    @Autowired
    public CurrencyExchangeService(WebClient webClient) {
        this.webClient = webClient;
    }


    public Flux<ExchangeRate> getExchangeRate() {
        return webClient.get()
                .uri("https://bank.gov.ua/NBUStatService/v1/statdirectory/exchange?json")
                .retrieve()
                .bodyToFlux(ExchangeRate.class);
    }


}
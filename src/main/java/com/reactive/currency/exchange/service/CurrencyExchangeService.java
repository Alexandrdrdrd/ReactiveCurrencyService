package com.reactive.currency.exchange.service;

import com.reactive.currency.exchange.entity.ExchangeRate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

/**
 * Service class that provides methods to retrieve currency exchange rates from an external API.
 */
@Service
public class CurrencyExchangeService {
    /**
     * The URL of the external API to fetch the exchange rates.
     */
    @Value("${api.url.nbu}")
    private  String URL;

    /**
     * WebClient instance to perform HTTP requests.
     */
    private final WebClient webClient;

    /**
     * Constructor for CurrencyExchangeService.
     *
     * @param webClient The WebClient instance to be used for making HTTP requests.
     */
    @Autowired
    public CurrencyExchangeService(WebClient webClient) {
        this.webClient = webClient;
    }

    /**
     * Fetches the currency exchange rates from the external API.
     *
     * @return Flux<ExchangeRate> - a reactive stream of ExchangeRate objects representing the retrieved exchange rates.
     */
    public Flux<ExchangeRate> getExchangeRate() {
        return webClient.get()
                .uri(URL)
                .retrieve()
                .bodyToFlux(ExchangeRate.class);
    }


}
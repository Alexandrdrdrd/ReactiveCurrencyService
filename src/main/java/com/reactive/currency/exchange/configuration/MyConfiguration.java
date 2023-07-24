package com.reactive.currency.exchange.configuration;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;


/**
 * Configuration class for creating the WebClient bean.
 */
@Configuration
public class MyConfiguration {

    /**
     * Creates an instance of WebClient.
     *
     * @return WebClient - an instance of WebClient for performing HTTP requests.
     */
    @Bean
    public WebClient webClient() {
        return WebClient.builder().build();
    }
}

package com.reactive.currency.exchange.controller;

import com.reactive.currency.exchange.entity.ExchangeRate;

import static org.assertj.core.api.Assertions.assertThat;

import com.reactive.currency.exchange.entity.FromToRate;
import com.reactive.currency.exchange.repository.ExchangeRatesRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@WebFluxTest(CurrencyExchangeController.class)
public class CurrencyExchangeControllerTests {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private ExchangeRatesRepository repository;

    private double getTestRateForCurrency(String currency) {

        if (currency.equals("USD")) {
            return 36.5686;
        } else if (currency.equals("EUR")) {
            return 40.6643;
        } else {
            return 0.0;
        }
    }


    private List<ExchangeRate> getTestExchangeRates() {
        List<ExchangeRate> exchangeRates = new ArrayList<>();
        exchangeRates.add(new ExchangeRate(36, "Австралійський долар", 24.629, "AUD", "24.07.2023"));
        exchangeRates.add(new ExchangeRate(124, "Канадський долар", 27.7255, "CAD", "24.07.2023"));

        return exchangeRates;
    }


    @Test
    public void testGetExchangeRatesForNonexistentCurrency() {
        String currency = "XYZ";
        when(repository.findLastByCc(currency)).thenReturn(Mono.empty());

        webTestClient.get().uri("/api/get-exchange-rates-for/" + currency)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    public void testGetAllExchangeRates() {
        List<ExchangeRate> exchangeRates = getTestExchangeRates();
        when(repository.findAll()).thenReturn(Flux.fromIterable(exchangeRates));

        List<ExchangeRate> expectedExchangeRates = getTestExchangeRates();

        webTestClient.get().uri("/api/exchange-rates")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(ExchangeRate.class)
                .value(responseExchangeRates -> {
                    assertThat(responseExchangeRates).containsExactlyInAnyOrderElementsOf(expectedExchangeRates);
                });
    }


    @Test
    public void testGetExchangeRatesForPair() {
        String fromCurrency = "USD";
        String toCurrency = "EUR";
        double fromRate = getTestRateForCurrency(fromCurrency);
        double toRate = getTestRateForCurrency(toCurrency);


        ExchangeRate fromExchangeRate = new ExchangeRate(1, "USD to Any Currency", fromRate, fromCurrency, "24.07.2023");
        ExchangeRate toExchangeRate = new ExchangeRate(2, "EUR to Any Currency", toRate, toCurrency, "24.07.2023");

        when(repository.findLastByCc(fromCurrency)).thenReturn(Mono.just(fromExchangeRate));
        when(repository.findLastByCc(toCurrency)).thenReturn(Mono.just(toExchangeRate));

        double expectedRate = fromRate / toRate;
        FromToRate expectedFromToRate = new FromToRate(fromCurrency, toCurrency, expectedRate);

        webTestClient.get().uri("/api/currency-exchange/from/" + fromCurrency + "/to/" + toCurrency)
                .exchange()
                .expectStatus().isOk()
                .expectBody(FromToRate.class)
                .value(responseFromToRate -> {
                    assertEquals(expectedFromToRate.getFrom(), responseFromToRate.getFrom());
                    assertEquals(expectedFromToRate.getTo(), responseFromToRate.getTo());
                    assertEquals(expectedFromToRate.getRate(), responseFromToRate.getRate(), 0.0001);
                });
    }


    @Test
    public void testGetExchangeRatesForInvalidCurrency() {
        String invalidCurrency = "INVALID";

        webTestClient.get().uri("/get-exchange-rates-for/" + invalidCurrency)
                .exchange()
                .expectStatus().isNotFound();
    }


}

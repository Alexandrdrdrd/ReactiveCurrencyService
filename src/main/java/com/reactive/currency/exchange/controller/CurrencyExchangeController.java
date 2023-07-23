package com.reactive.currency.exchange.controller;

import com.reactive.currency.exchange.entity.ExchangeRate;
import com.reactive.currency.exchange.entity.FromToRate;
import com.reactive.currency.exchange.repository.ExchangeRatesRepository;
import com.reactive.currency.exchange.service.CurrencyExchangeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@RestController
@RequestMapping("/api")
public class CurrencyExchangeController {


    private final ExchangeRatesRepository repository;


    @Autowired
    public CurrencyExchangeController(ExchangeRatesRepository repository) {
        this.repository = repository;
    }


    @GetMapping("/exchange-rates")
    public Flux<ExchangeRate> getAllExchangeRates() {
        return repository.findAll()
                .switchIfEmpty(Flux.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "No exchange rates found")));
    }


    @GetMapping("/get-exchange-rates-for/{currency}")
    public Flux<ExchangeRate> getExchangeRatesForCurrency(@PathVariable String currency) {
        if (currency.equals("UAH")) {
            return repository.findAll()
                    .switchIfEmpty(Flux.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "No exchange rates found")));
        } else {
            return repository.findDistinctFirstByCc(currency)
                    .flatMapMany(currencyRate -> repository.findAll()
                            .map(exchangeRate -> {
                                double updatedRate = currencyRate.getRate() / exchangeRate.getRate();
                                exchangeRate.setRate(updatedRate);
                                return exchangeRate;
                            }))
                    .switchIfEmpty(Flux.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "No exchange rates found")));
        }
    }


    @GetMapping("/currency-exchange/from/{from}/to/{to}")
    public Mono<FromToRate> getExchangeRatesForPair(@PathVariable String from, @PathVariable String to) {

        Mono<Double> rateForFromCurrencyMono = repository.findDistinctFirstByCc(from).map(ExchangeRate::getRate);
        Mono<Double> rateForToCurrencyMono = repository.findDistinctFirstByCc(to).map(ExchangeRate::getRate);

        if (from.equals(to)) {
            return Mono.just(new FromToRate(from, to, 1.0));
        } else if (from.equals("UAH")) {
            return rateForToCurrencyMono.map(rateTo -> new FromToRate(from, to, 1 / rateTo))
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "No exchange rates found")));
        } else if (to.equals("UAH")) {
            return rateForFromCurrencyMono.map(rateFrom -> new FromToRate(from, to, rateFrom))
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "No exchange rates found")));
        } else {
            return Mono.zip(rateForFromCurrencyMono, rateForToCurrencyMono, (rateFrom, rateTo) ->
                            new FromToRate(from, to, rateFrom / rateTo))
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "No exchange rates found")));
        }

    }


    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNotFoundException(ResponseStatusException ex) {
        return ex.getReason();
    }


}



package com.reactive.currency.exchange.controller;

import com.reactive.currency.exchange.entity.ExchangeRate;
import com.reactive.currency.exchange.entity.FromToRate;
import com.reactive.currency.exchange.repository.ExchangeRatesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * REST Controller class that handles currency exchange-related API endpoints.
 */
@RestController
@RequestMapping("/api")
public class CurrencyExchangeController {


    private final ExchangeRatesRepository repository;

    /**
     * Constructor for CurrencyExchangeController.
     *
     * @param repository The ExchangeRatesRepository instance used to access currency exchange rate data from the database.
     */
    @Autowired
    public CurrencyExchangeController(ExchangeRatesRepository repository) {
        this.repository = repository;
    }

    /**
     * Endpoint to retrieve all exchange rates available in the database.
     *
     * @return Flux of ExchangeRate containing all the exchange rates stored in the database.
     *         If no exchange rates are found, returns an error with HTTP status 404 (NOT_FOUND).
     */
    @GetMapping("/exchange-rates")
    public Flux<ExchangeRate> getAllExchangeRates() {
        return repository.findAll()
                .switchIfEmpty(Flux.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "No exchange rates found")));
    }

    /**
     * Endpoint to retrieve exchange rates for a specific currency (either to UAH or between two currencies).
     *
     * @param currency The currency code for which exchange rates are to be retrieved.
     * @return Flux of ExchangeRate containing the exchange rates based on the provided currency.
     *         If no exchange rates are found, returns an error with HTTP status 404 (NOT_FOUND).
     *         If the currency is "UAH," returns all exchange rates available in the database.
     *         If the currency is another valid currency code, returns the exchange rates relative to the specified currency.
     */
    @GetMapping("/get-exchange-rates-for/{currency}")
    public Flux<ExchangeRate> getExchangeRatesForCurrency(@PathVariable String currency) {
        if (currency.equals("UAH")) {
            return repository.findAll()
                    .switchIfEmpty(Flux.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "No exchange rates found")));
        } else {
            return repository.findLastByCc(currency)
                    .flatMapMany(currencyRate -> repository.findAll()
                            .map(exchangeRate -> {
                                double updatedRate = currencyRate.getRate() / exchangeRate.getRate();
                                exchangeRate.setRate(updatedRate);
                                return exchangeRate;
                            }))
                    .switchIfEmpty(Flux.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "No exchange rates found")));
        }
    }

    /**
     * Endpoint to calculate the exchange rate between two currencies.
     *
     * @param from The currency code to convert from.
     * @param to   The currency code to convert to.
     * @return Mono of FromToRate containing the exchange rate from the 'from' currency to the 'to' currency.
     *         If the 'from' currency is the same as the 'to' currency, returns a rate of 1.0.
     *         If no exchange rates are found for the provided currencies, returns an error with HTTP status 404 (NOT_FOUND).
     */
    @GetMapping("/currency-exchange/from/{from}/to/{to}")
    public Mono<FromToRate> getExchangeRatesForPair(@PathVariable String from, @PathVariable String to) {

        Mono<Double> rateForFromCurrencyMono = repository.findLastByCc(from).map(ExchangeRate::getRate);
        Mono<Double> rateForToCurrencyMono = repository.findLastByCc(to).map(ExchangeRate::getRate);

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

    /**
     * Exception handler method to handle ResponseStatusException with HTTP status 404 (NOT_FOUND).
     *
     * @param ex The ResponseStatusException that occurred.
     * @return A string representing the error message provided in the ResponseStatusException.
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNotFoundException(ResponseStatusException ex) {
        return ex.getReason();
    }


}



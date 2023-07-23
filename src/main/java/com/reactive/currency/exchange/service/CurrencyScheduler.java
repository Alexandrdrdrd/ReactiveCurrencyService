package com.reactive.currency.exchange.service;


import com.reactive.currency.exchange.repository.ExchangeRatesRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;


@Service
public class CurrencyScheduler {
    private final CurrencyExchangeService service;
    private final ExchangeRatesRepository repository;


    @Autowired
    public CurrencyScheduler(CurrencyExchangeService service, ExchangeRatesRepository repository) {
        this.service = service;
        this.repository = repository;
    }


    @PostConstruct
    public void executeOnStartup() {
        saveExchangeRate().subscribe();
        System.out.println("executeOnStartup");
    }

    @Scheduled(cron = "0 0 6 * * ?")
    public void executeDaily() {
        saveExchangeRate().subscribe();
    }

    public Mono<Void> saveExchangeRate() {
        return service.getExchangeRate()
                .collectList()
                .flatMap(exchangeRates -> repository.saveAll(exchangeRates).then());
    }
}

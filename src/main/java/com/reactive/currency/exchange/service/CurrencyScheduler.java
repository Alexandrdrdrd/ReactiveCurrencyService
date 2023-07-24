package com.reactive.currency.exchange.service;


import com.reactive.currency.exchange.repository.ExchangeRatesRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * Service class that schedules tasks to fetch and save currency exchange rates.
 */
@Service
public class CurrencyScheduler {
    /**
     * Service instance to fetch currency exchange rates from an external API.
     */
    private final CurrencyExchangeService service;
    /**
     * Repository instance to store currency exchange rates in the database.
     */
    private final ExchangeRatesRepository repository;


    /**
     * Constructor for CurrencyScheduler.
     *
     * @param service    The CurrencyExchangeService instance to fetch exchange rates from the external API.
     * @param repository The ExchangeRatesRepository instance to store exchange rates in the database.
     */
    @Autowired
    public CurrencyScheduler(CurrencyExchangeService service, ExchangeRatesRepository repository) {
        this.service = service;
        this.repository = repository;
    }

    /**
     * Executes the task to fetch and save exchange rates on application startup.
     * This method will be invoked once after the application context has been initialized.
     */
    @PostConstruct
    public void executeOnStartup() {
        saveExchangeRate().subscribe();
        System.out.println("executeOnStartup");
    }

    /**
     * Executes the task to fetch and save exchange rates daily at 6 AM.
     * This method will be automatically scheduled to run at the specified cron expression.
     */
    @Scheduled(cron = "0 0 6 * * ?")
    public void executeDaily() {
        saveExchangeRate().subscribe();
    }

    /**
     * Fetches currency exchange rates from the external API and saves them to the database.
     *
     * @return Mono<Void> - a reactive Mono that represents the completion of the task.
     */
    public Mono<Void> saveExchangeRate() {
        return service.getExchangeRate()
                .collectList()
                .flatMap(exchangeRates -> repository.saveAll(exchangeRates).then());
    }
}

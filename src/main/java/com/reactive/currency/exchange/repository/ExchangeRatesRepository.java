package com.reactive.currency.exchange.repository;

import com.reactive.currency.exchange.entity.ExchangeRate;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


/**
 * Repository interface for interacting with the 'exchange_rates' table in the database.
 * This interface extends the ReactiveCrudRepository interface, providing basic CRUD operations for the 'exchange_rates' table.
 */
@Repository
public interface ExchangeRatesRepository extends R2dbcRepository<ExchangeRate, Integer> {

    /**
     * Retrieve all exchange rates from the 'exchange_rates' table, ordered by id in descending order, with a limit of 61 elements.
     *
     * @return Flux<ExchangeRate> - a reactive stream of ExchangeRate objects representing the retrieved exchange rates.
     */
    @Query("SELECT * FROM exchange_rates ORDER BY id DESC LIMIT 61")
    Flux<ExchangeRate> findAll();

    /**
     * Retrieve the last exchange rate with the specified currency code (cc) from the 'exchange_rates' table, ordered by id in descending order.
     *
     * @param cc The currency code for which the exchange rate is to be retrieved.
     * @return Mono<ExchangeRate> - a reactive stream of ExchangeRate object representing the retrieved exchange rate.
     */
    @Query("SELECT * FROM exchange_rates WHERE cc = :cc ORDER BY id DESC LIMIT 1")
    Mono<ExchangeRate> findLastByCc(String cc);
}

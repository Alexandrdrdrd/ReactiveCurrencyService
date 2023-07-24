package com.reactive.currency.exchange.repository;

import com.reactive.currency.exchange.entity.ExchangeRate;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Repository
public interface ExchangeRatesRepository extends ReactiveCrudRepository<ExchangeRate, Integer> {

    @Query("SELECT * FROM exchange_rates ORDER BY id DESC LIMIT 61")
    Flux<ExchangeRate> findAll();

    @Query("SELECT * FROM exchange_rates WHERE cc = :cc ORDER BY id DESC LIMIT 1")
    Mono<ExchangeRate> findLastByCc(String cc);
}

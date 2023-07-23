package com.reactive.currency.exchange.repository;

import com.reactive.currency.exchange.entity.ExchangeRate;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;


@Repository
public interface ExchangeRatesRepository extends ReactiveCrudRepository<ExchangeRate, Integer> {

    Mono<ExchangeRate> findDistinctFirstByCc(String cc);
}

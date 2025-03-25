package com.onelab.users_service.repository.jpa;

import com.onelab.users_service.entity.ExchangeRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ExchangeRateRepository extends JpaRepository<ExchangeRate, Long> {
    Optional<ExchangeRate> findExchangeRateByCurrencyPair(String currencyPair);
}

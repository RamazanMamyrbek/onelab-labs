package com.onelab.users_service.service;

import org.onelab.common.dto.response.ExchangeRatesResponseDto;

import java.math.BigDecimal;

public interface ExchangeRateService {
    BigDecimal toUsd(BigDecimal sum, String currencyShortname);

    ExchangeRatesResponseDto updateExchangeRates() throws InterruptedException;
}
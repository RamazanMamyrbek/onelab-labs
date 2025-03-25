package com.onelab.users_service.service.impl;

import com.onelab.users_service.entity.ExchangeRate;
import com.onelab.users_service.repository.jpa.ExchangeRateRepository;
import com.onelab.users_service.service.ExchangeRateService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.onelab.common.dto.response.ExchangeRatesResponseDto;
import org.onelab.common.exception.ResourceNotFoundException;
import org.onelab.common.feign.ExchangeRateFeignClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Map;

@Service
@Transactional(readOnly = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class ExchangeRateServiceImpl implements ExchangeRateService {
    final ExchangeRateRepository exchangeRateRepository;
    final ExchangeRateFeignClient exchangeRateFeignClient;
    @Value("${openexchangerates.appId}")
    private String appId;

    @Override
    public BigDecimal toUsd(BigDecimal sum, String currencyShortname) {
        String currencyPair = "USD/"+currencyShortname;
        ExchangeRate exchangeRate = exchangeRateRepository.findExchangeRateByCurrencyPair(currencyPair)
                .orElseThrow(() -> ResourceNotFoundException.exchangeRateNotFound(currencyPair));
        double sumUsd = sum.doubleValue() * exchangeRate.getRate();
        return new BigDecimal(sumUsd);
    }

    @Override
    @Transactional
    @Scheduled(cron = "0 0 0 * * *")
    public ExchangeRatesResponseDto updateExchangeRates() throws InterruptedException {
        truncateTable();
        ExchangeRatesResponseDto exchangeRatesResponseDto = exchangeRateFeignClient.updateExchangeRates(appId);
        if(exchangeRatesResponseDto != null) {
            for(Map.Entry<String, Double> rate: exchangeRatesResponseDto.rates().entrySet()) {
                ExchangeRate exchangeRateToUsd = new ExchangeRate();
                exchangeRateToUsd.setCurrencyPair(rate.getKey()+"/USD");
                exchangeRateToUsd.setRate(rate.getValue());


                ExchangeRate exchangeRateFromUsd = new ExchangeRate();
                exchangeRateFromUsd.setCurrencyPair("USD/" + rate.getKey());
                exchangeRateFromUsd.setRate(1.0 / rate.getValue());

                exchangeRateRepository.save(exchangeRateToUsd);
                if(!exchangeRateFromUsd.getCurrencyPair().equals("USD/USD"))
                    exchangeRateRepository.save(exchangeRateFromUsd);
            }
        }
        return exchangeRatesResponseDto;
    }

    @Transactional
    protected void truncateTable() {
        exchangeRateRepository.deleteAll();
    }
}

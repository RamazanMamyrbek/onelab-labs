package com.onelab.course_service.junit.services;

import com.onelab.users_service.entity.ExchangeRate;
import com.onelab.users_service.repository.jpa.ExchangeRateRepository;
import com.onelab.users_service.service.impl.ExchangeRateServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.onelab.common.dto.response.ExchangeRatesResponseDto;
import org.onelab.common.exception.ResourceNotFoundException;
import org.onelab.common.feign.ExchangeRateFeignClient;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExchangeRateServiceTest {

    @Mock
    private ExchangeRateRepository exchangeRateRepository;

    @Mock
    private ExchangeRateFeignClient exchangeRateFeignClient;

    @InjectMocks
    private ExchangeRateServiceImpl exchangeRateService;

    @Test
    void toUsd_ShouldConvertCurrencyToUsd() {
        ExchangeRate exchangeRate = new ExchangeRate();
        exchangeRate.setCurrencyPair("USD/EUR");
        exchangeRate.setRate(0.85);
        when(exchangeRateRepository.findExchangeRateByCurrencyPair("USD/EUR"))
                .thenReturn(Optional.of(exchangeRate));

        BigDecimal result = exchangeRateService.toUsd(new BigDecimal("100"), "EUR");

        assertEquals(new BigDecimal("85.00"), result.setScale(2));
    }

    @Test
    void toUsd_WhenRateNotFound_ShouldThrowException() {
        when(exchangeRateRepository.findExchangeRateByCurrencyPair("USD/XYZ"))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> exchangeRateService.toUsd(new BigDecimal("100"), "XYZ"));
    }


}
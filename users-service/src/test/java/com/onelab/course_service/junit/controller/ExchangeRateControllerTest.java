package com.onelab.course_service.junit.controller;

import com.onelab.users_service.controller.ExchangeRateController;
import com.onelab.users_service.service.ExchangeRateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.onelab.common.dto.response.ExchangeRatesResponseDto;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ExchangeRateControllerTest {

    @Mock
    private ExchangeRateService exchangeRateService;

    @InjectMocks
    private ExchangeRateController exchangeRateController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void updateExchangeRates_ShouldReturnUpdatedRates() throws InterruptedException {
        ExchangeRatesResponseDto responseDto = new ExchangeRatesResponseDto(
                "Usage subject to terms: https://example.com",
                "Open Exchange License",
                System.currentTimeMillis(),
                "USD",
                Map.of("EUR", 0.92, "GBP", 0.79)
        );

        when(exchangeRateService.updateExchangeRates()).thenReturn(responseDto);

        ResponseEntity<ExchangeRatesResponseDto> response = exchangeRateController.updateExchangeRates();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(responseDto, response.getBody());
        verify(exchangeRateService, times(1)).updateExchangeRates();
    }
}

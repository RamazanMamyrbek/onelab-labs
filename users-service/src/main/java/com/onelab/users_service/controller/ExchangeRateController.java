package com.onelab.users_service.controller;

import com.onelab.users_service.service.ExchangeRateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.onelab.common.dto.response.ExchangeRatesResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/exchangerates")
@RequiredArgsConstructor
@Tag(name = "ExchangeRateController", description = "Endpoints for Exchange Rates management")
public class ExchangeRateController {
    private final ExchangeRateService exchangeRateService;

    @PutMapping("/exchange-rates")
    @Operation(summary = "Update exchange rates manually", description = "This endpoint is for updating exchange rates in database.By default it will be updated automatically every day at 00:00")
    public ResponseEntity<ExchangeRatesResponseDto> updateExchangeRates() throws InterruptedException {
        return ResponseEntity.ok(exchangeRateService.updateExchangeRates());
    }
}

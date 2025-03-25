package org.onelab.common.feign;

import org.onelab.common.dto.response.ExchangeRatesResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "exchangeRateFeignClient", url = "https://openexchangerates.org/api")
public interface ExchangeRateFeignClient {

    @GetMapping("/latest.json")
    ExchangeRatesResponseDto updateExchangeRates(@RequestParam("app_id") String appId);
}
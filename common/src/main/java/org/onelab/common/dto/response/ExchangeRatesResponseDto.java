package org.onelab.common.dto.response;

import java.util.Map;

public record ExchangeRatesResponseDto(
        String disclaimer,
        String license,
        long timestamp,
        String base,
        Map<String, Double>rates
) {
}

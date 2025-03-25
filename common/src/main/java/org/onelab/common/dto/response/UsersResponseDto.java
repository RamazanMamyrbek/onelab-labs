package org.onelab.common.dto.response;

import java.math.BigDecimal;

public record UsersResponseDto(
        Long id,
        String email,
        String name,
        String country,
        Long age,
        String role,
        BigDecimal balance,
        String balanceCurrency
) {
}

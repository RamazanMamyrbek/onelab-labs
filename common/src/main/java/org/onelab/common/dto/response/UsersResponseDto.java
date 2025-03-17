package org.onelab.common.dto.response;

public record UsersResponseDto(
        Long id,
        String email,
        String name,
        String country,
        Long age,
        String role
) {
}

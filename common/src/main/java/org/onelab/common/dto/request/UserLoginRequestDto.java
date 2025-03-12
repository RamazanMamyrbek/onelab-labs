package org.onelab.common.dto.request;

public record UserLoginRequestDto(
        String email,
        String password
) {
}

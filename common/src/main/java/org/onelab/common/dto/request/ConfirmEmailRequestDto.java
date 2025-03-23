package org.onelab.common.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ConfirmEmailRequestDto(
        @Email(message = "Invalid email format")
        @NotBlank(message = "Email should not be blank")
        String email,
        @NotBlank(message = "Confirmation code should not be blank")
        String code
) {
}
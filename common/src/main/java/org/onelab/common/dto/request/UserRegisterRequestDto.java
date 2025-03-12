package org.onelab.common.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.onelab.common.enums.Role;

public record UserRegisterRequestDto(
        @NotBlank
        @Email(message = "Should be email")
        String email,
        @NotBlank(message = "Password cannot be blank")
        @Size(min = 6, max = 50, message = "Password should be between 6 and 50 characters")
        String password,

        @NotBlank(message = "Name should not be blank")
        String name,
        Role role
) {
}

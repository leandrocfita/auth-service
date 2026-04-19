package br.com.fiap.auth_service.adapter.input.web.dto;

import jakarta.validation.constraints.NotBlank;

public record ChangePasswordRequestDto(

        @NotBlank
        String oldPassword,

        @NotBlank
        String newPassword
) {
}

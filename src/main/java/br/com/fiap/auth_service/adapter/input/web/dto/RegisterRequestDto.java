package br.com.fiap.auth_service.adapter.input.web.dto;


import jakarta.validation.constraints.NotEmpty;

public record RegisterRequestDto(
        @NotEmpty
        String login,

        @NotEmpty
        String password,

        @NotEmpty
        String externalId
) {
}

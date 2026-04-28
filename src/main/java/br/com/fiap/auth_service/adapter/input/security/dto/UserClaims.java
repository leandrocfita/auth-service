package br.com.fiap.auth_service.adapter.input.security.dto;

import java.util.UUID;

public record UserClaims(
        UUID externalId,
        String login,
        UUID authId
) {
}

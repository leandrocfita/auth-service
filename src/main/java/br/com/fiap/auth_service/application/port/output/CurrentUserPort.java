package br.com.fiap.auth_service.application.port.output;

import br.com.fiap.auth_service.adapter.input.security.dto.UserClaims;

public interface CurrentUserPort {

    UserClaims getClaims();
}

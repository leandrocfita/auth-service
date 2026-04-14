package br.com.fiap.auth_service.application.port.output;

import br.com.fiap.auth_service.domain.AuthUser;

public interface TokenGeneratorPort {
    String generate(AuthUser user);
}

package br.com.fiap.auth_service.application.port.output;

import br.com.fiap.auth_service.domain.AuthUser;

import java.util.Optional;

public interface AuthUserRepositoryPort {
    Optional<AuthUser> findByLogin(String login);
    AuthUser save(AuthUser authUser);
}

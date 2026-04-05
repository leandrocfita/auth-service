package br.com.fiap.auth_service.adapter.output.persistence;

import br.com.fiap.auth_service.application.port.output.AuthUserRepositoryPort;
import br.com.fiap.auth_service.domain.AuthUser;
import br.com.fiap.auth_service.persistence.AuthUserJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class AuthUserRepository implements AuthUserRepositoryPort {

    private final AuthUserJpaRepository repository;

    public AuthUserRepository(AuthUserJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<AuthUser> findByLogin(String login) {
        return repository.findByLogin(login);
    }

    @Override
    public AuthUser save(AuthUser authUser) {
        return repository.save(authUser);
    }
}

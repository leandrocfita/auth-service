package br.com.fiap.auth_service.persistence;

import br.com.fiap.auth_service.domain.AuthUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AuthUserJpaRepository extends JpaRepository<AuthUser, UUID> {
    Optional<AuthUser> findByLogin(String login);
    AuthUser save(AuthUser authUser);
    Optional<AuthUser> findByExternalId(String externalId);
}

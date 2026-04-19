package br.com.fiap.auth_service.application.service;

import br.com.fiap.auth_service.adapter.input.security.dto.UserClaims;
import br.com.fiap.auth_service.application.port.input.ChangePasswordUseCase;
import br.com.fiap.auth_service.application.port.input.LoginUseCase;
import br.com.fiap.auth_service.application.port.input.RegisterUserUseCase;
import br.com.fiap.auth_service.application.port.output.AuthUserRepositoryPort;
import br.com.fiap.auth_service.application.port.output.CurrentUserPort;
import br.com.fiap.auth_service.application.port.output.PasswordEncoderPort;
import br.com.fiap.auth_service.application.port.output.TokenGeneratorPort;
import br.com.fiap.auth_service.domain.AuthUser;
import br.com.fiap.auth_service.domain.exceptions.LoginAlreadyExistsException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@Transactional
public class AuthService implements
        RegisterUserUseCase,
        LoginUseCase,
        ChangePasswordUseCase {

    private final AuthUserRepositoryPort repository;
    private final PasswordEncoderPort passwordEncoder;
    private final TokenGeneratorPort tokenGenerator;
    private final CurrentUserPort currentUser;

    public AuthService(
            AuthUserRepositoryPort repository,
            PasswordEncoderPort passwordEncoder,
            TokenGeneratorPort tokenGenerator,
            CurrentUserPort currentUser
    ) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.tokenGenerator = tokenGenerator;
        this.currentUser = currentUser;
    }

    @Override
    public String login(String login, String password) {

        AuthUser user = recoverAndValidateUser(login, password);

        return tokenGenerator.generate(user);
    }

    private AuthUser recoverAndValidateUser(String login, String password) {
        AuthUser user = repository.findByLogin(login).orElseThrow(
                () -> new RuntimeException("User not found")
        );

        if(!user.isActive()){
            throw new RuntimeException("User is not active");
        }

        if(!passwordEncoder.matches(password, user.getPassword())){
            throw new RuntimeException("Passwords don't match");
        }
        return user;
    }

    @Override
    public UUID register(String login, String password, String externalId) {

        log.info("Registering login: {} | externalId: {}", login, externalId);

        Optional<AuthUser> existing = repository.findByExternalId(externalId);

        if(existing.isPresent()){
            UUID authId = existing.get().getId();

            log.info("User already exists, idempotent flow returning authId: {}", authId.toString());

            return authId;
        }

        repository.findByLogin(login).ifPresent(authUser -> {
            log.info("User already exists, cannot register. Login: {}", login);
            throw new LoginAlreadyExistsException();
        });

        AuthUser.validatePassword(password);
        AuthUser authUser = AuthUser.newUser(
                login,
                passwordEncoder.encode(password),
                externalId
        );

        UUID id = repository.save(authUser).getId();

        log.info("New user registered, authId: {}", id.toString());

        return id;
    }

    @Override
    public void changePassword(String oldPassword, String newPassword) {
        UserClaims claims = currentUser.getClaims();

        AuthUser user = recoverAndValidateUser(claims.login(), oldPassword);

        AuthUser.validatePassword(newPassword);

        user.changePassword(passwordEncoder.encode(newPassword));

        repository.save(user);
    }
}

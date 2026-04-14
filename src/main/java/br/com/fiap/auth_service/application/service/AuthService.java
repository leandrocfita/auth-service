package br.com.fiap.auth_service.application.service;

import br.com.fiap.auth_service.application.port.input.LoginUseCase;
import br.com.fiap.auth_service.application.port.input.RegisterUserUseCase;
import br.com.fiap.auth_service.application.port.output.AuthUserRepositoryPort;
import br.com.fiap.auth_service.application.port.output.PasswordEncoderPort;
import br.com.fiap.auth_service.application.port.output.TokenGeneratorPort;
import br.com.fiap.auth_service.domain.AuthUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class AuthService implements RegisterUserUseCase, LoginUseCase {

    private final AuthUserRepositoryPort repository;
    private final PasswordEncoderPort passwordEncoder;
    private final TokenGeneratorPort tokenGenerator;

    public AuthService(
            AuthUserRepositoryPort repository,
            PasswordEncoderPort passwordEncoder,
            TokenGeneratorPort tokenGenerator
    ) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.tokenGenerator = tokenGenerator;
    }

    @Override
    public String login(String login, String password) {

        AuthUser user = repository.findByLogin(login).orElseThrow(
                () -> new RuntimeException("User not found")
        );

        if(!user.isActive()){
            throw new RuntimeException("User is not active");
        }

        if(!passwordEncoder.matches(password, user.getPassword())){
            throw new RuntimeException("Passwords don't match");
        }

        return tokenGenerator.generate(user);
    }

    @Override
    public UUID register(String login, String password) {

        repository.findByLogin(login).ifPresent(authUser -> {
            throw new IllegalArgumentException("Login already exists");
        });

        AuthUser authUser = AuthUser.newUser(
            login, passwordEncoder.encode(password)
        );

        return repository.save(authUser).getId();
    }

}

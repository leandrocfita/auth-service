package br.com.fiap.auth_service.application.port.input;


import java.util.UUID;

public interface RegisterUserUseCase {
    UUID register(String login, String password, String externalId);
}

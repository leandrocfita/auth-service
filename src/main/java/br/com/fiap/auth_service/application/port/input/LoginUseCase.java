package br.com.fiap.auth_service.application.port.input;

public interface LoginUseCase {
    String login(String login, String password);
}

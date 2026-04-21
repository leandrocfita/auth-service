package br.com.fiap.auth_service.application.port.output;

public interface PasswordEncoderPort {
    String encode(String raw);
    boolean matches(String password, String encodedPassword);
}

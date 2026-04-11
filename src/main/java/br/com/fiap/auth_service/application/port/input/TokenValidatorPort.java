package br.com.fiap.auth_service.application.port.input;

import org.springframework.security.oauth2.jwt.Jwt;

public interface TokenValidatorPort {

    Jwt validate(String token);
}

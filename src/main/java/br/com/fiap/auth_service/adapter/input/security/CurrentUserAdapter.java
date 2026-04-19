package br.com.fiap.auth_service.adapter.input.security;

import br.com.fiap.auth_service.adapter.input.security.dto.UserClaims;
import br.com.fiap.auth_service.application.port.output.CurrentUserPort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CurrentUserAdapter implements CurrentUserPort {

    @Override
    public UserClaims getClaims(){
        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return new UserClaims(
                UUID.fromString(jwt.getSubject()),
                jwt.getClaim("login").toString(),
                UUID.fromString(jwt.getClaim("authId").toString())
        );
    }

}

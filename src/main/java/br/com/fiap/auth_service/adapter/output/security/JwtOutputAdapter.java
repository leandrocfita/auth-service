package br.com.fiap.auth_service.adapter.output.security;

import br.com.fiap.auth_service.application.port.output.RsaKeyProviderPort;
import br.com.fiap.auth_service.application.port.output.TokenGeneratorPort;
import br.com.fiap.auth_service.domain.AuthUser;
import br.com.fiap.auth_service.security.JwtProperties;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;

@Service
public class JwtOutputAdapter implements TokenGeneratorPort {

    private final RsaKeyProviderPort keys;
    private long expiration;

    public JwtOutputAdapter(RsaKeyProviderPort keys, JwtProperties jwtProperties) {
        this.keys = keys;
        this.expiration = jwtProperties.getExpiration();
    }

    public String generate(AuthUser user) {
        Instant now = Instant.now();

        return Jwts.builder()
                .subject(user.getId().toString())
                .claim("login", user.getLogin())
                .claim("ver", user.getTokenVersion())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(expiration)))
                .signWith(keys.privateKey())
                .compact();

    }
}

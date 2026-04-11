package br.com.fiap.auth_service.adapter.output.security;

import br.com.fiap.auth_service.application.port.input.TokenValidatorPort;
import br.com.fiap.auth_service.application.port.output.RsaKeyProviderPort;
import br.com.fiap.auth_service.application.port.output.TokenGeneratorPort;
import br.com.fiap.auth_service.domain.AuthUser;
import br.com.fiap.auth_service.security.JwtProperties;
import br.com.fiap.auth_service.adapter.output.security.RsaKeyProviderAdapter;
import io.jsonwebtoken.Jwts;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;

@Service
public class JwtAdapter implements TokenGeneratorPort, TokenValidatorPort {

    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;
    private long expiration;

    private static final String ISSUER = "auth-service";

    public JwtAdapter(
            JwtEncoder jwtEncoder,
            JwtProperties jwtProperties,
            JwtDecoder jwtDecoder) {
        this.jwtEncoder = jwtEncoder;
        this.jwtDecoder = jwtDecoder;
        this.expiration = jwtProperties.getExpiration();
    }

    public String generate(AuthUser user) {
        Instant now = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .subject(user.getId().toString())
                .issuer(ISSUER)
                .claim("login", user.getLogin())
                .claim("ver", user.getTokenVersion())
                .issuedAt(now)
                .expiresAt(now.plusSeconds(expiration))
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims))
                .getTokenValue();
    }

    @Override
    public Jwt validate(String token) {
        return jwtDecoder.decode(token);
    }
}

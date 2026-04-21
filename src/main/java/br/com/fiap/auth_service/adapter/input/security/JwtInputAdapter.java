package br.com.fiap.auth_service.adapter.input.security;

import br.com.fiap.auth_service.application.port.output.TokenGeneratorPort;
import br.com.fiap.auth_service.domain.AuthUser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class JwtInputAdapter implements TokenGeneratorPort{

    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;
    private long expiration;

    @Value("${jwt.issuer}")
    private String ISSUER;

    public JwtInputAdapter(
            JwtEncoder jwtEncoder,
            JwtInputProperties jwtProperties,
            JwtDecoder jwtDecoder) {
        this.jwtEncoder = jwtEncoder;
        this.jwtDecoder = jwtDecoder;
        this.expiration = jwtProperties.getExpiration();
    }

    public String generate(AuthUser user) {
        Instant now = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .subject(user.getExternalId())
                .issuer(ISSUER)
                .claim("login", user.getLogin())
                .claim("ver", user.getTokenVersion())
                .claim("authId", user.getId().toString())
                .issuedAt(now)
                .expiresAt(now.plusSeconds(expiration))
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims))
                .getTokenValue();
    }

}

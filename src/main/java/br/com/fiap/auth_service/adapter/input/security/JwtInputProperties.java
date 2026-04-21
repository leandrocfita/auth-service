package br.com.fiap.auth_service.adapter.input.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
@ConfigurationProperties(prefix = "jwt")
@Component
@Getter
@Setter
public class JwtInputProperties {

    private long expiration;
}

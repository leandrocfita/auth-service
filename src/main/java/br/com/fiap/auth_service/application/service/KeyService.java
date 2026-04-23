package br.com.fiap.auth_service.application.service;

import br.com.fiap.auth_service.application.port.input.GetPublicKeyUseCase;
import com.nimbusds.jose.jwk.JWKSet;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class KeyService implements GetPublicKeyUseCase {

    private final JWKSet jwkSet;

    public KeyService(JWKSet jwkSet) {
        this.jwkSet = jwkSet;
    }

    @Override
    public Map<String, Object> getKey() {
        return jwkSet.toPublicJWKSet().toJSONObject();
    }


}

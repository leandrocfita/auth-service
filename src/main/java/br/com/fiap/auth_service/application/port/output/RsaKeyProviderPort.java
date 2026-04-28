package br.com.fiap.auth_service.application.port.output;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

public interface RsaKeyProviderPort {
    RSAPrivateKey privateKey();
    RSAPublicKey publicKey();
}

package br.com.fiap.auth_service.adapter.input.security;

import br.com.fiap.auth_service.application.port.output.RsaKeyProviderPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Slf4j
@Component
public class RsaKeyProviderAdapter implements RsaKeyProviderPort {

    private RSAPrivateKey privateKey;
    private RSAPublicKey publicKey;

    public RsaKeyProviderAdapter(
            // Se a chave não for encontrada, ele usa "/app/keys/private.pem" por padrão
            @Value("${app.security.rsa.private-key-path:/app/keys/private.pem}") String privatePath,
            @Value("${app.security.rsa.public-key-path:/app/keys/public.pem}") String publicPath
    ) throws Exception {
        this.privateKey = loadPrivateKey(privatePath);
        this.publicKey = loadPublicKey(publicPath);
    }

    @Override
    public RSAPrivateKey privateKey() {
        return privateKey;
    }

    @Override
    public RSAPublicKey publicKey() {
        return publicKey;
    }

    private RSAPrivateKey loadPrivateKey(String privatePath) throws Exception {

        log.info("Loading private key from {}", privatePath);

        String key = readKey(privatePath)
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");

        byte[] decoded = Base64.getDecoder().decode(key);

        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decoded);

        KeyFactory factory = KeyFactory.getInstance("RSA");

        return (RSAPrivateKey) factory.generatePrivate(spec);
    }

    private RSAPublicKey loadPublicKey(String publicKeyPath) throws Exception {

        log.info("Loading public key from {}", publicKeyPath);

        String key = readKey(publicKeyPath)
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", "");

        byte[] decoded = Base64.getDecoder().decode(key);

        X509EncodedKeySpec spec = new X509EncodedKeySpec(decoded);

        KeyFactory factory = KeyFactory.getInstance("RSA");

        return (RSAPublicKey) factory.generatePublic(spec);
    }

    private String readKey(String path) throws Exception {


        Path filePath = Path.of(path);

        return Files.readString(filePath);
    }
}

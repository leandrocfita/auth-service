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

    @Value("${app.security.rsa.private-key-path}")
    public String PRIVATE_KEY_PATH;
    @Value("${app.security.rsa.public-key-path}")
    public String PUBLIC_KEY_PATH;

    private RSAPrivateKey privateKey;
    private RSAPublicKey publicKey;

    public RsaKeyProviderAdapter() throws Exception {
     this.privateKey = loadPrivateKey();
     this.publicKey = loadPublicKey();
    }

    @Override
    public RSAPrivateKey privateKey() {
        return privateKey;
    }

    @Override
    public RSAPublicKey publicKey() {
        return publicKey;
    }

    private RSAPrivateKey loadPrivateKey() throws Exception {

        log.info("Loading private key from {}", this.PRIVATE_KEY_PATH);

        String key = readKey(PRIVATE_KEY_PATH)
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");

        byte[] decoded = Base64.getDecoder().decode(key);

        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decoded);

        KeyFactory factory = KeyFactory.getInstance("RSA");

        return (RSAPrivateKey) factory.generatePrivate(spec);
    }

    private RSAPublicKey loadPublicKey() throws Exception {

        log.info("Loading public key from {}", this.PUBLIC_KEY_PATH);

        String key = readKey(PUBLIC_KEY_PATH)
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", "");

        byte[] decoded = Base64.getDecoder().decode(key);

        X509EncodedKeySpec spec = new X509EncodedKeySpec(decoded);

        KeyFactory factory = KeyFactory.getInstance("RSA");

        return (RSAPublicKey) factory.generatePublic(spec);
    }

    private String readKey(String path) throws Exception {


        log.info("The path thas is send to the method is {}", path);

        Path filePath = Path.of(path);

        return Files.readString(filePath);
    }
}

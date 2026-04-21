package br.com.fiap.auth_service.adapter.input.security;

import br.com.fiap.auth_service.application.port.output.RsaKeyProviderPort;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Component
public class RsaKeyProviderAdapter implements RsaKeyProviderPort {

    public static final String PRIVATE_KEY_PATH = "keys/private.pem";
    public static final String PUBLIC_KEY_PATH = "keys/public.pem";

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

        Path filePath = Path.of(path);

        return Files.readString(filePath);
    }
}

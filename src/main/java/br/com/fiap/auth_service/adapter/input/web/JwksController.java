package br.com.fiap.auth_service.adapter.input.web;

import br.com.fiap.auth_service.application.port.input.GetPublicKeyUseCase;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/.well-known")
public class JwksController {

    private final GetPublicKeyUseCase getPublicKeyUseCase;

    public  JwksController(GetPublicKeyUseCase getPublicKeyUseCase) {
        this.getPublicKeyUseCase = getPublicKeyUseCase;
    }

    @GetMapping("/jwks.json")
    public Map<String, Object> getPublicKey() {
        return getPublicKeyUseCase.getKey();
    }
}

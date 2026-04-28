package br.com.fiap.auth_service.application.port.input;

import java.util.Map;

public interface GetPublicKeyUseCase {
    Map<String, Object> getKey();
}

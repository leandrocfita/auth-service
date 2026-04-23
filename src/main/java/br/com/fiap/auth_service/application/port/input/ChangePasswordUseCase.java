package br.com.fiap.auth_service.application.port.input;

public interface ChangePasswordUseCase {

    void changePassword(String oldPassword, String newPassword);
}

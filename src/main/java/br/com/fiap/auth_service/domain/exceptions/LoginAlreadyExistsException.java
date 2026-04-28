package br.com.fiap.auth_service.domain.exceptions;

public class LoginAlreadyExistsException extends RuntimeException{

    public LoginAlreadyExistsException(){
        super("Login already exists");
    }
}

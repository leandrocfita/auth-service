package br.com.fiap.auth_service.domain.exceptions;

public class InvalidPasswordException extends  RuntimeException{
    public InvalidPasswordException(String msg){
        super(msg);
    }
}

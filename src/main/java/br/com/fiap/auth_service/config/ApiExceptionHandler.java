package br.com.fiap.auth_service.config;

import br.com.fiap.auth_service.domain.exceptions.InvalidPasswordException;
import br.com.fiap.auth_service.domain.exceptions.LoginAlreadyExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(LoginAlreadyExistsException.class)
    public ProblemDetail handleLoginAlreadyExists(
            LoginAlreadyExistsException ex
    ) {

        ProblemDetail problem =
                ProblemDetail.forStatus(HttpStatus.CONFLICT);

        problem.setTitle("Login already exists");
        problem.setDetail(ex.getMessage());

        return problem;
    }

    @ExceptionHandler(InvalidPasswordException.class)
    public ProblemDetail handleInvalidPasswordException(
            InvalidPasswordException ex
    ) {

        ProblemDetail problem =
                ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);

        problem.setTitle("Invalid password");
        problem.setDetail(ex.getMessage());

        return problem;
    }
}

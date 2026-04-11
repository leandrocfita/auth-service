package br.com.fiap.auth_service.adapter.input.web;


import br.com.fiap.auth_service.adapter.input.web.dto.LoginRequestDto;
import br.com.fiap.auth_service.adapter.input.web.dto.RegisterRequestDto;
import br.com.fiap.auth_service.adapter.input.web.dto.RegisterResponseDto;
import br.com.fiap.auth_service.adapter.input.web.dto.TokenResponse;
import br.com.fiap.auth_service.application.port.input.LoginUseCase;
import br.com.fiap.auth_service.application.port.input.RegisterUserUseCase;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final LoginUseCase loginUseCase;
    private final RegisterUserUseCase registerUseCase;

    public AuthController(
            LoginUseCase loginUseCase,
            RegisterUserUseCase registerUseCase
    ) {
        this.loginUseCase = loginUseCase;
        this.registerUseCase = registerUseCase;
    }

    @PostMapping("/register")
    public RegisterResponseDto register(@RequestBody RegisterRequestDto req) {
        return new RegisterResponseDto(
            registerUseCase.register(req.login(), req.password()).toString()
        );
    }

    @PostMapping("/login")
    public TokenResponse login(@RequestBody LoginRequestDto req) {
        return new TokenResponse(
                loginUseCase.login(req.login(), req.password())
        );
    }
}

package br.com.fiap.auth_service.adapter.input.web;


import br.com.fiap.auth_service.adapter.input.web.dto.*;
import br.com.fiap.auth_service.application.port.input.ChangePasswordUseCase;
import br.com.fiap.auth_service.application.port.input.LoginUseCase;
import br.com.fiap.auth_service.application.port.input.RegisterUserUseCase;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final LoginUseCase loginUseCase;
    private final RegisterUserUseCase registerUseCase;
    private final ChangePasswordUseCase changePasswordUseCase;

    public AuthController(
            LoginUseCase loginUseCase,
            RegisterUserUseCase registerUseCase,
            ChangePasswordUseCase changePasswordUseCase
    ) {
        this.loginUseCase = loginUseCase;
        this.registerUseCase = registerUseCase;
        this.changePasswordUseCase = changePasswordUseCase;
    }

    @PostMapping("/register")
    public RegisterResponseDto register(@RequestBody RegisterRequestDto req) {
        return new RegisterResponseDto(
            registerUseCase.register(
                    req.login(),
                    req.password(),
                    req.externalId()
            ).toString()
        );
    }

    @PostMapping("/login")
    public TokenResponse login(@RequestBody @Valid LoginRequestDto req) {
        return new TokenResponse(
                loginUseCase.login(req.login(), req.password())
        );
    }

    @PatchMapping("/change-password")
    public void changePassword(@RequestBody @Valid ChangePasswordRequestDto req) {
        changePasswordUseCase.changePassword(req.oldPassword(), req.newPassword());
    }
}

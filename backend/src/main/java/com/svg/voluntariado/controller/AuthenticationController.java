package com.svg.voluntariado.controller;

import com.svg.voluntariado.domain.dto.user.LoginRequest;
import com.svg.voluntariado.domain.dto.user.LoginResponse;
import com.svg.voluntariado.domain.dto.user.UserRegisterRequest;
import com.svg.voluntariado.services.AuthenticationService;
import com.svg.voluntariado.services.TokenService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@Tag(name = "Atividades", description = "Endpoints para realizar o registro e login")
@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @Autowired
    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest loginRequest) {
        ResponseCookie cookie = authenticationService.login(loginRequest);
        LoginResponse body = new LoginResponse("Login realizado com sucesso!", TokenService.EXPIRY);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(body);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid UserRegisterRequest registerRequest) {
        authenticationService.register(registerRequest);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/confirm")
    public ResponseEntity<?> confirmRegistration(@RequestParam("token") String token) {
        authenticationService.confirmRegistration(token);
        return ResponseEntity.ok().build();
    }
}

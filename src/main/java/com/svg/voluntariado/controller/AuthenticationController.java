package com.svg.voluntariado.controller;

import com.svg.voluntariado.domain.dto.user.LoginRequest;
import com.svg.voluntariado.domain.dto.user.LoginResponse;
import com.svg.voluntariado.domain.dto.user.UserRegisterRequest;
import com.svg.voluntariado.services.AuthenticationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
        var response = authenticationService.login(loginRequest);
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid UserRegisterRequest registerRequest) {
        authenticationService.register(registerRequest);
        return ResponseEntity.ok().build();
    }
}

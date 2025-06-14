package com.svg.voluntariado.controller;

import com.svg.voluntariado.domain.dto.LoginRequest;
import com.svg.voluntariado.domain.dto.LoginResponse;
import com.svg.voluntariado.domain.dto.UserRegisterRequest;
import com.svg.voluntariado.domain.entities.RoleEntity;
import com.svg.voluntariado.domain.entities.UsuarioEntity;
import com.svg.voluntariado.repositories.RoleRepository;
import com.svg.voluntariado.repositories.UsuarioRepository;
import com.svg.voluntariado.services.AuthenticationService;
import com.svg.voluntariado.services.TokenService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;

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

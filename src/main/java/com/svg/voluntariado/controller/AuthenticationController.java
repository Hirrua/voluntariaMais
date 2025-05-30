package com.svg.voluntariado.controller;

import com.svg.voluntariado.dto.LoginRequest;
import com.svg.voluntariado.dto.LoginResponse;
import com.svg.voluntariado.dto.UserRegisterRequest;
import com.svg.voluntariado.entities.RoleEntity;
import com.svg.voluntariado.entities.UsuarioEntity;
import com.svg.voluntariado.repositories.RoleRepository;
import com.svg.voluntariado.repositories.UsuarioRepository;
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

    private final UsuarioRepository usuarioRepository;
    private final TokenService tokenService;
    private final RoleRepository roleRepository;

    private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    @Autowired
    public AuthenticationController(UsuarioRepository usuarioRepository, TokenService tokenService, RoleRepository roleRepository) {
        this.usuarioRepository = usuarioRepository;
        this.tokenService = tokenService;
        this.roleRepository = roleRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest loginRequest) {
        System.out.println(loginRequest);
        var user = usuarioRepository.findByEmail(loginRequest.email());

        if (user.isEmpty() || isLoginCorrect(loginRequest.senha(), user.get().getPassword(), bCryptPasswordEncoder)) {
            throw new BadCredentialsException("Credenciais incorretas.");
        }

        var userToken = tokenService.generateToken(user.get());

        return ResponseEntity.ok().body(new LoginResponse(userToken, TokenService.EXPIRY));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid UserRegisterRequest registerRequest) {
        if (usuarioRepository.findByEmail(registerRequest.email()).isPresent()) {
            return ResponseEntity.badRequest().body("Erro: Email já está em uso!");
        }

        if (usuarioRepository.findByCpf(registerRequest.cpf()).isPresent()) {
            return ResponseEntity.badRequest().body("Erro: CPF já está cadastrado!");
        }

        RoleEntity defaultRole = roleRepository.findByNome("ROLE_VOLUNTARIO");

        var senhaHash = bCryptPasswordEncoder.encode(registerRequest.senha());

        UsuarioEntity novoUsuario = new UsuarioEntity(
                registerRequest.nome(),
                registerRequest.sobrenome(),
                registerRequest.email(),
                senhaHash,
                registerRequest.cpf(),
                registerRequest.endereco(),
                OffsetDateTime.now()
        );

        novoUsuario.getRoles().add(defaultRole);

        usuarioRepository.save(novoUsuario);
        return ResponseEntity.ok().build();
    }

    private boolean isLoginCorrect(String senha, String senhaHash, BCryptPasswordEncoder passwordEncoder) {
        return passwordEncoder.matches(senha, senhaHash);
    }
}

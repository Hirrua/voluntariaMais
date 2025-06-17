package com.svg.voluntariado.services;

import com.svg.voluntariado.domain.dto.LoginRequest;
import com.svg.voluntariado.domain.dto.LoginResponse;
import com.svg.voluntariado.domain.dto.UserRegisterRequest;
import com.svg.voluntariado.domain.entities.RoleEntity;
import com.svg.voluntariado.domain.entities.UsuarioEntity;
import com.svg.voluntariado.domain.mapper.UserMapper;
import com.svg.voluntariado.repositories.RoleRepository;
import com.svg.voluntariado.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Service
public class AuthenticationService {

    private final UsuarioRepository usuarioRepository;
    private final TokenService tokenService;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;

    private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    @Autowired
    public AuthenticationService(UsuarioRepository usuarioRepository, TokenService tokenService, RoleRepository roleRepository, UserMapper userMapper) {
        this.usuarioRepository = usuarioRepository;
        this.tokenService = tokenService;
        this.roleRepository = roleRepository;
        this.userMapper = userMapper;
    }

    public LoginResponse login(LoginRequest loginRequest) {
        var user = usuarioRepository.findByEmail(loginRequest.email());

        if (user.isEmpty() || !isLoginCorrect(loginRequest.senha(), user.get().getPassword(), bCryptPasswordEncoder)) {
            throw new BadCredentialsException("Credenciais incorretas.");
        }

        var userToken = tokenService.generateToken(user.get());
        return new LoginResponse(userToken, TokenService.EXPIRY);
    }

    public void register(UserRegisterRequest registerRequest) {
        RoleEntity defaultRole = roleRepository.findByNome("ROLE_VOLUNTARIO");
        var senhaHash = bCryptPasswordEncoder.encode(registerRequest.senha());

        var newUser = userMapper.toUsuarioEntity(registerRequest);
        newUser.setSenha(senhaHash);
        newUser.getRoles().add(defaultRole);
        usuarioRepository.save(newUser);
    }

    private boolean isLoginCorrect(String senha, String senhaHash, BCryptPasswordEncoder passwordEncoder) {
        return passwordEncoder.matches(senha, senhaHash);
    }

}

package com.svg.voluntariado.services;

import com.svg.voluntariado.domain.dto.user.LoginRequest;
import com.svg.voluntariado.domain.dto.user.LoginResponse;
import com.svg.voluntariado.domain.dto.user.UserRegisterRequest;
import com.svg.voluntariado.domain.entities.RoleEntity;
import com.svg.voluntariado.exceptions.ExpiredTokenException;
import com.svg.voluntariado.exceptions.TokenNotFoundException;
import com.svg.voluntariado.exceptions.UserPendingConfirmationException;
import com.svg.voluntariado.mapper.UserMapper;
import com.svg.voluntariado.repositories.RoleRepository;
import com.svg.voluntariado.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class AuthenticationService {

    private final UserRepository userRepository;
    private final TokenService tokenService;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final EmailService emailService;

    @Value("${app.volunteer.approval.base-url:}")
    private String approvalBaseUrl;

    private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    @Autowired
    public AuthenticationService(UserRepository userRepository, TokenService tokenService,
                                 RoleRepository roleRepository, UserMapper userMapper, EmailService emailService) {
        this.userRepository = userRepository;
        this.tokenService = tokenService;
        this.roleRepository = roleRepository;
        this.userMapper = userMapper;
        this.emailService = emailService;
    }

    public ResponseCookie login(LoginRequest loginRequest) {
        var user = userRepository.findByEmail(loginRequest.email());

        if (user.isEmpty() || !isLoginCorrect(loginRequest.senha(), user.get().getPassword(), bCryptPasswordEncoder)) {
            throw new BadCredentialsException("Credenciais incorretas.");
        }

        if (!user.get().isEnabled()) {
            throw new UserPendingConfirmationException();
        }

        var userToken = tokenService.generateToken(user.get());

        return ResponseCookie.from("access_volunteer", userToken)
                .httpOnly(true)
                .secure(false) // Em produção, usar true com HTTPS
                .path("/")
                .maxAge(TokenService.EXPIRY)
                .sameSite("Lax") // Lax para desenvolvimento HTTP, None requer Secure=true
                .build();
    }

    public void register(UserRegisterRequest registerRequest) {
        RoleEntity defaultRole = roleRepository.findByNome("ROLE_VOLUNTARIO");
        var senhaHash = bCryptPasswordEncoder.encode(registerRequest.senha());

        var newUser = userMapper.toUsuarioEntity(registerRequest);
        newUser.setSenha(senhaHash);
        newUser.getRoles().add(defaultRole);
        newUser.prepareForConfirmation();

        userRepository.save(newUser);

        String confirmationUrl = approvalBaseUrl + newUser.getTokenConfirmacao();
        Map<String, Object> emailVar = new HashMap<>();
        emailVar.put("userName", registerRequest.nome() + " " + registerRequest.sobrenome());
        emailVar.put("confirmationUrl", confirmationUrl);

        emailService.sendHtmlEmail(
                registerRequest.email(),
                "Confirmação de registro",
                "send-email-register.html",
                emailVar
        );
    }

    public void confirmRegistration(String token) {
        var user = userRepository.findByTokenConfirmacao(token)
                .orElseThrow(() -> new TokenNotFoundException("Token de confirmação inválido."));

        if (user.getDataExpiracaoToken() == null || user.getDataExpiracaoToken().isBefore(OffsetDateTime.now())) {
            throw new ExpiredTokenException("Token de confirmação expirado.");
        }

        user.confirmRegistration();
        userRepository.save(user);
    }

    private boolean isLoginCorrect(String senha, String senhaHash, BCryptPasswordEncoder passwordEncoder) {
        return passwordEncoder.matches(senha, senhaHash);
    }
}

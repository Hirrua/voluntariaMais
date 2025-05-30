package com.svg.voluntariado.services;

import com.svg.voluntariado.entities.RoleEntity;
import com.svg.voluntariado.entities.UsuarioEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.stream.Collectors;

@Service
public class TokenService {

    private final JwtEncoder jwtEncoder;
    public static final Long EXPIRY = 18000L;

    @Autowired
    public TokenService(JwtEncoder jwtEncoder) {
        this.jwtEncoder = jwtEncoder;
    }

    public String generateToken(UsuarioEntity usuario) {
        Instant now = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("voluntaria-mais")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(EXPIRY))
                .subject(usuario.getId().toString())
                .claim("scope", usuario.getRoles()
                        .stream()
                        .map(RoleEntity::getNome)
                        .collect(Collectors.joining(" "))
                )
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }
}

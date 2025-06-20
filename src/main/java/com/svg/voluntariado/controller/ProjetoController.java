package com.svg.voluntariado.controller;

import com.svg.voluntariado.domain.dto.projeto.CreateProjetoRequest;
import com.svg.voluntariado.services.ProjetoService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/api/projetos")
public class ProjetoController {

    private final ProjetoService projetoService;

    public ProjetoController(ProjetoService projetoService) {
        this.projetoService = projetoService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN_ONG')")
    public ResponseEntity<?> createProject(@RequestBody CreateProjetoRequest createProjetoRequest, @AuthenticationPrincipal Jwt principal) {
        Long idAdmin = Long.parseLong(principal.getSubject());
        var projetoId = projetoService.create(createProjetoRequest, idAdmin);
        return ResponseEntity.created(URI.create("/api/projetos/" + projetoId)).body("Projeto criado com suscesso");
    }
}

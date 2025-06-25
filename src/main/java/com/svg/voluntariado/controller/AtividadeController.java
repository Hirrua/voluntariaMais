package com.svg.voluntariado.controller;

import com.svg.voluntariado.domain.dto.atividade.CreateAtividadeRequest;
import com.svg.voluntariado.services.AtividadeService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.nio.file.AccessDeniedException;

@RestController
@RequestMapping("/api/atividades")
public class AtividadeController {

    private final AtividadeService atividadeService;

    public AtividadeController(AtividadeService atividadeService) {
        this.atividadeService = atividadeService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN_ONG')")
    public ResponseEntity<?> createActivity(@RequestBody CreateAtividadeRequest createAtividadeRequest, @AuthenticationPrincipal Jwt principal) throws AccessDeniedException {
        Long idAdmin = Long.parseLong(principal.getSubject());
        var atividadeId = atividadeService.create(createAtividadeRequest, idAdmin);
        return ResponseEntity.created(URI.create("/api/atividades/" + atividadeId)).body("Atividade criada com sucesso.");
    }

    @GetMapping("/info/{id}")
    @PreAuthorize("hasRole('VOLUNTARIO') or hasRole('ADMIN_ONG')")
    public ResponseEntity<?> getInfoActivity(@PathVariable(value = "id") Long id) {
        var atividadeInfo = atividadeService.get(id);
        return ResponseEntity.ok().body(atividadeInfo);
    }
}

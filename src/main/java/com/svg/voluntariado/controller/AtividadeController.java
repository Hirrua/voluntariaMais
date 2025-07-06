package com.svg.voluntariado.controller;

import com.svg.voluntariado.domain.dto.atividade.CreateAtividadeRequest;
import com.svg.voluntariado.domain.dto.atividade.UpdateAtividadeRequest;
import com.svg.voluntariado.exceptions.AtividadeNotFoundException;
import com.svg.voluntariado.services.AtividadeService;
import jakarta.validation.Valid;
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

    @GetMapping("/infos")
    @PreAuthorize("hasRole('VOLUNTARIO') or hasRole('ADMIN_ONG')")
    public ResponseEntity<?> getActivities(@RequestParam int page, @RequestParam int itens) throws AtividadeNotFoundException {
        var atividades = atividadeService.getAllActivities(page, itens);
        return ResponseEntity.ok().body(atividades);
    }

    @GetMapping("/info/{id}")
    @PreAuthorize("hasRole('VOLUNTARIO') or hasRole('ADMIN_ONG')")
    public ResponseEntity<?> getInfoActivity(@PathVariable(value = "id") Long id) {
        var atividadeInfo = atividadeService.get(id);
        return ResponseEntity.ok().body(atividadeInfo);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN_ONG')")
    public ResponseEntity<?> updateActivity(@PathVariable(value = "id") Long id,
                                            @AuthenticationPrincipal Jwt principal,
                                            @RequestBody @Valid UpdateAtividadeRequest updateAtividadeRequest)
            throws AtividadeNotFoundException, AccessDeniedException {
        Long idAdmin = Long.parseLong(principal.getSubject());
        var atividade = atividadeService.update(id, idAdmin, updateAtividadeRequest);
        return ResponseEntity.ok().body(atividade);
    }
}

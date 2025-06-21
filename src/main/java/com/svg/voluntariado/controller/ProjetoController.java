package com.svg.voluntariado.controller;

import com.svg.voluntariado.domain.dto.projeto.CreateProjetoRequest;
import com.svg.voluntariado.domain.dto.projeto.UpdateProjetoRequest;
import com.svg.voluntariado.services.ProjetoService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/infos")
    public ResponseEntity<?> getAllProjects(@RequestParam int page, @RequestParam int itens) {
        var projetos = projetoService.getAll(page, itens);
        return ResponseEntity.ok().body(projetos);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN_ONG')")
    public ResponseEntity<?> updateProject(@PathVariable(value = "id") Long idProjeto,
                                           @AuthenticationPrincipal Jwt principal,
                                           @RequestBody UpdateProjetoRequest updateProjetoRequest) {
        Long idAdmin = Long.parseLong(principal.getSubject());
        var update = projetoService.update(idProjeto, idAdmin, updateProjetoRequest);
        return ResponseEntity.ok().body(update);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN_ONG') or hasRole('ADMIN_PLATAFORMA')")
    public ResponseEntity<?> deleteProject(@PathVariable(value = "id") Long idProjeto, @AuthenticationPrincipal Jwt principal) {
        Long idAdmin = Long.parseLong(principal.getSubject());
        projetoService.delete(idProjeto, idAdmin, principal);
        return ResponseEntity.ok().body("Projeto excluído com sucesso.");
    }
}

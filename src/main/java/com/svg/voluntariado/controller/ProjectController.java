package com.svg.voluntariado.controller;

import com.svg.voluntariado.domain.dto.project.CreateProjectRequest;
import com.svg.voluntariado.domain.dto.project.UpdateProjectRequest;
import com.svg.voluntariado.services.ProjectService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/projetos")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN_ONG')")
    public ResponseEntity<?> createProject(@RequestBody CreateProjectRequest createProjectRequest, @AuthenticationPrincipal Jwt principal) {
        Long idAdmin = Long.parseLong(principal.getSubject());
        var projetoId = projectService.create(createProjectRequest, idAdmin);
        return ResponseEntity.created(URI.create("/api/projetos/" + projetoId)).body("Projeto criado com suscesso");
    }

    @GetMapping("/infos")
    public ResponseEntity<?> getAllProjects(@RequestParam int page, @RequestParam int itens) {
        var projetos = projectService.getAll(page, itens);
        return ResponseEntity.ok().body(projetos);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN_ONG')")
    public ResponseEntity<?> updateProject(@PathVariable(value = "id") Long idProjeto,
                                           @AuthenticationPrincipal Jwt principal,
                                           @RequestBody UpdateProjectRequest updateProjectRequest) {
        Long idAdmin = Long.parseLong(principal.getSubject());
        var update = projectService.update(idProjeto, idAdmin, updateProjectRequest);
        return ResponseEntity.ok().body(update);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN_ONG') or hasRole('ADMIN_PLATAFORMA')")
    public ResponseEntity<?> deleteProject(@PathVariable(value = "id") Long idProjeto, @AuthenticationPrincipal Jwt principal) {
        Long idAdmin = Long.parseLong(principal.getSubject());
        projectService.delete(idProjeto, idAdmin, principal);
        return ResponseEntity.ok().body("Projeto excluído com sucesso.");
    }
}

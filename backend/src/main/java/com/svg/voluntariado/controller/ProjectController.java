package com.svg.voluntariado.controller;

import com.svg.voluntariado.domain.dto.PageResponse;
import com.svg.voluntariado.domain.dto.project.CreateProjectRequest;
import com.svg.voluntariado.domain.dto.project.SimpleInfoProjectResponse;
import com.svg.voluntariado.domain.dto.project.UpdateProjectRequest;
import com.svg.voluntariado.services.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@Tag(name = "Projetos", description = "Endpoints para gerenciar os projetos de ONG's")
@RestController
@RequestMapping("/api/projetos")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @Operation(summary = "Criar um projeto")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN_ONG') or hasRole('ADMIN_PLATAFORMA')")
    public ResponseEntity<?> createProject(@RequestBody CreateProjectRequest createProjectRequest, @AuthenticationPrincipal Jwt principal) {
        Long idAdmin = Long.parseLong(principal.getSubject());
        var projetoId = projectService.create(createProjectRequest, idAdmin);
        return ResponseEntity.created(URI.create("/api/projetos/" + projetoId)).body("Projeto criado com suscesso");
    }

    @Operation(summary = "Visualizar todos os projetos")
    @GetMapping("/infos")
    public ResponseEntity<PageResponse<SimpleInfoProjectResponse>> getAllProjects(@RequestParam int page, @RequestParam int itens) {
        var projetos = projectService.getAll(page, itens);
        return ResponseEntity.ok(projetos);
    }

    @GetMapping("/infos/{id}")
    public ResponseEntity<?> getOngProjectAndActivityInfo(@PathVariable(value = "id") Long id,
                                                          @AuthenticationPrincipal Jwt principal) {
        Long idUser = principal != null ? Long.parseLong(principal.getSubject()) : null;
        var projectInfo = projectService.getOngProjectAndActivityInfo(id, idUser);
        return ResponseEntity.ok(projectInfo);
    }

    @Operation(summary = "Atualizar um projeto")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN_ONG') or hasRole('ADMIN_PLATAFORMA')")
    public ResponseEntity<?> updateProject(@PathVariable(value = "id") Long idProjeto,
                                           @AuthenticationPrincipal Jwt principal,
                                           @RequestBody UpdateProjectRequest updateProjectRequest) {
        Long idAdmin = Long.parseLong(principal.getSubject());
        var update = projectService.update(idProjeto, idAdmin, updateProjectRequest);
        return ResponseEntity.ok().body(update);
    }

    @Operation(summary = "Deletar um projeto")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN_ONG') or hasRole('ADMIN_PLATAFORMA')")
    public ResponseEntity<?> deleteProject(@PathVariable(value = "id") Long idProjeto, @AuthenticationPrincipal Jwt principal) {
        Long idAdmin = Long.parseLong(principal.getSubject());
        projectService.delete(idProjeto, idAdmin);
        return ResponseEntity.ok().body("Projeto excluído com sucesso.");
    }
}

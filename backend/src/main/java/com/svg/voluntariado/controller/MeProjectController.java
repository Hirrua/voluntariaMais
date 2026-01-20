package com.svg.voluntariado.controller;

import com.svg.voluntariado.domain.dto.activity.CreateActivityRequest;
import com.svg.voluntariado.domain.dto.project.ProjectAdminResponse;
import com.svg.voluntariado.domain.dto.project.UpdateProjectRequest;
import com.svg.voluntariado.services.ActivityService;
import com.svg.voluntariado.services.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@Tag(name = "Meus projetos", description = "Endpoints de projetos e atividades para admin da ONG autenticado")
@RestController
@RequestMapping("/api/me/projects")
public class MeProjectController {

    private final ProjectService projectService;
    private final ActivityService activityService;

    public MeProjectController(ProjectService projectService, ActivityService activityService) {
        this.projectService = projectService;
        this.activityService = activityService;
    }

    @Operation(summary = "Listar projetos da ONG do admin")
    @GetMapping
    @PreAuthorize("hasRole('ADMIN_ONG')")
    public ResponseEntity<List<ProjectAdminResponse>> listMyProjects(@AuthenticationPrincipal Jwt principal) {
        Long adminId = Long.parseLong(principal.getSubject());
        var projects = projectService.listForAdmin(adminId);
        return ResponseEntity.ok(projects);
    }

    @Operation(summary = "Detalhar um projeto do admin")
    @GetMapping("/{projectId}")
    @PreAuthorize("hasRole('ADMIN_ONG')")
    public ResponseEntity<ProjectAdminResponse> getMyProject(@PathVariable Long projectId,
                                                             @AuthenticationPrincipal Jwt principal) {
        Long adminId = Long.parseLong(principal.getSubject());
        var project = projectService.getForAdmin(projectId, adminId);
        return ResponseEntity.ok(project);
    }

    @Operation(summary = "Atualizar um projeto do admin")
    @PatchMapping("/{projectId}")
    @PreAuthorize("hasRole('ADMIN_ONG')")
    public ResponseEntity<ProjectAdminResponse> updateMyProject(@PathVariable Long projectId,
                                                                @RequestBody UpdateProjectRequest updateProjectRequest,
                                                                @AuthenticationPrincipal Jwt principal) {
        Long adminId = Long.parseLong(principal.getSubject());
        var project = projectService.updateForAdmin(projectId, adminId, updateProjectRequest);
        return ResponseEntity.ok(project);
    }

    @Operation(summary = "Deletar um projeto do admin")
    @DeleteMapping("/{projectId}")
    @PreAuthorize("hasRole('ADMIN_ONG')")
    public ResponseEntity<?> deleteMyProject(@PathVariable Long projectId,
                                             @AuthenticationPrincipal Jwt principal) {
        Long adminId = Long.parseLong(principal.getSubject());
        projectService.deleteForAdmin(projectId, adminId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Listar atividades de um projeto do admin")
    @GetMapping("/{projectId}/activities")
    @PreAuthorize("hasRole('ADMIN_ONG')")
    public ResponseEntity<?> listMyProjectActivities(@PathVariable Long projectId,
                                                     @AuthenticationPrincipal Jwt principal) {
        Long adminId = Long.parseLong(principal.getSubject());
        var activities = activityService.listForProjectAdmin(projectId, adminId);
        return ResponseEntity.ok(activities);
    }

    @Operation(summary = "Criar atividade para um projeto do admin")
    @PostMapping("/{projectId}/activities")
    @PreAuthorize("hasRole('ADMIN_ONG')")
    public ResponseEntity<?> createMyProjectActivity(@PathVariable Long projectId,
                                                     @RequestBody CreateActivityRequest createActivityRequest,
                                                     @AuthenticationPrincipal Jwt principal) {
        Long adminId = Long.parseLong(principal.getSubject());
        var activityId = activityService.createForProjectAdmin(projectId, adminId, createActivityRequest);
        return ResponseEntity.created(URI.create("/api/atividades/" + activityId)).body("Atividade criada com sucesso.");
    }
}

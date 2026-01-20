package com.svg.voluntariado.controller;

import com.svg.voluntariado.domain.dto.ong.UpdateInfoOngRequest;
import com.svg.voluntariado.domain.dto.project.CreateProjectRequest;
import com.svg.voluntariado.services.OngService;
import com.svg.voluntariado.services.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@Tag(name = "Meu contexto", description = "Endpoints seguros do administrador da ONG autenticado")
@RestController
@RequestMapping("/api/me")
public class MeController {

    private final OngService ongService;
    private final ProjectService projectService;

    public MeController(OngService ongService, ProjectService projectService) {
        this.ongService = ongService;
        this.projectService = projectService;
    }

    @Operation(summary = "Buscar ONG do admin logado")
    @GetMapping("/ong")
    @PreAuthorize("hasRole('ADMIN_ONG')")
    public ResponseEntity<?> getMyOng(@AuthenticationPrincipal Jwt principal) {
        Long adminId = Long.parseLong(principal.getSubject());
        var ongInfo = ongService.getForAdmin(adminId);
        return ResponseEntity.ok(ongInfo);
    }

    @Operation(summary = "Atualizar ONG do admin logado")
    @PatchMapping("/ong")
    @PreAuthorize("hasRole('ADMIN_ONG')")
    public ResponseEntity<?> updateMyOng(@RequestBody UpdateInfoOngRequest updateInfoOngRequest,
                                         @AuthenticationPrincipal Jwt principal) {
        Long adminId = Long.parseLong(principal.getSubject());
        var ongInfo = ongService.updateForAdmin(adminId, updateInfoOngRequest);
        return ResponseEntity.ok(ongInfo);
    }

    @Operation(summary = "Criar projeto na ONG do admin logado")
    @PostMapping("/projetos")
    @PreAuthorize("hasRole('ADMIN_ONG')")
    public ResponseEntity<?> createMyProject(@RequestBody CreateProjectRequest createProjectRequest,
                                             @AuthenticationPrincipal Jwt principal) {
        Long adminId = Long.parseLong(principal.getSubject());
        var projetoId = projectService.create(createProjectRequest, adminId);
        return ResponseEntity.created(URI.create("/api/projetos/" + projetoId)).body("Projeto criado com suscesso");
    }
}

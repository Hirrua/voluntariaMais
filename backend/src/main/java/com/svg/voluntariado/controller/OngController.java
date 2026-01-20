package com.svg.voluntariado.controller;

import com.svg.voluntariado.domain.dto.ong.CreateOngRequest;
import com.svg.voluntariado.domain.dto.ong.ListOngResponse;
import com.svg.voluntariado.domain.dto.ong.UpdateInfoOngRequest;
import com.svg.voluntariado.services.OngService;
import com.svg.voluntariado.utils.JwtRoleUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@Tag(name = "ONG", description = "Endpoints para realizar o gerenciamento de ONG's")
@RestController
@RequestMapping("/api/ong")
public class OngController {

    private final OngService ongService;

    public OngController(OngService ongService) {
        this.ongService = ongService;
    }

    @Operation(summary = "Criar uma ONG")
    @PostMapping
    @PreAuthorize("hasAnyRole('VOLUNTARIO', 'ADMIN_ONG', 'ADMIN_PLATAFORMA')")
    public ResponseEntity<?> createOng(@RequestBody @Valid CreateOngRequest createOngRequest, @AuthenticationPrincipal Jwt principal) {
        var requesterId = Long.parseLong(principal.getSubject());
        boolean isAdminPlataforma = JwtRoleUtils.hasRole(principal, "ROLE_ADMIN_PLATAFORMA");
        var ongId = ongService.create(createOngRequest, requesterId, isAdminPlataforma);
        String message = isAdminPlataforma ? "ONG criada com sucesso." : "Solicitação de ONG enviada para aprovação.";
        return ResponseEntity.created(URI.create("/api/ong/" + ongId)).body(message);
    }

    @Operation(summary = "Buscar informações de uma ONG")
    @GetMapping("/info/{id}")
    public ResponseEntity<?> getInfoOng(@PathVariable(value = "id") Long id) {
        var ongInfos = ongService.get(id);
        return ResponseEntity.ok().body(ongInfos);
    }

    @Operation(summary = "Buscar informações de uma ONG e seus projetos vinculados")
    @GetMapping("/info/about/{idOng}")
    public ResponseEntity<?> getInfoOngAndProject(@PathVariable(value = "idOng") Long idOng,
                                                  @AuthenticationPrincipal Jwt principal) {
        Long requesterId = principal != null ? Long.parseLong(principal.getSubject()) : null;
        boolean isAdminOng = JwtRoleUtils.hasRole(principal, "ROLE_ADMIN_ONG");
        boolean isAdminPlataforma = JwtRoleUtils.hasRole(principal, "ROLE_ADMIN_PLATAFORMA");
        var infos = ongService.findOngAndProjects(idOng, requesterId, isAdminOng, isAdminPlataforma);
        return ResponseEntity.ok().body(infos);
    }

    @Operation(summary = "Buscar todas as ONG's cadastradas")
    @GetMapping("/info")
    public ResponseEntity<?> findAllOng(@RequestParam int page, @RequestParam int itens) {
        List<ListOngResponse> ongResponseList = ongService.findAllOng(page, itens);
        return ResponseEntity.ok().body(ongResponseList);
    }

    @Operation(summary = "Atualizar informações de uma ONG")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN_ONG') or hasRole('ADMIN_PLATAFORMA')")
    public ResponseEntity<?> updateOng(@PathVariable(value = "id") Long id, @RequestBody UpdateInfoOngRequest infoOngRequest, @AuthenticationPrincipal Jwt principal) {
        Long idAdmin = Long.parseLong(principal.getSubject());
        var ongInfos = ongService.update(id, idAdmin, infoOngRequest);
        return ResponseEntity.ok().body(ongInfos);
    }

    @Operation(summary = "Deletar uma ONG")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN_ONG') or hasRole('ADMIN_PLATAFORMA')")
    public ResponseEntity<?> deleteOng(@PathVariable(value = "id") Long id, @AuthenticationPrincipal Jwt principal) {
        Long idAdmin = Long.parseLong(principal.getSubject());
        ongService.delete(id, idAdmin);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Aprovar solicitação de ONG")
    @GetMapping("/aprovacao")
    public ResponseEntity<?> approveOng(@RequestParam("token") String token) {
        ongService.approveByToken(token);
        return ResponseEntity.ok().body("ONG aprovada com sucesso.");
    }

    @Operation(summary = "Rejeitar solicitação de ONG")
    @GetMapping("/rejeicao")
    public ResponseEntity<?> rejectOng(@RequestParam("token") String token) {
        ongService.rejectByToken(token);
        return ResponseEntity.ok().body("ONG rejeitada com sucesso.");
    }
}

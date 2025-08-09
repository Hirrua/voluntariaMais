package com.svg.voluntariado.controller;

import com.svg.voluntariado.domain.dto.ong.CreateOngRequest;
import com.svg.voluntariado.domain.dto.ong.ListOngResponse;
import com.svg.voluntariado.domain.dto.ong.UpdateInfoOngRequest;
import com.svg.voluntariado.services.OngService;
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
    @PreAuthorize("T(java.lang.Long).parseLong(principal.subject) == #createOngRequest.idUsuarioResponsavel() or hasRole('ADMIN_PLATAFORMA')")
    public ResponseEntity<?> createOng(@RequestBody @Valid CreateOngRequest createOngRequest) {
        var ongId = ongService.create(createOngRequest);
        return ResponseEntity.created(URI.create("/api/ong/" + ongId)).body("Ong criado com sucesso.");
    }

    @Operation(summary = "Buscar informações de uma ONG")
    @GetMapping("/info/{id}")
    public ResponseEntity<?> getInfoOng(@PathVariable(value = "id") Long id) {
        var ongInfos = ongService.get(id);
        return ResponseEntity.ok().body(ongInfos);
    }

    @Operation(summary = "Buscar informações de uma ONG e seus projetos vinculados")
    @GetMapping("/info/about/{idOng}")
    public ResponseEntity<?> getInfoOngAndProject(@PathVariable(value = "idOng") Long idOng) {
        var infos = ongService.findOngAndProjects(idOng);
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
    @PreAuthorize("hasRole('ADMIN_ONG')")
    public ResponseEntity<?> updateOng(@PathVariable(value = "id") Long id, @RequestBody UpdateInfoOngRequest infoOngRequest, @AuthenticationPrincipal Jwt principal) {
        Long idAdmin = Long.parseLong(principal.getSubject());
        var ongInfos = ongService.update(id, idAdmin, infoOngRequest);
        return ResponseEntity.ok().body(ongInfos);
    }

    @Operation(summary = "Deletar uma ONG")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN_ONG')")
    public ResponseEntity<?> deleteOng(@PathVariable(value = "id") Long id, @AuthenticationPrincipal Jwt principal) {
        Long idAdmin = Long.parseLong(principal.getSubject());
        ongService.delete(id, idAdmin);
        return ResponseEntity.ok().build();
    }
}

package com.svg.voluntariado.controller;

import com.svg.voluntariado.domain.dto.CreateProfileRequest;
import com.svg.voluntariado.domain.dto.InfoPerfilResponse;
import com.svg.voluntariado.domain.dto.UpdateInfoProfileRequest;
import com.svg.voluntariado.services.PerfilVoluntarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/perfil")
public class PerfilVoluntarioController {

    private final PerfilVoluntarioService perfilVoluntarioService;

    @Autowired
    public PerfilVoluntarioController(PerfilVoluntarioService perfilVoluntarioService) {
        this.perfilVoluntarioService = perfilVoluntarioService;
    }

    @PostMapping
    public ResponseEntity<?> createProfile(@RequestBody @Valid CreateProfileRequest createProfileRequest) {
        var profileId = perfilVoluntarioService.create(createProfileRequest);
        return ResponseEntity.created(URI.create("/api/profile/" + profileId)).body("Perfil criado com sucesso.");
    }

    @GetMapping("/{id}")
    public ResponseEntity<InfoPerfilResponse> getInfoProfile(@PathVariable(value = "id") Long id) {
        var infos = perfilVoluntarioService.read(id);
        return ResponseEntity.ok().body(infos);
    }

    @PutMapping("/{id}")
    @PreAuthorize("T(java.lang.Long).parseLong(principal.subject) == #id")
    public ResponseEntity<?> updateInfoProfile(@PathVariable(value = "id") Long id, @RequestBody @Valid UpdateInfoProfileRequest updateInfo) {
        var infos = perfilVoluntarioService.update(id, updateInfo);
        return ResponseEntity.ok().body(infos);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("T(java.lang.Long).parseLong(principal.subject) == #id or hasRole('ADMIN_PLATAFORMA')")
    public ResponseEntity<?> deleteProfile(@PathVariable(value = "id") Long id) {
        perfilVoluntarioService.delete(id);
        return ResponseEntity.ok().build();
    }
}

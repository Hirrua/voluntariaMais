package com.svg.voluntariado.controller;

import com.svg.voluntariado.domain.dto.profile.CreateProfileRequest;
import com.svg.voluntariado.domain.dto.profile.InfoProfileResponse;
import com.svg.voluntariado.domain.dto.profile.UpdateInfoProfileRequest;
import com.svg.voluntariado.services.VolunteerProfileService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/perfil")
public class VolunteerProfileController {

    private final VolunteerProfileService volunteerProfileService;

    @Autowired
    public VolunteerProfileController(VolunteerProfileService volunteerProfileService) {
        this.volunteerProfileService = volunteerProfileService;
    }

    @PostMapping
    public ResponseEntity<?> createProfile(@RequestBody @Valid CreateProfileRequest createProfileRequest) {
        var profileId = volunteerProfileService.create(createProfileRequest);
        return ResponseEntity.created(URI.create("/api/profile/" + profileId)).body("Perfil criado com sucesso.");
    }

    @GetMapping("/{id}")
    public ResponseEntity<InfoProfileResponse> getInfoProfile(@PathVariable(value = "id") Long id) {
        var infos = volunteerProfileService.get(id);
        return ResponseEntity.ok().body(infos);
    }

    @PutMapping("/me")
    @PreAuthorize("hasRole('VOLUNTARIO')")
    public ResponseEntity<?> updateInfoProfile(@RequestBody @Valid UpdateInfoProfileRequest updateInfo, @AuthenticationPrincipal Jwt principal) {
        Long id = Long.parseLong(principal.getSubject());
        var infos = volunteerProfileService.update(id, updateInfo);
        return ResponseEntity.ok().body(infos);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("T(java.lang.Long).parseLong(principal.subject) == #id or hasRole('ADMIN_PLATAFORMA')")
    public ResponseEntity<?> deleteProfile(@PathVariable(value = "id") Long id) {
        volunteerProfileService.delete(id);
        return ResponseEntity.ok().build();
    }
}

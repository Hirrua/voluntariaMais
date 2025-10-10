package com.svg.voluntariado.controller;

import com.svg.voluntariado.domain.dto.profile.CreateProfileRequest;
import com.svg.voluntariado.domain.dto.profile.InfoProfileResponse;
import com.svg.voluntariado.domain.dto.profile.UpdateInfoProfileRequest;
import com.svg.voluntariado.domain.dto.user.UserInfoDTO;
import com.svg.voluntariado.exceptions.UserNotFoundException;
import com.svg.voluntariado.repositories.UserRepository;
import com.svg.voluntariado.services.VolunteerProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@Tag(name = "Perfil voluntário", description = "Endpoint para criação e gerenciamento do perfil do voluntário")
@RestController
@RequestMapping("/api/perfil")
public class VolunteerProfileController {

    private final VolunteerProfileService volunteerProfileService;
    private final UserRepository userRepository;

    @Autowired
    public VolunteerProfileController(VolunteerProfileService volunteerProfileService, UserRepository userRepository) {
        this.volunteerProfileService = volunteerProfileService;
        this.userRepository = userRepository;
    }

    @Operation(summary = "Criar um perfil")
    @PostMapping
    public ResponseEntity<?> createProfile(@RequestBody @Valid CreateProfileRequest createProfileRequest) {
        var profileId = volunteerProfileService.create(createProfileRequest);
        return ResponseEntity.created(URI.create("/api/profile/" + profileId)).body("Perfil criado com sucesso.");
    }

    @Operation(summary = "Buscar informações do perfil")
    @GetMapping("/{id}")
    public ResponseEntity<InfoProfileResponse> getInfoProfile(@PathVariable(value = "id") Long id) {
        var infos = volunteerProfileService.get(id);
        return ResponseEntity.ok().body(infos);
    }

    @Operation(summary = "Atualizar perfil")
    @PutMapping("/me")
    @PreAuthorize("hasRole('VOLUNTARIO')")
    public ResponseEntity<?> updateInfoProfile(@RequestBody @Valid UpdateInfoProfileRequest updateInfo, @AuthenticationPrincipal Jwt principal) {
        Long id = Long.parseLong(principal.getSubject());
        var infos = volunteerProfileService.update(id, updateInfo);
        return ResponseEntity.ok().body(infos);
    }

    @Operation(summary = "Deletar perfil")
    @DeleteMapping("/{id}")
    @PreAuthorize("T(java.lang.Long).parseLong(principal.subject) == #id or hasRole('ADMIN_PLATAFORMA')")
    public ResponseEntity<?> deleteProfile(@PathVariable(value = "id") Long id) {
        volunteerProfileService.delete(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/users/me")
    public ResponseEntity<UserInfoDTO> getCurrentUser(@AuthenticationPrincipal Jwt authentication) {
        var currentUser = userRepository.findByIdIfProfileExists(Long.parseLong(authentication.getSubject())).orElseThrow(UserNotFoundException::new);
        UserInfoDTO userInfo = new UserInfoDTO(currentUser.getId(), currentUser.getNome(), currentUser.getEmail());
        return ResponseEntity.ok(userInfo);
    }

}

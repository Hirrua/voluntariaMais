package com.svg.voluntariado.controller;

import com.svg.voluntariado.domain.dto.activity.CreateActivityRequest;
import com.svg.voluntariado.domain.dto.activity.UpdateActivityRequest;
import com.svg.voluntariado.exceptions.ActivityNotFoundException;
import com.svg.voluntariado.services.ActivityService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.nio.file.AccessDeniedException;

@RestController
@RequestMapping("/api/atividades")
public class ActivityController {

    private final ActivityService activityService;

    public ActivityController(ActivityService activityService) {
        this.activityService = activityService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN_ONG')")
    public ResponseEntity<?> createActivity(@RequestBody CreateActivityRequest createActivityRequest, @AuthenticationPrincipal Jwt principal) throws AccessDeniedException {
        Long idAdmin = Long.parseLong(principal.getSubject());
        var atividadeId = activityService.create(createActivityRequest, idAdmin);
        return ResponseEntity.created(URI.create("/api/atividades/" + atividadeId)).body("Atividade criada com sucesso.");
    }

    @GetMapping("/infos")
    @PreAuthorize("hasRole('VOLUNTARIO') or hasRole('ADMIN_ONG')")
    public ResponseEntity<?> getActivities(@RequestParam int page, @RequestParam int itens) throws ActivityNotFoundException {
        var atividades = activityService.getAllActivities(page, itens);
        return ResponseEntity.ok().body(atividades);
    }

    @GetMapping("/info/{id}")
    @PreAuthorize("hasRole('VOLUNTARIO') or hasRole('ADMIN_ONG')")
    public ResponseEntity<?> getInfoActivity(@PathVariable(value = "id") Long id) throws ActivityNotFoundException {
        var atividadeInfo = activityService.get(id);
        return ResponseEntity.ok().body(atividadeInfo);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN_ONG')")
    public ResponseEntity<?> updateActivity(@PathVariable(value = "id") Long id,
                                            @AuthenticationPrincipal Jwt principal,
                                            @RequestBody @Valid UpdateActivityRequest updateActivityRequest)
            throws ActivityNotFoundException, AccessDeniedException {
        Long idAdmin = Long.parseLong(principal.getSubject());
        var atividade = activityService.update(id, idAdmin, updateActivityRequest);
        return ResponseEntity.ok().body(atividade);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN_ONG')")
    public ResponseEntity<?> deleteActivity(@PathVariable(value = "id") Long id, @AuthenticationPrincipal Jwt principal)
            throws ActivityNotFoundException, AccessDeniedException {
        Long idAdmin = Long.parseLong(principal.getSubject());
        activityService.delete(id, idAdmin);
        return ResponseEntity.ok().body("Atividade foi deletada com sucesso");
    }
}

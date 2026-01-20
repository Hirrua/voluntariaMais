package com.svg.voluntariado.controller;

import com.svg.voluntariado.domain.dto.activity.UpdateActivityRequest;
import com.svg.voluntariado.services.ActivityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Minhas atividades", description = "Endpoints de atividades para admin da ONG autenticado")
@RestController
@RequestMapping("/api/me/activities")
public class MeActivityController {

    private final ActivityService activityService;

    public MeActivityController(ActivityService activityService) {
        this.activityService = activityService;
    }

    @Operation(summary = "Atualizar atividade do admin")
    @PatchMapping("/{activityId}")
    @PreAuthorize("hasRole('ADMIN_ONG')")
    public ResponseEntity<?> updateMyActivity(@PathVariable Long activityId,
                                              @RequestBody UpdateActivityRequest updateActivityRequest,
                                              @AuthenticationPrincipal Jwt principal)
            throws com.svg.voluntariado.exceptions.ActivityNotFoundException {
        Long adminId = Long.parseLong(principal.getSubject());
        var update = activityService.update(activityId, adminId, updateActivityRequest);
        return ResponseEntity.ok(update);
    }

    @Operation(summary = "Deletar atividade do admin")
    @DeleteMapping("/{activityId}")
    @PreAuthorize("hasRole('ADMIN_ONG')")
    public ResponseEntity<?> deleteMyActivity(@PathVariable Long activityId,
                                              @AuthenticationPrincipal Jwt principal)
            throws com.svg.voluntariado.exceptions.ActivityNotFoundException {
        Long adminId = Long.parseLong(principal.getSubject());
        activityService.delete(activityId, adminId);
        return ResponseEntity.ok().build();
    }
}

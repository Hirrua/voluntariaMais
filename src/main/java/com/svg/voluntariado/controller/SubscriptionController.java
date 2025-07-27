package com.svg.voluntariado.controller;

import com.svg.voluntariado.exceptions.ActivityNotFoundException;
import com.svg.voluntariado.services.SubscriptionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/inscricao")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    public SubscriptionController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @PostMapping("/{idAtividade}")
    @PreAuthorize("hasRole('VOLUNTARIO')")
    public ResponseEntity<?> subscribe(@PathVariable(value = "idAtividade") Long idAtividade,
                                       @AuthenticationPrincipal Jwt principal) throws ActivityNotFoundException {
        Long idUser = Long.parseLong(principal.getSubject());
        var subscriptionId = subscriptionService.create(idAtividade, idUser);
        return ResponseEntity.created(URI.create("/api/inscricao/" + subscriptionId)).body("Email enviado para confirmação.");
    }

    @GetMapping("/{idAtividade}")
    @PreAuthorize("hasRole('ADMIN_ONG')")
    public ResponseEntity<?> getSubscription(@PathVariable(value = "idAtividade") Long idAtividade) throws ActivityNotFoundException {
        var sub = subscriptionService.getByActivity(idAtividade);
        return ResponseEntity.ok().body(sub);
    }

    @GetMapping("/confirmar")
    public ResponseEntity<?> subscriptionConfirm(@RequestParam("token") String token) {
        try {
            subscriptionService.subscriptionConfirm(token);
            return ResponseEntity.ok("Inscrição confirmada com sucesso!");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}

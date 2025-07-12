package com.svg.voluntariado.controller;

import com.svg.voluntariado.exceptions.ActivityNotFoundException;
import com.svg.voluntariado.services.InscricaoService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/inscricao")
public class InscricaoController {

    private final InscricaoService inscricaoService;

    public InscricaoController(InscricaoService inscricaoService) {
        this.inscricaoService = inscricaoService;
    }

    @PostMapping("/{idAtividade}")
    @PreAuthorize("hasRole('VOLUNTARIO')")
    public ResponseEntity<?> subscribe(@PathVariable(value = "idAtividade") Long idAtividade,
                                       @AuthenticationPrincipal Jwt principal) throws ActivityNotFoundException {
        Long idUser = Long.parseLong(principal.getSubject());
        var subscriptionId = inscricaoService.create(idAtividade, idUser);
        return ResponseEntity.created(URI.create("/api/inscricao/" + subscriptionId)).body("Inscrição realizada com sucesso.");
    }

    @GetMapping("/{idAtividade}")
    @PreAuthorize("hasRole('VOLUNTARIO')")
    public ResponseEntity<?> getSubscription(@PathVariable(value = "idAtividade") Long idAtividade) throws ActivityNotFoundException {
        var sub = inscricaoService.get(idAtividade);
        return ResponseEntity.ok().body(sub);
    }
}

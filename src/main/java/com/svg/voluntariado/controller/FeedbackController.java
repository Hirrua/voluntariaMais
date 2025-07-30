package com.svg.voluntariado.controller;

import com.svg.voluntariado.domain.dto.feedback.InsertFeedbackRequest;
import com.svg.voluntariado.services.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.nio.file.AccessDeniedException;

@RestController
@RequestMapping("/api/feedback")
public class FeedbackController {

    private final FeedbackService feedbackService;

    @Autowired
    public FeedbackController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    @PostMapping("/{idSubscription}")
    @PreAuthorize("hasRole('VOLUNTARIO')")
    public ResponseEntity<?> writeFeedback(@RequestBody InsertFeedbackRequest insertFeedbackRequest,
                                           @PathVariable(value = "idSubscription") Long idSubscription,
                                           @AuthenticationPrincipal Jwt principal) throws AccessDeniedException {
        var idUser = Long.parseLong(principal.getSubject());
        var feedbackResponse = feedbackService.create(insertFeedbackRequest, idSubscription, idUser);
        return ResponseEntity.created(URI.create("/api/feedback/" + feedbackResponse.id())).body("Comentário adicionado com sucesso.");
    }

    @PutMapping("/{idFeedback}")
    @PreAuthorize("hasRole('VOLUNTARIO')")
    public ResponseEntity<?> updateFeedback(@RequestBody InsertFeedbackRequest updateFeedbackRequest,
                                            @PathVariable(value = "idFeedback") Long idFeedback,
                                            @AuthenticationPrincipal Jwt principal) throws AccessDeniedException {
        var idUser = Long.parseLong(principal.getSubject());
        feedbackService.update(updateFeedbackRequest, idFeedback, idUser);
        return ResponseEntity.ok().body("Comentário atualizado");
    }

}

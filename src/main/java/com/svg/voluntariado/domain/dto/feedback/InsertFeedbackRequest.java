package com.svg.voluntariado.domain.dto.feedback;

public record InsertFeedbackRequest(
        Integer nota,
        String comentario
) {
}

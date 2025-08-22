package com.svg.voluntariado.domain.dto.feedback;

import com.svg.voluntariado.domain.dto.user.InfoUserSubscription;

import java.time.OffsetDateTime;

public record InsertFeedbackResponse(
        Long id,
        Integer nota,
        OffsetDateTime dataFeedback,
        InfoUserSubscription infoUserSubscription
) {
}

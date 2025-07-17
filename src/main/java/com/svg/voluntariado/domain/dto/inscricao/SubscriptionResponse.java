package com.svg.voluntariado.domain.dto.inscricao;

import com.svg.voluntariado.domain.dto.user.InfoUserSubscription;
import com.svg.voluntariado.domain.enums.StatusInscricaoEnum;

import java.time.Instant;
import java.time.OffsetDateTime;

public record SubscriptionResponse(
        Long id,
        Instant dataInscricao,
        StatusInscricaoEnum statusInscricaoEnum,
        InfoUserSubscription infoUserSubscription
) {
}

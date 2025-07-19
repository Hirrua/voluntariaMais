package com.svg.voluntariado.domain.dto.subscription;

import com.svg.voluntariado.domain.dto.user.InfoUserSubscription;
import com.svg.voluntariado.domain.enums.StatusInscricaoEnum;

import java.time.Instant;

public record SubscriptionResponse(
        Long id,
        Instant dataInscricao,
        StatusInscricaoEnum statusInscricaoEnum,
        InfoUserSubscription infoUserSubscription
) {
}

package com.svg.voluntariado.domain.dto.subscription;

import com.svg.voluntariado.domain.dto.activity.InfoActivitySubscription;
import com.svg.voluntariado.domain.enums.StatusInscricaoEnum;

import java.time.OffsetDateTime;

public record VolunteerSubscriptionResponse(
        Long id,
        OffsetDateTime dataInscricao,
        StatusInscricaoEnum statusInscricaoEnum,
        InfoActivitySubscription infoActivitySubscription
) {
}

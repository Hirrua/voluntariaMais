package com.svg.voluntariado.projection;

import com.svg.voluntariado.domain.enums.StatusInscricaoEnum;

import java.time.Instant;

public interface SubscriptionProjection {

    Long getIdInscricao();
    Instant getDataInscricao();
    StatusInscricaoEnum getStatus();
    String getUsuarioNome();
    String getUsuarioEmail();
}

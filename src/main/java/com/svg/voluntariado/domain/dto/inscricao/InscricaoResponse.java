package com.svg.voluntariado.domain.dto.inscricao;

import com.svg.voluntariado.domain.dto.atividade.InfoActivitySubscription;
import com.svg.voluntariado.domain.dto.user.InfoUserSubscription;
import com.svg.voluntariado.domain.enums.StatusInscricaoEnum;

import java.time.OffsetDateTime;

public record InscricaoResponse(
        Long id,
        OffsetDateTime dataInscricao,
        StatusInscricaoEnum statusInscricaoEnum,
        InfoUserSubscription infoUserSubscription, // id, nome, e-mail
        InfoActivitySubscription infoActivitySubscription // id, nome
) {
}

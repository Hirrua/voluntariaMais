package com.svg.voluntariado.domain.dto.activity;

import java.time.OffsetDateTime;

public record UpdateActivityRequest(
        String nomeAtividade,
        String descricaoAtividade,
        OffsetDateTime dataHoraInicioAtividade,
        OffsetDateTime dataHoraFimAtividade,
        String localAtividade,
        Integer vagasDisponiveisAtividade
) {
}

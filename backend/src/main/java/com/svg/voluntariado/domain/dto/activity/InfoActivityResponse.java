package com.svg.voluntariado.domain.dto.activity;

import com.svg.voluntariado.domain.dto.ong.OngContextResponse;
import com.svg.voluntariado.domain.dto.project.ProjectContextResponse;

import java.time.OffsetDateTime;

public record InfoActivityResponse(
        Long id,
        String nomeAtividade,
        String descricaoAtividade,
        OffsetDateTime dataHoraInicio,
        OffsetDateTime dataHoraFim,
        String localAtividade,
        Integer vagasDisponiveisAtividade,
        Integer vagasPreenchidasAtividade,
        OffsetDateTime dataCriacao,
        ProjectContextResponse projeto,
        OngContextResponse ong
) {
}

package com.svg.voluntariado.domain.dto.activity;

import com.svg.voluntariado.domain.enums.StatusInscricaoEnum;

import java.time.OffsetDateTime;

public record SimpleInfoActivityResponse(
        Long id,
        String nomeAtividade,
        String descricaoAtividade,
        OffsetDateTime dataHoraInicioAtividade,
        OffsetDateTime dataHoraFimAtividade,
        String localAtividade,
        Integer vagasTotais,
        Integer vagasPreenchidasAtividade,
        OffsetDateTime dataCriacao,
        Long idInscricao,
        StatusInscricaoEnum statusInscricao
) {
}

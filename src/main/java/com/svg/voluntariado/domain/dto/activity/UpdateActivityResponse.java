package com.svg.voluntariado.domain.dto.activity;

import java.time.OffsetDateTime;

public record UpdateActivityResponse(
        Long id,
        String nomeAtividade,
        String descricaoAtividade,
        OffsetDateTime dataHoraInicioAtividade,
        OffsetDateTime dataHoraFimAtividade,
        String localAtividade,
        Integer vagasTotais,
        OffsetDateTime ultimaAtualizacao
) {
}

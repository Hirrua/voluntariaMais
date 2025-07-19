package com.svg.voluntariado.domain.dto.activity;

import java.time.OffsetDateTime;

public record SimpleInfoActivityResponse(
        Long id,
        String nomeAtividade,
        String descricaoAtividade,
        OffsetDateTime dataHoraInicioAtividade,
        OffsetDateTime dataHoraFimAtividade,
        Integer vagasTotais,
        Integer vagasPreenchidasAtividade,
        OffsetDateTime dataCriacao
) {
}

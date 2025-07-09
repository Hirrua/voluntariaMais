package com.svg.voluntariado.domain.dto.atividade;

import java.time.OffsetDateTime;

public record SimpleInfoAtividadeResponse(
        Long id,
        String nomeAtividade,
        String descricaoAtividade,
        OffsetDateTime dataHoraInicioAtividade,
        OffsetDateTime dataHoraFimAtividade,
        Integer vagasDisponiveisAtividade,
        Integer vagasPreenchidasAtividade,
        OffsetDateTime dataCriacao
) {
}

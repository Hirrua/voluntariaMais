package com.svg.voluntariado.domain.dto.atividade;

import java.time.OffsetDateTime;

public record UpdateAtividadeRequest(
        String nomeAtividade,
        String descricaoAtividade,
        OffsetDateTime dataHoraInicioAtividade,
        OffsetDateTime dataHoraFimAtividade,
        String localAtividade,
        Integer vagasDisponiveisAtividade
) {
}

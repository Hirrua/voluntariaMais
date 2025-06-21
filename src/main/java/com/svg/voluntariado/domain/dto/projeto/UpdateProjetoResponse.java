package com.svg.voluntariado.domain.dto.projeto;

import java.time.LocalDate;
import java.time.OffsetDateTime;

public record UpdateProjetoResponse(
        String nome,
        String objetivo,
        String descricaoDetalhada,
        String publicoAlvo,
        String urlImagemDestaque,
        LocalDate dataInicioPrevista,
        LocalDate dataFimPrevista,
        OffsetDateTime dataAtualizacao
) {
}

package com.svg.voluntariado.domain.dto.project;

import java.time.LocalDate;
import java.time.OffsetDateTime;

public record UpdateProjectResponse(
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

package com.svg.voluntariado.domain.dto.project;

import java.time.LocalDate;

public record UpdateProjectRequest(
        String nome,
        String objetivo,
        String descricaoDetalhada,
        String publicoAlvo,
        String urlImagemDestaque,
        LocalDate dataInicioPrevista,
        LocalDate dataFimPrevista
) {
}

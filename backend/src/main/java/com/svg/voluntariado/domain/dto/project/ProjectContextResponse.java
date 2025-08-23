package com.svg.voluntariado.domain.dto.project;

import java.time.LocalDate;

public record ProjectContextResponse(
        String nome,
        String objetivo,
        String publicoAlvo,
        LocalDate dataInicioPrevista,
        LocalDate dataFimPrevista
) {
}

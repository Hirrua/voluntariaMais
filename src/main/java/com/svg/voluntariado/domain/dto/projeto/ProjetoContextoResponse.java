package com.svg.voluntariado.domain.dto.projeto;

import java.time.LocalDate;

public record ProjetoContextoResponse(
        String nome,
        String objetivo,
        String publicoAlvo,
        LocalDate dataInicioPrevista,
        LocalDate dataFimPrevista
) {
}

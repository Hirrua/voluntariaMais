package com.svg.voluntariado.domain.dto.projeto;

import java.time.LocalDate;

public record UpdateProjetoRequest(
        String nome,
        String objetivo,
        String descricaoDetalhada,
        String publicoAlvo,
        String urlImagemDestaque,
        LocalDate dataInicioPrevista,
        LocalDate dataFimPrevista
) {
}

package com.svg.voluntariado.domain.dto.ong;

import java.time.LocalDate;

public record ListOngResponse(
        String nomeOng,
        String descricao,
        String emailContatoOng,
        String website,
        String logoUrl,
        LocalDate dataFundacao
) {
}

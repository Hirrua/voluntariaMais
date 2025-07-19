package com.svg.voluntariado.domain.dto.ong;

import java.time.LocalDate;

public record OngContextResponse(
        String nomeOng,
        String emailContatoOng,
        String telefoneOng,
        LocalDate dataFundacao
) {
}

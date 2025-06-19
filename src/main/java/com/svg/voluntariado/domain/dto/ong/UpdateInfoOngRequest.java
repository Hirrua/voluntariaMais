package com.svg.voluntariado.domain.dto.ong;

public record UpdateInfoOngRequest(
        String nomeOng,
        String descricao,
        String emailContatoOng,
        String telefoneOng,
        String website,
        String logoUrl
) {
}

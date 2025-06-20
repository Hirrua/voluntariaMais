package com.svg.voluntariado.domain.dto.projeto;

public record SimpleInfoProjetoResponse(
        Long id,
        String nome,
        String objetivo,
        String publicoAlvo,
        String urlImagemDestaque
) {
}

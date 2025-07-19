package com.svg.voluntariado.domain.dto.project;

public record SimpleInfoProjectResponse(
        Long id,
        String nome,
        String objetivo,
        String publicoAlvo,
        String urlImagemDestaque
) {
}

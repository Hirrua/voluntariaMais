package com.svg.voluntariado.domain.dto.project;

import com.svg.voluntariado.domain.entities.EnderecoEntity;

import java.time.LocalDate;

public record CreateProjectRequest(
        Long idOng,
        String nome,
        String descricaoDetalhada,
        String objetivo,
        String publicoAlvo,
        LocalDate dataInicioPrevista,
        LocalDate dataFimPrevista,
        EnderecoEntity endereco,
        String urlImagemDestaque
) {}

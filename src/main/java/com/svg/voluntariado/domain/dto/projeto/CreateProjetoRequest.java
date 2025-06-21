package com.svg.voluntariado.domain.dto.projeto;

import com.svg.voluntariado.domain.entities.EnderecoEntity;

import java.time.LocalDate;

public record CreateProjetoRequest(
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

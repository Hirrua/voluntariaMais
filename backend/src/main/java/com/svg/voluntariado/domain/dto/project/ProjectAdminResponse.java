package com.svg.voluntariado.domain.dto.project;

import com.svg.voluntariado.domain.entities.EnderecoEntity;
import com.svg.voluntariado.domain.enums.StatusProjetoEnum;

import java.time.LocalDate;
import java.time.OffsetDateTime;

public record ProjectAdminResponse(
        Long id,
        String nome,
        StatusProjetoEnum status,
        String objetivo,
        String descricaoDetalhada,
        String publicoAlvo,
        LocalDate dataInicioPrevista,
        LocalDate dataFimPrevista,
        EnderecoEntity endereco,
        String urlImagemDestaque,
        OffsetDateTime dataCriacao,
        OffsetDateTime dataAtualizacao
) {
}

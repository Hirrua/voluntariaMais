package com.svg.voluntariado.domain.dto.ong;

import com.svg.voluntariado.domain.dto.project.SimpleInfoProjectResponse;
import com.svg.voluntariado.domain.entities.EnderecoEntity;
import com.svg.voluntariado.domain.enums.StatusAprovacaoOngEnum;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

public record InfoOngAndProjectResponse(
        String nomeOng,
        String descricao,
        String emailContatoOng,
        String telefoneOng,
        String website,
        String logoUrl,
        LocalDate dataFundacao,
        StatusAprovacaoOngEnum status,
        OffsetDateTime dataCriacaoRegistro,
        EnderecoEntity endereco,
        List<SimpleInfoProjectResponse> projectResponse
) {
}

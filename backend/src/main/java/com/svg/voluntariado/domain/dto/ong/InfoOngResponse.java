package com.svg.voluntariado.domain.dto.ong;

import com.svg.voluntariado.domain.entities.EnderecoEntity;
import com.svg.voluntariado.domain.enums.StatusAprovacaoOngEnum;

import java.time.LocalDate;
import java.time.OffsetDateTime;

public record InfoOngResponse(
        Long id,
        String nomeOng,
        String cnpj,
        String descricao,
        String emailContatoOng,
        String website,
        String logoUrl,
        LocalDate dataFundacao,
        StatusAprovacaoOngEnum status,
        OffsetDateTime dataCriacaoRegistro,
        EnderecoEntity endereco
) {
}

package com.svg.voluntariado.domain.dto.ong;

import com.svg.voluntariado.domain.entities.EnderecoEntity;

import java.time.LocalDate;

public record CreateOngRequest(
        Long idUsuarioResponsavel,
        String nomeOng,
        String cnpj,
        String descricao,
        String emailContatoOng,
        String telefoneOng,
        String website,
        String logoUrl,
        LocalDate dataFundacao,
        EnderecoEntity endereco
) {
}

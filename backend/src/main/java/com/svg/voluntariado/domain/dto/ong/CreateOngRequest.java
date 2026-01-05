package com.svg.voluntariado.domain.dto.ong;

import com.svg.voluntariado.domain.entities.EnderecoEntity;

import java.time.LocalDate;

public record CreateOngRequest(
        Long idUsuarioResponsavel, // TODO alterar para e-mail
        String nomeOng,
        String cnpj,
        String descricao,
        String emailContatoOng,
        String telefoneOng,
        String website,
        LocalDate dataFundacao,
        EnderecoEntity endereco
) {
}

package com.svg.voluntariado.domain.dto;

import com.svg.voluntariado.domain.entities.EnderecoEntity;

public record UserRegisterRequest(
        String nome,
        String sobrenome,
        String email,
        String senha,
        String cpf,
        EnderecoEntity endereco
) {
}

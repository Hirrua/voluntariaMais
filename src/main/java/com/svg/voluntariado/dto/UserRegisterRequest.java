package com.svg.voluntariado.dto;

import com.svg.voluntariado.entities.EnderecoEntity;

public record UserRegisterRequest(
        String nome,
        String sobrenome,
        String email,
        String senha,
        String cpf,
        EnderecoEntity endereco
) {
}

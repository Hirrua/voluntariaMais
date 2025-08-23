package com.svg.voluntariado.projection;

import com.svg.voluntariado.domain.entities.EnderecoEntity;

import java.time.Instant;

@Deprecated
public interface UserProjection {
    Long getIdUsuario();
    String getUsuarioNome();
    String getUsuarioSobrenome();
    String getUsuarioEmail();
    String getUsuarioSenha();
    String getUsuarioCpf();
    EnderecoEntity getUsuarioEndereco();
    Instant getUsuarioDataCadastro();
}

package com.svg.voluntariado.domain.dto.profile;

import com.svg.voluntariado.domain.entities.EnderecoEntity;

import java.time.LocalDate;

public record InfoProfileResponse(Long id,
                                  String nome,
                                  String sobrenome,
                                  String email,
                                  String bio,
                                  String disponibilidade,
                                  LocalDate dataNascimento,
                                  String telefoneContato,
                                  String fotoPerfilUrl,
                                  EnderecoEntity endereco) {
}

package com.svg.voluntariado.domain.dto;

import java.time.LocalDate;

public record InfoPerfilResponse(Long id, String bio, String disponibilidade, LocalDate dataNascimento, String telefoneContato) {
}

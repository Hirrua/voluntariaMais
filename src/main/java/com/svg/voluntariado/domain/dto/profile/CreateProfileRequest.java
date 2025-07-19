package com.svg.voluntariado.domain.dto.profile;

import java.time.LocalDate;

public record CreateProfileRequest(Long id_usuario, String bio, String disponibilidade, LocalDate dataNascimento, String telefoneContato) {
}

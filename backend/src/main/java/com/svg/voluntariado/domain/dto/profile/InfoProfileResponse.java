package com.svg.voluntariado.domain.dto.profile;

import java.time.LocalDate;

public record InfoProfileResponse(Long id, String bio, String disponibilidade, LocalDate dataNascimento, String telefoneContato) {
}

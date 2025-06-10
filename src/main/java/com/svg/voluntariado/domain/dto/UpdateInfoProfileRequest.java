package com.svg.voluntariado.domain.dto;

import java.time.LocalDate;

public record UpdateInfoProfileRequest(String bio, String disponibilidade, LocalDate dataNascimento, String telefoneContato) {
}

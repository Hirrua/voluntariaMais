package com.svg.voluntariado.domain.dto.user;

import java.util.List;

public record UserInfoDTO(Long id, String nome, String email, List<String> roles, Long ongId) {
}

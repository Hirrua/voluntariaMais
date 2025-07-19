package com.svg.voluntariado.domain.dto.email;

public record EmailRequest(
    String to,
    String subject,
    String text
) {
}

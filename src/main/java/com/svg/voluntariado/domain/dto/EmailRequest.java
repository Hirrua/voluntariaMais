package com.svg.voluntariado.domain.dto;

public record EmailRequest(
    String to,
    String subject,
    String text
) {
}

package com.svg.voluntariado.domain.dto;

import org.springframework.data.domain.Page;

import java.util.List;

public record PageResponse<T>(
        List<T> content,
        int totalPages,
        long totalElements,
        int number,
        int size
) {
    public static <T, S> PageResponse<T> from(Page<S> page, List<T> content) {
        return new PageResponse<>(
                content,
                page.getTotalPages(),
                page.getTotalElements(),
                page.getNumber(),
                page.getSize()
        );
    }

    public static <T> PageResponse<T> empty(int page, int size) {
        return new PageResponse<>(
                List.of(),
                0,
                0,
                page,
                size
        );
    }
}

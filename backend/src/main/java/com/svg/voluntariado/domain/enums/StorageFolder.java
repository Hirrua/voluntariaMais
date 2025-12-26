package com.svg.voluntariado.domain.enums;

import lombok.Getter;

@Getter
public enum StorageFolder {
    VOLUNTARIO("voluntario"),
    PROJETO("projeto"),
    ONG("ong");

    private final String path;

    StorageFolder(String path) {
        this.path = path;
    }

}

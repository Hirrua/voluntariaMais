package com.svg.voluntariado.domain.enums;

import lombok.Getter;

@Getter
public enum StorageFolder {
    USUARIOS("usuarios"),
    PROJETOS("projetos"),
    ONGS("ongs");

    private final String path;

    StorageFolder(String path) {
        this.path = path;
    }

}

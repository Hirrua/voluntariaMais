package com.svg.voluntariado.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

@Data
@Embeddable
public class EnderecoEntity {

    @Column(length = 60, nullable = false)
    private String logradouro;

    @Column(length = 60, nullable = false)
    private String bairro;

    @Column(length = 60, nullable = false)
    private String complemento;

    @Column(length = 60, nullable = false)
    private String cidade;

    @Column(length = 2, nullable = false)
    private String estado;

    @Column(length = 8, nullable = false)
    private String cep;
}

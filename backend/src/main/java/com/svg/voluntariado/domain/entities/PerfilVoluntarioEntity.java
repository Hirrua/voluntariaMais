package com.svg.voluntariado.domain.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@EqualsAndHashCode(of = "id")
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tb_perfil_voluntario")
public class PerfilVoluntarioEntity {

    @Id
    @Column(name = "id_usuario")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "id_usuario", referencedColumnName = "id_usuario")
    private UsuarioEntity usuario;

    private String bio;

    @Column(length = 30)
    private String disponibilidade;

    @Temporal(TemporalType.DATE)
    @Column(name = "data_nascimento")
    private LocalDate dataNascimento;

    @Column(name = "telefone_contato", length = 11)
    private String telefoneContato;

    @Column(name = "foto_perfil_url", length = 512)
    private String fotoPerfilUrl;

    public PerfilVoluntarioEntity(String bio, String disponibilidade, LocalDate dataNascimento, String telefoneContato, String fotoPerfilUrl) {
        this.bio = bio;
        this.disponibilidade = disponibilidade;
        this.dataNascimento = dataNascimento;
        this.telefoneContato = telefoneContato;
        this.fotoPerfilUrl = fotoPerfilUrl;
    }
}

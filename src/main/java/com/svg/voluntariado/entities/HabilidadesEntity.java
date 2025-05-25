package com.svg.voluntariado.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@EqualsAndHashCode(of = "id")
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tb_habilidades")
public class HabilidadesEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_habilidade")
    private Long id;

    @Column(name = "nome_habilidade", nullable = false, unique = true)
    private String nomeHabilidade;

    @Column(name = "descricao_habilidade")
    private String descricaoHabilidade;

    @ManyToMany(mappedBy = "habilidades")
    private Set<UsuarioEntity> usuarios = new HashSet<>();

}

package com.svg.voluntariado.domain.entities;

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
@Table(name = "tb_roles")
public class RoleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(length = 30, nullable = false, unique = true)
    private String nome;

    @ManyToMany(mappedBy = "roles")
    private Set<UsuarioEntity> usuarios = new HashSet<>();
}

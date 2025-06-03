package com.svg.voluntariado.domain.entities;

import com.svg.voluntariado.domain.enums.StatusInscricaoEnum;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;

@Getter
@Setter
@EqualsAndHashCode(of = "id")
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tb_inscricao")
public class InscricaoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_inscricao")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private UsuarioEntity usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_atividade", nullable = false)
    private AtividadeEntity atividade;

    @CreationTimestamp
    @Column(name = "data_inscricao", nullable = false, updatable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private OffsetDateTime dataInscricao;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_inscricao", nullable = false, columnDefinition = "status_inscricao_enum")
    private StatusInscricaoEnum status = StatusInscricaoEnum.PENDENTE;

    @OneToOne(mappedBy = "inscricao", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private FeedbackEntity feedback;

}

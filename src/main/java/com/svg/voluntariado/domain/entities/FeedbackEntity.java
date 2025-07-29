package com.svg.voluntariado.domain.entities;

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
@Table(name = "tb_feedback")
public class FeedbackEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_feedback")
    private Long id;

    @Column(nullable = false)
    private Integer nota;

    @Column(columnDefinition = "TEXT")
    private String comentario;

    @CreationTimestamp
    @Column(name = "data_feedback", nullable = false, updatable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private OffsetDateTime dataFeedback;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_inscricao", nullable = false, unique = true)
    private InscricaoEntity inscricao;

    public FeedbackEntity(Integer nota, String comentario, InscricaoEntity inscricao) {
        this.nota = nota;
        this.comentario = comentario;
        this.dataFeedback = OffsetDateTime.now();
        this.inscricao = inscricao;
    }
}

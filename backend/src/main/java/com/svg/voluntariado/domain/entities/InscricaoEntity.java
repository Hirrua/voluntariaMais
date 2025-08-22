package com.svg.voluntariado.domain.entities;

import com.svg.voluntariado.domain.enums.StatusInscricaoEnum;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@EqualsAndHashCode(of = "id")
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
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "status_inscricao")
    private StatusInscricaoEnum status = StatusInscricaoEnum.PENDENTE;

    @OneToOne(mappedBy = "inscricao", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private FeedbackEntity feedback;

    @Column(name = "token_confirmacao")
    private String tokenConfirmacao;

    @Column(name = "data_expiracao_token", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private OffsetDateTime dataExpiracaoToken;

    public InscricaoEntity(UsuarioEntity usuario, AtividadeEntity atividade) {
        this.usuario = usuario;
        this.atividade = atividade;
        this.tokenConfirmacao = UUID.randomUUID().toString();
        this.dataExpiracaoToken = OffsetDateTime.now().plusHours(24);
    }
}

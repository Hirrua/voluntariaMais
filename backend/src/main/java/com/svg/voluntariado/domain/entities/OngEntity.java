package com.svg.voluntariado.domain.entities;

import com.svg.voluntariado.domain.enums.StatusAprovacaoOngEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@EqualsAndHashCode(of = "id")
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tb_ong")
public class OngEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ong")
    private Long id;

    @Column(name = "nome_ong", nullable = false)
    private String nomeOng;

    @Column(nullable = false, unique = true, length = 14)
    private String cnpj;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "logradouro", column = @Column(name = "logradouro_ong", length = 60)),
            @AttributeOverride(name = "bairro", column = @Column(name = "bairro_ong", length = 60)),
            @AttributeOverride(name = "complemento", column = @Column(name = "complemento_ong", length = 60)),
            @AttributeOverride(name = "cidade", column = @Column(name = "cidade_ong", length = 60)),
            @AttributeOverride(name = "estado", column = @Column(name = "estado_ong", length = 2)),
            @AttributeOverride(name = "cep", column = @Column(name = "cep_ong", length = 8)),
    })
    private EnderecoEntity endereco;

    @Email
    @Column(name = "email_contato_ong", unique = true, nullable = false, length = 30)
    private String emailContatoOng;

    @Column(name = "telefone_ong", length = 20)
    private String telefoneOng;

    private String website;

    @Column(name = "logo_url", length = 512)
    private String logoUrl;

    @Column(name = "data_fundacao")
    private LocalDate dataFundacao;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "status_aprovacao")
    private StatusAprovacaoOngEnum status = StatusAprovacaoOngEnum.PENDENTE;

    @Column(name = "token_aprovacao")
    private String tokenAprovacao;

    @Column(name = "data_expiracao_aprovacao", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private OffsetDateTime dataExpiracaoAprovacao;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario_responsavel", nullable = false, unique = true)
    private UsuarioEntity usuarioResponsavel;

    @CreationTimestamp
    @Column(name = "data_criacao_registro", nullable = false, updatable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private OffsetDateTime dataCriacaoRegistro;

    @Column(name = "data_aprovacao_rejeicao", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private OffsetDateTime dataAprovacaoRejeicao;

    @OneToMany(mappedBy = "ong", orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<ProjetoEntity> projetos = new HashSet<>();
}

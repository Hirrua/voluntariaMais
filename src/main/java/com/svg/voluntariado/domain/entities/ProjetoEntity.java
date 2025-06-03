package com.svg.voluntariado.domain.entities;

import com.svg.voluntariado.domain.enums.StatusProjetoEnum;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "tb_projeto")
public class ProjetoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_projeto")
    private Long id;

    @Column(name = "nome_projeto", nullable = false)
    private String nome;

    @Column(columnDefinition = "TEXT")
    private String descricaoDetalhada;

    @Column(columnDefinition = "TEXT")
    private String objetivo;

    @Column(name = "publico_alvo")
    private String publicoAlvo;

    @Column(name = "data_inicio_prevista")
    private LocalDate dataInicioPrevista;

    @Column(name = "data_fim_prevista")
    private LocalDate dataFimPrevista;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_projeto")
    private StatusProjetoEnum status  = StatusProjetoEnum.PLANEJAMENTO;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "logradouro", column = @Column(name = "logradouro_projeto", length = 60)),
            @AttributeOverride(name = "bairro", column = @Column(name = "bairro_projeto", length = 60)),
            @AttributeOverride(name = "complemento", column = @Column(name = "complemento_projeto", length = 60)),
            @AttributeOverride(name = "cidade", column = @Column(name = "cidade_projeto", length = 60)),
            @AttributeOverride(name = "estado", column = @Column(name = "estado_projeto", length = 2)),
            @AttributeOverride(name = "cep", column = @Column(name = "cep_projeto", length = 8))
    })
    private EnderecoEntity endereco;

    @Column(name = "url_imagem_destaque", length = 512)
    private String urlImagemDestaque;

    @CreationTimestamp
    @Column(name = "data_criacao", nullable = false, updatable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP")
    private OffsetDateTime dataCriacao;

    @Column(name = "data_atualizacao", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private OffsetDateTime dataAtualizacao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ong", nullable = false)
    private OngEntity ong;

    @OneToMany(mappedBy = "projeto", fetch = FetchType.LAZY, orphanRemoval = true, cascade = CascadeType.ALL)
    private Set<AtividadeEntity> atividades = new HashSet<>();
}

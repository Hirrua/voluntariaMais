package com.svg.voluntariado.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@EqualsAndHashCode(of = "id")
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tb_atividade")
public class AtividadeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_atividade")
    private Long id;

    @Column(name = "nome_atividade", nullable = false)
    private String nomeAtividade;

    @Column(name = "descricao_atividade", columnDefinition = "TEXT")
    private String descricaoAtividade;

    @Column(name = "data_hora_inicio_atividade", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private OffsetDateTime dataHoraInicioAtividade;

    @Column(name = "data_hora_fim_atividade", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private OffsetDateTime dataHoraFimAtividade;

    @Column(name = "local_atividade")
    private String localAtividade;

    @Column(name = "vagas_disponiveis_atividade")
    private Integer vagasDisponiveisAtividade;

    @Column(name = "vagas_preenchidas_atividade")
    private Integer vagasPreenchidasAtividade = 0;

    @CreationTimestamp
    @Column(name = "data_criacao", updatable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,")
    private OffsetDateTime dataCriacao;

    @Column(name = "ultima_atualizacao", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private OffsetDateTime ultimaAtualizacao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_projeto", nullable = false)
    private ProjetoEntity projeto;

    @OneToMany(mappedBy = "atividade", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<InscricaoEntity> inscricao = new HashSet<>();
}

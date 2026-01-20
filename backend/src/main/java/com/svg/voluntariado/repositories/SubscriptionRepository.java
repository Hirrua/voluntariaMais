package com.svg.voluntariado.repositories;

import com.svg.voluntariado.domain.entities.InscricaoEntity;
import com.svg.voluntariado.projection.SubscriptionProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SubscriptionRepository extends JpaRepository<InscricaoEntity, Long> {

    @Query(value = """
            SELECT
                i.id_inscricao AS idInscricao,
                i.data_inscricao AS dataInscricao,
                i.status_inscricao AS status,
                u.id AS usuarioId,
                u.nome AS usuarioNome,
                u.email AS usuarioEmail
            FROM tb_inscricao i
            JOIN tb_usuarios u ON i.id_usuario = u.id_usuario
            WHERE i.id_atividade = :idAtividade""",
            nativeQuery = true)
    List<SubscriptionProjection> findSubscriptionFlatten(Long idAtividade);

    Optional<InscricaoEntity> findByTokenConfirmacao(String token);

    @Query(value = """
            SELECT
                i.id_inscricao AS idInscricao,
                i.status_inscricao AS status,
                u.id AS usuarioId,
                u.nome AS usuarioNome,
                u.email AS usuarioEmail
            FROM tb_inscricao i
            JOIN tb_usuarios u ON i.id_usuario = u.id_usuario
            WHERE i.id_inscricao = :id_inscricao""",
            nativeQuery = true)
    Optional<SubscriptionProjection> findOneSubscriptionFlatten(Long id_inscricao);

    List<InscricaoEntity> findByUsuarioIdAndAtividadeIdIn(Long idUsuario, List<Long> idsAtividade);

    List<InscricaoEntity> findByUsuarioId(Long idUsuario);
}

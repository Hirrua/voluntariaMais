package com.svg.voluntariado.repositories;

import com.svg.voluntariado.domain.entities.InscricaoEntity;
import com.svg.voluntariado.projection.SubscriptionProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SubscriptionRepository extends JpaRepository<InscricaoEntity, Long> {

    @Query(value = """
            SELECT
                i.id_inscricao AS idInscricao, 
                i.data_inscricao AS dataInscricao,
                i.status_inscricao AS status,
                u.nome AS usuarioNome,
                u.email AS usuarioEmail
            FROM tb_inscricao i
            JOIN tb_usuarios u ON i.id_usuario = u.id_usuario
            WHERE i.id_atividade = :idAtividade""",
            nativeQuery = true)
    List<SubscriptionProjection> findSubscriptionFlatten(Long idAtividade);

}

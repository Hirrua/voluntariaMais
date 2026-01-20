package com.svg.voluntariado.repositories;

import com.svg.voluntariado.domain.entities.ProjetoEntity;
import com.svg.voluntariado.domain.enums.StatusAprovacaoOngEnum;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProjectRepository extends JpaRepository<ProjetoEntity, Long> {

    @NotNull Page<ProjetoEntity> findAll(@NotNull Pageable pageable);

    @NotNull Page<ProjetoEntity> findAllByOngStatus(@NotNull StatusAprovacaoOngEnum status, @NotNull Pageable pageable);

    @EntityGraph(value = "Projeto.ong.atividade", type = EntityGraph.EntityGraphType.FETCH)
    Optional<ProjetoEntity> findById(@NotNull Long id);

    Page<ProjetoEntity> findByOngUsuarioResponsavelId(Long usuarioResponsavelId, Pageable pageable);
}

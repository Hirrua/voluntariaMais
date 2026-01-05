package com.svg.voluntariado.repositories;

import com.svg.voluntariado.domain.entities.OngEntity;
import com.svg.voluntariado.domain.enums.StatusAprovacaoOngEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OngRepository extends JpaRepository<OngEntity, Long> {

    Page<OngEntity> findAll(Pageable pageable);

    Page<OngEntity> findAllByStatus(StatusAprovacaoOngEnum status, Pageable pageable);

    Optional<OngEntity> findByIdAndStatus(Long id, StatusAprovacaoOngEnum status);

    Optional<OngEntity> findByTokenAprovacao(String tokenAprovacao);
}

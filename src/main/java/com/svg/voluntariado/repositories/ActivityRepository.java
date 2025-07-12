package com.svg.voluntariado.repositories;

import com.svg.voluntariado.domain.entities.AtividadeEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActivityRepository extends JpaRepository<AtividadeEntity, Long> {
    Page<AtividadeEntity> findAll(Pageable pageable);
}

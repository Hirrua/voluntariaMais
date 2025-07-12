package com.svg.voluntariado.repositories;

import com.svg.voluntariado.domain.entities.ProjetoEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<ProjetoEntity, Long> {

    Page<ProjetoEntity> findAll(Pageable pageable);
}

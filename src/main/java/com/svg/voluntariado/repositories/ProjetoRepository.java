package com.svg.voluntariado.repositories;

import com.svg.voluntariado.domain.entities.ProjetoEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjetoRepository extends JpaRepository<ProjetoEntity, Long> {

    Page<ProjetoEntity> findAll(Pageable pageable);
}

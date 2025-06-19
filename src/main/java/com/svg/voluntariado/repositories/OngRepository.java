package com.svg.voluntariado.repositories;

import com.svg.voluntariado.domain.entities.OngEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OngRepository extends JpaRepository<OngEntity, Long> {

    Page<OngEntity> findAll(Pageable pageable);
}

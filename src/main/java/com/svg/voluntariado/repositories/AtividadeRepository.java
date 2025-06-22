package com.svg.voluntariado.repositories;

import com.svg.voluntariado.domain.entities.AtividadeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AtividadeRepository extends JpaRepository<AtividadeEntity, Long> {
}

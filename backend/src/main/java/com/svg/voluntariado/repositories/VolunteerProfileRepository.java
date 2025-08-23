package com.svg.voluntariado.repositories;

import com.svg.voluntariado.domain.entities.PerfilVoluntarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VolunteerProfileRepository extends JpaRepository<PerfilVoluntarioEntity, Long> {
}

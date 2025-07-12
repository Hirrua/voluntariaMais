package com.svg.voluntariado.repositories;

import com.svg.voluntariado.domain.entities.InscricaoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscriptionRepository extends JpaRepository<InscricaoEntity, Long> {
}

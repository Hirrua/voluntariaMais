package com.svg.voluntariado.repositories;

import com.svg.voluntariado.domain.entities.FeedbackEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedbackRepository extends JpaRepository<FeedbackEntity, Long> {
}

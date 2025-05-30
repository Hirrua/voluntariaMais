package com.svg.voluntariado.repositories;

import com.svg.voluntariado.entities.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<RoleEntity, Long> {
    RoleEntity findByNome(String nome);
}

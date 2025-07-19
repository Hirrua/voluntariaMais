package com.svg.voluntariado.repositories;

import com.svg.voluntariado.domain.entities.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UsuarioEntity, Long> {

    Optional<UsuarioEntity> findByEmail(String email);
    Optional<UsuarioEntity> findByCpf(String cpf);

    @Query("SELECT u FROM UsuarioEntity u JOIN FETCH u.perfilVoluntario pv WHERE u.id = :id")
    Optional<UsuarioEntity> findByIdIfProfileExists(@Param("id") Long id);
}

package com.svg.voluntariado.repositories;

import com.svg.voluntariado.domain.entities.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UsuarioEntity, Long> {

    Optional<UsuarioEntity> findByEmail(String email);
    Optional<UsuarioEntity> findByCpf(String cpf);
    Optional<UsuarioEntity> findByTokenConfirmacao(String tokenConfirmacao);

    @Query("SELECT u FROM UsuarioEntity u JOIN FETCH u.perfilVoluntario pv WHERE u.id = :id")
    Optional<UsuarioEntity> findByIdIfProfileExists(@Param("id") Long id);

    @Query("SELECT DISTINCT u FROM UsuarioEntity u LEFT JOIN FETCH u.roles WHERE u.id = :id")
    Optional<UsuarioEntity> findByIdWithRoles(@Param("id") Long id);
}

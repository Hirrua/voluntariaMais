package com.svg.voluntariado.mapper;

import com.svg.voluntariado.domain.dto.user.UserRegisterRequest;
import com.svg.voluntariado.domain.entities.UsuarioEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "ativo", ignore = true)
    @Mapping(target = "statusUsuario", ignore = true)
    @Mapping(target = "tokenConfirmacao", ignore = true)
    @Mapping(target = "dataExpiracaoToken", ignore = true)
    @Mapping(target = "dataCadastro", ignore = true)
    @Mapping(target = "perfilVoluntario", ignore = true)
    @Mapping(target = "habilidades", ignore = true)
    @Mapping(target = "inscricao", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "authorities", ignore = true)
    UsuarioEntity toUsuarioEntity(UserRegisterRequest registerRequest);
}

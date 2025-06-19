package com.svg.voluntariado.domain.mapper;

import com.svg.voluntariado.domain.dto.UserRegisterRequest;
import com.svg.voluntariado.domain.entities.UsuarioEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UsuarioEntity toUsuarioEntity(UserRegisterRequest registerRequest);
}

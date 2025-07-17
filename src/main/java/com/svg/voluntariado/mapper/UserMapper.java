package com.svg.voluntariado.mapper;

import com.svg.voluntariado.domain.dto.user.UserRegisterRequest;
import com.svg.voluntariado.domain.entities.UsuarioEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UsuarioEntity toUsuarioEntity(UserRegisterRequest registerRequest);
}

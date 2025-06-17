package com.svg.voluntariado.domain.mapper;

import com.svg.voluntariado.domain.dto.CreateProfileRequest;
import com.svg.voluntariado.domain.dto.InfoPerfilResponse;
import com.svg.voluntariado.domain.dto.UpdateInfoProfileRequest;
import com.svg.voluntariado.domain.entities.PerfilVoluntarioEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProfileMapper {

    @Mapping(target = "id", ignore = true)
    PerfilVoluntarioEntity toPerfilVoluntarioEntity(CreateProfileRequest createProfileRequest);

    InfoPerfilResponse toInfoPerfilResponse(PerfilVoluntarioEntity perfil);

    @Mapping(target = "id", ignore = true)
    PerfilVoluntarioEntity toPerfilVoluntarioEntity(UpdateInfoProfileRequest update, @MappingTarget PerfilVoluntarioEntity perfil);


}

package com.svg.voluntariado.mapper;

import com.svg.voluntariado.domain.dto.profile.CreateProfileRequest;
import com.svg.voluntariado.domain.dto.profile.InfoProfileResponse;
import com.svg.voluntariado.domain.dto.profile.UpdateInfoProfileRequest;
import com.svg.voluntariado.domain.entities.PerfilVoluntarioEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface ProfileMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "usuario", ignore = true)
    PerfilVoluntarioEntity toPerfilVoluntarioEntity(CreateProfileRequest createProfileRequest);

    @Mapping(source = "usuario.nome", target = "nome")
    @Mapping(source = "usuario.sobrenome", target = "sobrenome")
    @Mapping(source = "usuario.email", target = "email")
    @Mapping(source = "usuario.endereco", target = "endereco")
    InfoProfileResponse toInfoPerfilResponse(PerfilVoluntarioEntity perfil);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "usuario", ignore = true)
    PerfilVoluntarioEntity toPerfilVoluntarioEntity(UpdateInfoProfileRequest update, @MappingTarget PerfilVoluntarioEntity perfil);

}

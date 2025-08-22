package com.svg.voluntariado.mapper;

import com.svg.voluntariado.domain.dto.project.*;
import com.svg.voluntariado.domain.entities.ProjetoEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface ProjectMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "dataCriacao", ignore = true)
    @Mapping(target = "dataAtualizacao", ignore = true)
    @Mapping(target = "atividades", ignore = true)
    @Mapping(target = "ong", ignore = true)
    ProjetoEntity toProjetoEntity(CreateProjectRequest createProjectRequest);

    List<SimpleInfoProjectResponse> toSimpleInfoProjetoResponse(Page<ProjetoEntity> entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "atividades", ignore = true)
    @Mapping(target = "ong", ignore = true)
    @Mapping(target = "endereco", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "dataCriacao", ignore = true)
    @Mapping(target = "dataAtualizacao", ignore = true)
    ProjetoEntity toProjetoEntity(UpdateProjectRequest updateProjectRequest, @MappingTarget ProjetoEntity projeto);

    UpdateProjectResponse toUpdateProjetoResponse(ProjetoEntity entity);

    ProjectContextResponse toProjetoContextoResponse(ProjetoEntity entity);
}

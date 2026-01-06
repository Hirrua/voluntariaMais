package com.svg.voluntariado.mapper;

import com.svg.voluntariado.domain.dto.activity.CreateActivityRequest;
import com.svg.voluntariado.domain.dto.activity.SimpleInfoActivityResponse;
import com.svg.voluntariado.domain.dto.activity.UpdateActivityRequest;
import com.svg.voluntariado.domain.dto.activity.UpdateActivityResponse;
import com.svg.voluntariado.domain.entities.AtividadeEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface ActivityMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "dataCriacao", ignore = true)
    @Mapping(target = "ultimaAtualizacao", ignore = true)
    @Mapping(target = "vagasPreenchidasAtividade", ignore = true)
    @Mapping(target = "inscricao", ignore = true)
    @Mapping(target = "projeto", ignore = true)
    @Mapping(target = "version", ignore = true)
    AtividadeEntity toAtividadeEntity(CreateActivityRequest createActivityRequest);

    @Mapping(target = "vagasTotais", expression = "java(entity.getVagasDisponiveis())")
    List<SimpleInfoActivityResponse> toSimpleInfoAtividadeResponse(Page<AtividadeEntity> entities);

    UpdateActivityResponse toUpdateAtividadeResponse(AtividadeEntity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "dataCriacao", ignore = true)
    @Mapping(target = "vagasPreenchidasAtividade", ignore = true)
    @Mapping(target = "inscricao", ignore = true)
    @Mapping(target = "projeto", ignore = true)
    @Mapping(target = "ultimaAtualizacao", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "vagasTotais", ignore = true)
    AtividadeEntity toAtividadeEntity(UpdateActivityRequest atividadeRequest, @MappingTarget AtividadeEntity entity);

    @Mapping(target = "vagasTotais", expression = "java(entity.getVagasDisponiveis())")
    Set<SimpleInfoActivityResponse> toSimpleInfoAtividadeResponse(Set<AtividadeEntity> entity);

    @Mapping(target = "vagasTotais", expression = "java(entity.getVagasDisponiveis())")
    List<SimpleInfoActivityResponse> toSimpleInfoAtividadeResponseList(List<AtividadeEntity> entity);
}

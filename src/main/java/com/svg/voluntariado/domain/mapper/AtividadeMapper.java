package com.svg.voluntariado.domain.mapper;

import com.svg.voluntariado.domain.dto.atividade.CreateAtividadeRequest;
import com.svg.voluntariado.domain.entities.AtividadeEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface AtividadeMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "dataCriacao", ignore = true)
    @Mapping(target = "ultimaAtualizacao", ignore = true)
    @Mapping(target = "vagasPreenchidasAtividade", ignore = true)
    @Mapping(target = "inscricao", ignore = true)
    @Mapping(target = "projeto", ignore = true)
    AtividadeEntity toAtividadeEntity(CreateAtividadeRequest createAtividadeRequest);
}

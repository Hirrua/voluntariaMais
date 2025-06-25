package com.svg.voluntariado.domain.mapper;

import com.svg.voluntariado.domain.dto.atividade.CreateAtividadeRequest;
import com.svg.voluntariado.domain.dto.atividade.SimpleInfoAtividadeResponse;
import com.svg.voluntariado.domain.entities.AtividadeEntity;
import com.svg.voluntariado.domain.entities.ProjetoEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface AtividadeMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "dataCriacao", ignore = true)
    @Mapping(target = "ultimaAtualizacao", ignore = true)
    @Mapping(target = "vagasPreenchidasAtividade", ignore = true)
    @Mapping(target = "inscricao", ignore = true)
    @Mapping(target = "projeto", ignore = true)
    AtividadeEntity toAtividadeEntity(CreateAtividadeRequest createAtividadeRequest);

    List<SimpleInfoAtividadeResponse> toSimpleInfoAtividadeResponse(Page<AtividadeEntity> entities);
}

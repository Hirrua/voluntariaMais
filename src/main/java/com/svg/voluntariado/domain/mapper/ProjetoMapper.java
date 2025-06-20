package com.svg.voluntariado.domain.mapper;

import com.svg.voluntariado.domain.dto.projeto.CreateProjetoRequest;
import com.svg.voluntariado.domain.dto.projeto.SimpleInfoProjetoResponse;
import com.svg.voluntariado.domain.entities.ProjetoEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface ProjetoMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "dataCriacao", ignore = true)
    @Mapping(target = "dataAtualizacao", ignore = true)
    @Mapping(target = "atividades", ignore = true)
    @Mapping(target = "ong", ignore = true)
    ProjetoEntity toProjetoEntity(CreateProjetoRequest createProjetoRequest);

    @Mapping(target = "descricaoDetalhada", ignore = true)
    @Mapping(target = "dataInicioPrevista", ignore = true)
    @Mapping(target = "dataFimPrevista", ignore = true)
    @Mapping(target = "ong", ignore = true)
    @Mapping(target = "atividades", ignore = true)
    List<SimpleInfoProjetoResponse> toSimpleInfoProjetoResponse(Page<ProjetoEntity> entity);
}

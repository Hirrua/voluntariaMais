package com.svg.voluntariado.mapper;

import com.svg.voluntariado.domain.dto.ong.*;
import com.svg.voluntariado.domain.dto.project.SimpleInfoProjectResponse;
import com.svg.voluntariado.domain.entities.OngEntity;
import com.svg.voluntariado.domain.entities.ProjetoEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface OngMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "logoUrl", ignore = true)
    @Mapping(target = "tokenAprovacao", ignore = true)
    @Mapping(target = "dataExpiracaoAprovacao", ignore = true)
    @Mapping(target = "dataCriacaoRegistro", ignore = true)
    @Mapping(target = "dataAprovacaoRejeicao", ignore = true)
    @Mapping(target = "projetos", ignore = true)
    @Mapping(target = "usuarioResponsavel", ignore = true)
    OngEntity toOngEntity(CreateOngRequest createOngRequest);

    InfoOngResponse toInfoOngResponse(OngEntity ongEntity);

    List<ListOngResponse> toListOngResponse(Page<OngEntity> ongEntity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "cnpj", ignore = true)
    @Mapping(target = "endereco", ignore = true)
    @Mapping(target = "dataFundacao", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "tokenAprovacao", ignore = true)
    @Mapping(target = "dataExpiracaoAprovacao", ignore = true)
    @Mapping(target = "dataCriacaoRegistro", ignore = true)
    @Mapping(target = "dataAprovacaoRejeicao", ignore = true)
    @Mapping(target = "projetos", ignore = true)
    @Mapping(target = "usuarioResponsavel", ignore = true)
    OngEntity toOngEntity(UpdateInfoOngRequest updateInfoOngRequest, @MappingTarget OngEntity ongEntity);

    OngContextResponse toOngContextoResponse(OngEntity ong);

    @Mapping(source = "projetos", target = "projectResponse")
    InfoOngAndProjectResponse toInfoOngAndProjectResponse(OngEntity ong);
    SimpleInfoProjectResponse toSimpleInfoProjectResponse(ProjetoEntity projeto);
}

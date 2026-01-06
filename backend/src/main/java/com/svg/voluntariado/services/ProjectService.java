package com.svg.voluntariado.services;

import com.svg.voluntariado.domain.dto.PageResponse;
import com.svg.voluntariado.domain.dto.activity.SimpleInfoActivityResponse;
import com.svg.voluntariado.domain.dto.ong.OngContextResponse;
import com.svg.voluntariado.domain.dto.project.*;
import com.svg.voluntariado.domain.entities.AtividadeEntity;
import com.svg.voluntariado.domain.entities.OngEntity;
import com.svg.voluntariado.domain.entities.ProjetoEntity;
import com.svg.voluntariado.domain.enums.StatusAprovacaoOngEnum;
import com.svg.voluntariado.mapper.ActivityMapper;
import com.svg.voluntariado.mapper.OngMapper;
import com.svg.voluntariado.mapper.ProjectMapper;
import com.svg.voluntariado.exceptions.OngNotFoundException;
import com.svg.voluntariado.exceptions.ProjectNotFoundException;
import com.svg.voluntariado.repositories.OngRepository;
import com.svg.voluntariado.repositories.ProjectRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final OngRepository ongRepository;
    private final ProjectMapper projectMapper;
    private final OngMapper ongMapper;
    private final ActivityMapper activityMapper;

    public ProjectService(ProjectRepository projectRepository, OngRepository ongRepository, ProjectMapper projectMapper,
                          OngMapper ongMapper, ActivityMapper activityMapper) {
        this.projectRepository = projectRepository;
        this.ongRepository = ongRepository;
        this.projectMapper = projectMapper;
        this.ongMapper = ongMapper;
        this.activityMapper = activityMapper;
    }

    @Transactional
    public Long create(CreateProjectRequest createProjectRequest, Long idAdmin, boolean isAdminPlataforma) {
        var ong = ongRepository.findById(createProjectRequest.idOng())
                .orElseThrow(OngNotFoundException::new);

        if (!StatusAprovacaoOngEnum.APROVADA.equals(ong.getStatus())) {
            throw new AccessDeniedException("A ONG precisa estar aprovada para criar projetos.");
        }

        if (!isAdminPlataforma && !ong.getUsuarioResponsavel().getId().equals(idAdmin)) {
            throw new AccessDeniedException("Apenas o admin da ong pode editar.");
        }

        var projeto = projectMapper.toProjetoEntity(createProjectRequest);
        projeto.setOng(ong);
        projectRepository.save(projeto);
        return projeto.getId();
    }

    @Transactional(readOnly = true)
    public PageResponse<SimpleInfoProjectResponse> getAll(int page, int itens) {
        var projetos = projectRepository.findAll(PageRequest.of(page, itens));

        if (projetos.isEmpty()) {
            return PageResponse.empty(page, itens);
        }

        var projetosDto = projectMapper.toSimpleInfoProjetoResponse(projetos);
        return PageResponse.from(projetos, projetosDto);
    }

    @Transactional(readOnly = true)
    public OngProjectAndActivityInfo getOngProjectAndActivityInfo(Long idProject) {
        ProjetoEntity projectInfo = projectRepository.findById(idProject).orElseThrow(ProjectNotFoundException::new);
        OngEntity ongInfo = projectInfo.getOng();
        List<AtividadeEntity> activitiesOrdered = projectInfo.getAtividades().stream()
                .sorted(Comparator.comparing(AtividadeEntity::getDataHoraInicioAtividade))
                .toList();

        SimpleInfoProjectResponse projectMap = projectMapper.toSimpleInfoProjetoResponse(projectInfo);
        OngContextResponse ongMap = ongMapper.toOngContextoResponse(ongInfo);
        List<SimpleInfoActivityResponse> activitiesMap = activityMapper.toSimpleInfoAtividadeResponseList(activitiesOrdered);

        return new OngProjectAndActivityInfo(
                projectMap,
                ongMap,
                activitiesMap
        );
    }

    @Transactional
    public UpdateProjectResponse update(Long idProjeto, Long idAdmin, boolean isAdminPlataforma, UpdateProjectRequest updateProjectRequest) {
        var projeto = projectRepository.findById(idProjeto).orElseThrow(ProjectNotFoundException::new);
        var ong = ongRepository.findById(projeto.getOng().getId())
                .orElseThrow(OngNotFoundException::new);

        if (!isAdminPlataforma && !ong.getUsuarioResponsavel().getId().equals(idAdmin)) {
            throw new AccessDeniedException("Apenas o admin da ong pode editar.");
        }

        var projetoMap = projectMapper.toProjetoEntity(updateProjectRequest, projeto);
        projetoMap.setDataAtualizacao(OffsetDateTime.now());
        projectRepository.save(projetoMap);
        return projectMapper.toUpdateProjetoResponse(projetoMap);
    }

    @Transactional
    public void delete(Long idProjeto, Long idAdmin, boolean isAdminPlataforma) {
        var projeto = projectRepository.findById(idProjeto).orElseThrow(ProjectNotFoundException::new);

        if (!isAdminPlataforma) {
            var ong = ongRepository.findById(projeto.getOng().getId())
                    .orElseThrow(OngNotFoundException::new);

            if (!ong.getUsuarioResponsavel().getId().equals(idAdmin)) {
                throw new AccessDeniedException("Apenas o admin da ong pode excluir.");
            }
        }

        projectRepository.delete(projeto);
    }
}

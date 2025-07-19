package com.svg.voluntariado.services;

import com.svg.voluntariado.domain.dto.project.CreateProjectRequest;
import com.svg.voluntariado.domain.dto.project.SimpleInfoProjectResponse;
import com.svg.voluntariado.domain.dto.project.UpdateProjectRequest;
import com.svg.voluntariado.domain.dto.project.UpdateProjectResponse;
import com.svg.voluntariado.mapper.ProjectMapper;
import com.svg.voluntariado.exceptions.OngNotFoundException;
import com.svg.voluntariado.exceptions.ProjectNotFoundException;
import com.svg.voluntariado.repositories.OngRepository;
import com.svg.voluntariado.repositories.ProjectRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final OngRepository ongRepository;
    private final ProjectMapper projectMapper;

    public ProjectService(ProjectRepository projectRepository, OngRepository ongRepository, ProjectMapper projectMapper) {
        this.projectRepository = projectRepository;
        this.ongRepository = ongRepository;
        this.projectMapper = projectMapper;
    }

    @Transactional
    public Long create(CreateProjectRequest createProjectRequest, Long idAdmin) {
        var ong = ongRepository.findById(createProjectRequest.idOng())
                .orElseThrow(OngNotFoundException::new);

        if (!ong.getUsuarioResponsavel().getId().equals(idAdmin)) {
            throw new AccessDeniedException("Apenas o admin da ong pode editar.");
        }

        var projeto = projectMapper.toProjetoEntity(createProjectRequest);
        projeto.setOng(ong);
        projectRepository.save(projeto);
        return projeto.getId();
    }

    @Transactional(readOnly = true)
    public List<SimpleInfoProjectResponse> getAll(int page, int itens) {
        var projetos = projectRepository.findAll(PageRequest.of(page, itens));
        if (projetos.isEmpty()) {
            throw new ProjectNotFoundException("Nenhum projeto foi criada até o momento.");
        }
        return projectMapper.toSimpleInfoProjetoResponse(projetos);
    }

    // TODO método para buscar projeto e as atividade


    @Transactional
    public UpdateProjectResponse update(Long idProjeto, Long idAdmin, UpdateProjectRequest updateProjectRequest) {
        var projeto = projectRepository.findById(idProjeto).orElseThrow(ProjectNotFoundException::new);
        var ong = ongRepository.findById(projeto.getOng().getId())
                .orElseThrow(OngNotFoundException::new);

        if (!ong.getUsuarioResponsavel().getId().equals(idAdmin)) {
            throw new AccessDeniedException("Apenas o admin da ong pode editar.");
        }

        var projetoMap = projectMapper.toProjetoEntity(updateProjectRequest, projeto);
        projetoMap.setDataAtualizacao(OffsetDateTime.now());
        projectRepository.save(projetoMap);
        return projectMapper.toUpdateProjetoResponse(projetoMap);
    }

    @Transactional
    public void delete(Long idProjeto, Long idAdmin, Jwt principal) {
        var projeto = projectRepository.findById(idProjeto).orElseThrow(ProjectNotFoundException::new);
        boolean isAdminPlataforma = principal.getClaimAsStringList("roles").contains("ADMIN_PLATAFORMA");

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

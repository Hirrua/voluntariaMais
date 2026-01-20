package com.svg.voluntariado.services;

import com.svg.voluntariado.domain.dto.PageResponse;
import com.svg.voluntariado.domain.dto.activity.SimpleInfoActivityResponse;
import com.svg.voluntariado.domain.dto.ong.OngContextResponse;
import com.svg.voluntariado.domain.dto.project.*;
import com.svg.voluntariado.domain.entities.AtividadeEntity;
import com.svg.voluntariado.domain.entities.InscricaoEntity;
import com.svg.voluntariado.domain.entities.OngEntity;
import com.svg.voluntariado.domain.entities.ProjetoEntity;
import com.svg.voluntariado.domain.enums.StatusAprovacaoOngEnum;
import com.svg.voluntariado.mapper.OngMapper;
import com.svg.voluntariado.mapper.ProjectMapper;
import com.svg.voluntariado.exceptions.OngNotFoundException;
import com.svg.voluntariado.exceptions.ProjectNotFoundException;
import com.svg.voluntariado.repositories.OngRepository;
import com.svg.voluntariado.repositories.ProjectRepository;
import com.svg.voluntariado.repositories.SubscriptionRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final OngRepository ongRepository;
    private final ProjectMapper projectMapper;
    private final OngMapper ongMapper;
    private final SubscriptionRepository subscriptionRepository;
    private final StorageService storageService;

    public ProjectService(ProjectRepository projectRepository, OngRepository ongRepository, ProjectMapper projectMapper,
                          OngMapper ongMapper, SubscriptionRepository subscriptionRepository, StorageService storageService) {
        this.projectRepository = projectRepository;
        this.ongRepository = ongRepository;
        this.projectMapper = projectMapper;
        this.ongMapper = ongMapper;
        this.subscriptionRepository = subscriptionRepository;
        this.storageService = storageService;
    }

    @Transactional
    public Long create(CreateProjectRequest createProjectRequest, Long idAdmin) {
        var ong = ongRepository.findByUsuarioResponsavelId(idAdmin)
                .orElseThrow(OngNotFoundException::new);

        if (!StatusAprovacaoOngEnum.APROVADA.equals(ong.getStatus())) {
            throw new AccessDeniedException("A ONG precisa estar aprovada para criar projetos.");
        }

        var projeto = projectMapper.toProjetoEntity(createProjectRequest);
        projeto.setOng(ong);
        projectRepository.save(projeto);
        return projeto.getId();
    }

    @Transactional(readOnly = true)
    public List<ProjectAdminResponse> listForAdmin(Long adminId) {
        var projetos = projectRepository.findByOngUsuarioResponsavelId(adminId, PageRequest.of(0, 100)).getContent();
        return projetos.stream()
                .map(this::toAdminResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProjectAdminResponse getForAdmin(Long projectId, Long adminId) {
        var projeto = projectRepository.findById(projectId).orElseThrow(ProjectNotFoundException::new);
        var ong = projeto.getOng();
        if (ong == null || !ong.getUsuarioResponsavel().getId().equals(adminId)) {
            throw new AccessDeniedException("Acesso negado.");
        }
        return toAdminResponse(projeto);
    }

    @Transactional(readOnly = true)
    public PageResponse<SimpleInfoProjectResponse> getAll(int page, int itens) {
        var projetos = projectRepository.findAllByOngStatus(StatusAprovacaoOngEnum.APROVADA, PageRequest.of(page, itens));

        if (projetos.isEmpty()) {
            return PageResponse.empty(page, itens);
        }

        var projetosDto = projetos.getContent().stream()
                .map(projectMapper::toSimpleInfoProjetoResponse)
                .map(this::withPublicUrl)
                .toList();
        return PageResponse.from(projetos, projetosDto);
    }

    @Transactional(readOnly = true)
    public OngProjectAndActivityInfo getOngProjectAndActivityInfo(Long idProject, Long idUser) {
        ProjetoEntity projectInfo = projectRepository.findById(idProject).orElseThrow(ProjectNotFoundException::new);
        OngEntity ongInfo = projectInfo.getOng();
        List<AtividadeEntity> activitiesOrdered = projectInfo.getAtividades().stream()
                .sorted(Comparator.comparing(AtividadeEntity::getDataHoraInicioAtividade))
                .toList();

        SimpleInfoProjectResponse projectMap = withPublicUrl(projectMapper.toSimpleInfoProjetoResponse(projectInfo));
        OngContextResponse ongMap = ongMapper.toOngContextoResponse(ongInfo);
        Map<Long, InscricaoEntity> subscriptionsByActivity = (idUser != null && !activitiesOrdered.isEmpty())
                ? subscriptionRepository.findByUsuarioIdAndAtividadeIdIn(
                        idUser,
                        activitiesOrdered.stream().map(AtividadeEntity::getId).toList()
                )
                .stream()
                .collect(Collectors.toMap(
                        sub -> sub.getAtividade().getId(),
                        Function.identity(),
                        (existing, ignored) -> existing
                ))
                : Collections.emptyMap();

        List<SimpleInfoActivityResponse> activitiesMap = activitiesOrdered.stream()
                .map(activity -> {
                    InscricaoEntity subscription = subscriptionsByActivity.get(activity.getId());
                    return new SimpleInfoActivityResponse(
                            activity.getId(),
                            activity.getNomeAtividade(),
                            activity.getDescricaoAtividade(),
                            activity.getDataHoraInicioAtividade(),
                            activity.getDataHoraFimAtividade(),
                            activity.getLocalAtividade(),
                            activity.getVagasTotais(),
                            activity.getVagasPreenchidasAtividade(),
                            activity.getDataCriacao(),
                            subscription != null ? subscription.getId() : null,
                            subscription != null ? subscription.getStatus() : null
                    );
                })
                .toList();

        return new OngProjectAndActivityInfo(
                projectMap,
                ongMap,
                activitiesMap
        );
    }

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
        return withPublicUrl(projectMapper.toUpdateProjetoResponse(projetoMap));
    }

    @Transactional
    public ProjectAdminResponse updateForAdmin(Long projectId, Long adminId, UpdateProjectRequest updateProjectRequest) {
        var projeto = projectRepository.findById(projectId).orElseThrow(ProjectNotFoundException::new);
        var ong = projeto.getOng();
        if (ong == null || !ong.getUsuarioResponsavel().getId().equals(adminId)) {
            throw new AccessDeniedException("Acesso negado.");
        }
        var projetoMap = projectMapper.toProjetoEntity(updateProjectRequest, projeto);
        projetoMap.setDataAtualizacao(OffsetDateTime.now());
        projectRepository.save(projetoMap);
        return toAdminResponse(projetoMap);
    }

    @Transactional
    public void delete(Long idProjeto, Long idAdmin) {
        var projeto = projectRepository.findById(idProjeto).orElseThrow(ProjectNotFoundException::new);

        var ong = ongRepository.findById(projeto.getOng().getId())
                .orElseThrow(OngNotFoundException::new);

        if (!ong.getUsuarioResponsavel().getId().equals(idAdmin)) {
            throw new AccessDeniedException("Apenas o admin da ong pode excluir.");
        }

        projectRepository.delete(projeto);
    }

    @Transactional
    public void deleteForAdmin(Long projectId, Long adminId) {
        var projeto = projectRepository.findById(projectId).orElseThrow(ProjectNotFoundException::new);
        var ong = projeto.getOng();
        if (ong == null || !ong.getUsuarioResponsavel().getId().equals(adminId)) {
            throw new AccessDeniedException("Acesso negado.");
        }
        projectRepository.delete(projeto);
    }

    private SimpleInfoProjectResponse withPublicUrl(SimpleInfoProjectResponse response) {
        return new SimpleInfoProjectResponse(
                response.id(),
                response.nome(),
                response.objetivo(),
                response.publicoAlvo(),
                storageService.buildPublicUrl(response.urlImagemDestaque())
        );
    }

    private UpdateProjectResponse withPublicUrl(UpdateProjectResponse response) {
        return new UpdateProjectResponse(
                response.nome(),
                response.objetivo(),
                response.descricaoDetalhada(),
                response.publicoAlvo(),
                storageService.buildPublicUrl(response.urlImagemDestaque()),
                response.dataInicioPrevista(),
                response.dataFimPrevista(),
                response.dataAtualizacao()
        );
    }

    private ProjectAdminResponse toAdminResponse(ProjetoEntity entity) {
        return new ProjectAdminResponse(
                entity.getId(),
                entity.getNome(),
                entity.getStatus(),
                entity.getObjetivo(),
                entity.getDescricaoDetalhada(),
                entity.getPublicoAlvo(),
                entity.getDataInicioPrevista(),
                entity.getDataFimPrevista(),
                entity.getEndereco(),
                storageService.buildPublicUrl(entity.getUrlImagemDestaque()),
                entity.getDataCriacao(),
                entity.getDataAtualizacao()
        );
    }
}

package com.svg.voluntariado.services;

import com.svg.voluntariado.domain.dto.activity.*;
import com.svg.voluntariado.domain.dto.ong.OngContextResponse;
import com.svg.voluntariado.domain.dto.project.ProjectContextResponse;
import com.svg.voluntariado.domain.entities.AtividadeEntity;
import com.svg.voluntariado.mapper.ActivityMapper;
import com.svg.voluntariado.mapper.OngMapper;
import com.svg.voluntariado.mapper.ProjectMapper;
import com.svg.voluntariado.exceptions.ActivityNotFoundException;
import com.svg.voluntariado.exceptions.OngNotFoundException;
import com.svg.voluntariado.exceptions.ProjectNotFoundException;
import com.svg.voluntariado.repositories.ActivityRepository;
import com.svg.voluntariado.repositories.OngRepository;
import com.svg.voluntariado.repositories.ProjectRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.time.OffsetDateTime;
import java.util.List;

@Service
public class ActivityService {

    private final ActivityRepository activityRepository;
    private final ProjectRepository projectRepository;
    private final ActivityMapper activityMapper;
    private final OngRepository ongRepository;
    private final ProjectMapper projectMapper;
    private final OngMapper ongMapper;

    public ActivityService(ActivityRepository activityRepository, ProjectRepository projectRepository,
                           ActivityMapper activityMapper, OngRepository ongRepository,
                           ProjectMapper projectMapper, OngMapper ongMapper) {
        this.activityRepository = activityRepository;
        this.projectRepository = projectRepository;
        this.activityMapper = activityMapper;
        this.ongRepository = ongRepository;
        this.projectMapper = projectMapper;
        this.ongMapper = ongMapper;
    }

    @Transactional
    public Long create(CreateActivityRequest createActivityRequest, Long idAdmin) throws AccessDeniedException {
        var project = projectRepository.findById(createActivityRequest.idProjeto());
        if (project.isEmpty()) {
            throw  new ProjectNotFoundException();
        }

        var ong = ongRepository.findById(project.get().getOng().getId()).orElseThrow(OngNotFoundException::new);
        if (!ong.getUsuarioResponsavel().getId().equals(idAdmin)) {
            throw new AccessDeniedException("Somente o admin da ong pode criar atividades.");
        }

        // TODO validar as datas

        var activity = activityMapper.toAtividadeEntity(createActivityRequest);
        activity.setProjeto(project.get());
        activityRepository.save(activity);
        return activity.getId();
    }

    @Transactional(readOnly = true)
    public List<SimpleInfoActivityResponse> getAllActivities(int page, int itens) throws ActivityNotFoundException {
        var atividades = activityRepository.findAll(PageRequest.of(page, itens));
        if (atividades.isEmpty()) {
            throw new ActivityNotFoundException("Nenhuma atividade foi criada.");
        }
        return activityMapper.toSimpleInfoAtividadeResponse(atividades);
    }

    @Transactional
    public UpdateActivityResponse update(Long idAtividade, Long idAdmin, UpdateActivityRequest atividadeRequest)
            throws ActivityNotFoundException, AccessDeniedException {
        var activity = activityRepository.findById(idAtividade).orElseThrow(ActivityNotFoundException::new);
        var project = activity.getProjeto();

        var ong = project.getOng();
        if (!ong.getUsuarioResponsavel().getId().equals(idAdmin)) {
            throw new AccessDeniedException("Somente o admin da ong pode editar atividades.");
        }

        var updateEntity = activityMapper.toAtividadeEntity(atividadeRequest, activity);
        updateEntity.setUltimaAtualizacao(OffsetDateTime.now());
        activityRepository.save(updateEntity);
        return activityMapper.toUpdateAtividadeResponse(updateEntity);
    }

    @Transactional
    public void delete(Long idAtividade, Long idAdmin) throws ActivityNotFoundException, AccessDeniedException {
        var activity = activityRepository.findById(idAtividade).orElseThrow(ActivityNotFoundException::new);
        var project = activity.getProjeto();

        var ong = project.getOng();
        if (!ong.getUsuarioResponsavel().getId().equals(idAdmin)) {
            throw new AccessDeniedException("Somente o admin da ong pode deletar atividades.");
        }

        activityRepository.delete(activity);
    }

    @Transactional(readOnly = true)
    public InfoActivityResponse get(Long id) throws ActivityNotFoundException {
        var activity = activityRepository.findById(id).orElseThrow(ActivityNotFoundException::new);
        var project = activity.getProjeto();
        var ong = project.getOng();

        var ongContexto = ongMapper.toOngContextoResponse(ong);
        var projectContext = projectMapper.toProjetoContextoResponse(project);

        return toInfoAtividadeResponse(activity, ongContexto, projectContext);
    }

    /*
    * Maneira mais simples e fácil (do que mapstruct) de conseguir controlar o DTO
    * */
    private InfoActivityResponse toInfoAtividadeResponse(AtividadeEntity atividade, OngContextResponse ongContexto, ProjectContextResponse projetoContexto) {
        return new InfoActivityResponse(
                atividade.getId(),
                atividade.getNomeAtividade(),
                atividade.getDescricaoAtividade(),
                atividade.getDataHoraInicioAtividade(),
                atividade.getDataHoraFimAtividade(),
                atividade.getLocalAtividade(),
                atividade.getVagasDisponiveisAtividade(),
                atividade.getVagasPreenchidasAtividade(),
                atividade.getDataCriacao(),
                projetoContexto,
                ongContexto
        );
    }
}

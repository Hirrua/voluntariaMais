package com.svg.voluntariado.services;

import com.svg.voluntariado.domain.dto.atividade.*;
import com.svg.voluntariado.domain.dto.ong.OngContextoResponse;
import com.svg.voluntariado.domain.dto.projeto.ProjetoContextoResponse;
import com.svg.voluntariado.domain.entities.AtividadeEntity;
import com.svg.voluntariado.mapper.ActivityMapper;
import com.svg.voluntariado.mapper.OngMapper;
import com.svg.voluntariado.mapper.ProjetoMapper;
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
    private final ProjetoMapper projetoMapper;
    private final OngMapper ongMapper;

    public ActivityService(ActivityRepository activityRepository, ProjectRepository projectRepository,
                           ActivityMapper activityMapper, OngRepository ongRepository,
                           ProjetoMapper projetoMapper, OngMapper ongMapper) {
        this.activityRepository = activityRepository;
        this.projectRepository = projectRepository;
        this.activityMapper = activityMapper;
        this.ongRepository = ongRepository;
        this.projetoMapper = projetoMapper;
        this.ongMapper = ongMapper;
    }

    @Transactional
    public Long create(CreateAtividadeRequest createAtividadeRequest, Long idAdmin) throws AccessDeniedException {
        var project = projectRepository.findById(createAtividadeRequest.idProjeto());
        if (project.isEmpty()) {
            throw  new ProjectNotFoundException();
        }

        var ong = ongRepository.findById(project.get().getOng().getId()).orElseThrow(OngNotFoundException::new);
        if (!ong.getUsuarioResponsavel().getId().equals(idAdmin)) {
            throw new AccessDeniedException("Somente o admin da ong pode criar atividades.");
        }

        // TODO validar as datas

        var activity = activityMapper.toAtividadeEntity(createAtividadeRequest);
        activity.setProjeto(project.get());
        activityRepository.save(activity);
        return activity.getId();
    }

    @Transactional(readOnly = true)
    public List<SimpleInfoAtividadeResponse> getAllActivities(int page, int itens) throws ActivityNotFoundException {
        var atividades = activityRepository.findAll(PageRequest.of(page, itens));
        if (atividades.isEmpty()) {
            throw new ActivityNotFoundException("Nenhuma atividade foi criada.");
        }
        return activityMapper.toSimpleInfoAtividadeResponse(atividades);
    }

    @Transactional
    public UpdateAtividadeResponse update(Long idAtividade, Long idAdmin, UpdateAtividadeRequest atividadeRequest)
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
    public InfoAtividadeResponse get(Long id) throws ActivityNotFoundException {
        var activity = activityRepository.findById(id).orElseThrow(ActivityNotFoundException::new);
        var project = activity.getProjeto();
        var ong = project.getOng();

        var ongContexto = ongMapper.toOngContextoResponse(ong);
        var projectContext = projetoMapper.toProjetoContextoResponse(project);

        return toInfoAtividadeResponse(activity, ongContexto, projectContext);
    }

    /*
    * Maneira mais simples e fácil (do que mapstruct) de conseguir controlar o DTO
    * */
    private InfoAtividadeResponse toInfoAtividadeResponse(AtividadeEntity atividade, OngContextoResponse ongContexto, ProjetoContextoResponse projetoContexto) {
        return new InfoAtividadeResponse(
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

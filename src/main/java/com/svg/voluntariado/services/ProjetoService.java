package com.svg.voluntariado.services;

import com.svg.voluntariado.domain.dto.projeto.CreateProjetoRequest;
import com.svg.voluntariado.domain.dto.projeto.SimpleInfoProjetoResponse;
import com.svg.voluntariado.domain.dto.projeto.UpdateProjetoRequest;
import com.svg.voluntariado.domain.dto.projeto.UpdateProjetoResponse;
import com.svg.voluntariado.domain.mapper.ProjetoMapper;
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
public class ProjetoService {

    private final ProjectRepository projectRepository;
    private final OngRepository ongRepository;
    private final ProjetoMapper projetoMapper;

    public ProjetoService(ProjectRepository projectRepository, OngRepository ongRepository, ProjetoMapper projetoMapper) {
        this.projectRepository = projectRepository;
        this.ongRepository = ongRepository;
        this.projetoMapper = projetoMapper;
    }

    @Transactional
    public Long create(CreateProjetoRequest createProjetoRequest, Long idAdmin) {
        var ong = ongRepository.findById(createProjetoRequest.idOng())
                .orElseThrow(OngNotFoundException::new);

        if (!ong.getUsuarioResponsavel().getId().equals(idAdmin)) {
            throw new AccessDeniedException("Apenas o admin da ong pode editar.");
        }

        var projeto = projetoMapper.toProjetoEntity(createProjetoRequest);
        projeto.setOng(ong);
        projectRepository.save(projeto);
        return projeto.getId();
    }

    @Transactional(readOnly = true)
    public List<SimpleInfoProjetoResponse> getAll(int page, int itens) {
        var projetos = projectRepository.findAll(PageRequest.of(page, itens));
        if (projetos.isEmpty()) {
            throw new ProjectNotFoundException("Nenhum projeto foi criada até o momento.");
        }
        return projetoMapper.toSimpleInfoProjetoResponse(projetos);
    }

    // TODO método para buscar projeto e as atividade


    @Transactional
    public UpdateProjetoResponse update(Long idProjeto, Long idAdmin, UpdateProjetoRequest updateProjetoRequest) {
        var projeto = projectRepository.findById(idProjeto).orElseThrow(ProjectNotFoundException::new);
        var ong = ongRepository.findById(projeto.getOng().getId())
                .orElseThrow(OngNotFoundException::new);

        if (!ong.getUsuarioResponsavel().getId().equals(idAdmin)) {
            throw new AccessDeniedException("Apenas o admin da ong pode editar.");
        }

        var projetoMap = projetoMapper.toProjetoEntity(updateProjetoRequest, projeto);
        projetoMap.setDataAtualizacao(OffsetDateTime.now());
        projectRepository.save(projetoMap);
        return projetoMapper.toUpdateProjetoResponse(projetoMap);
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

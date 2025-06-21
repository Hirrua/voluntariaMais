package com.svg.voluntariado.services;

import com.svg.voluntariado.domain.dto.projeto.CreateProjetoRequest;
import com.svg.voluntariado.domain.dto.projeto.SimpleInfoProjetoResponse;
import com.svg.voluntariado.domain.dto.projeto.UpdateProjetoRequest;
import com.svg.voluntariado.domain.dto.projeto.UpdateProjetoResponse;
import com.svg.voluntariado.domain.mapper.ProjetoMapper;
import com.svg.voluntariado.exceptions.OngNotFoundException;
import com.svg.voluntariado.exceptions.ProjetoNotFoundException;
import com.svg.voluntariado.repositories.OngRepository;
import com.svg.voluntariado.repositories.ProjetoRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

@Service
public class ProjetoService {

    private final ProjetoRepository projetoRepository;
    private final OngRepository ongRepository;
    private final ProjetoMapper projetoMapper;

    public ProjetoService(ProjetoRepository projetoRepository, OngRepository ongRepository, ProjetoMapper projetoMapper) {
        this.projetoRepository = projetoRepository;
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
        projetoRepository.save(projeto);
        return projeto.getId();
    }

    @Transactional(readOnly = true)
    public List<SimpleInfoProjetoResponse> getAll(int page, int itens) {
        var projetos = projetoRepository.findAll(PageRequest.of(page, itens));
        if (projetos.isEmpty()) {
            throw new ProjetoNotFoundException("Nenhum projeto foi criada até o momento.");
        }
        return projetoMapper.toSimpleInfoProjetoResponse(projetos);
    }

    // TODO método para buscar projeto e as atividade


    @Transactional
    public UpdateProjetoResponse update(Long idProjeto, Long idAdmin, UpdateProjetoRequest updateProjetoRequest) {
        var projeto = projetoRepository.findById(idProjeto).orElseThrow(ProjetoNotFoundException::new);
        var ong = ongRepository.findById(projeto.getOng().getId())
                .orElseThrow(OngNotFoundException::new);

        if (!ong.getUsuarioResponsavel().getId().equals(idAdmin)) {
            throw new AccessDeniedException("Apenas o admin da ong pode editar.");
        }

        var projetoMap = projetoMapper.toProjetoEntity(updateProjetoRequest, projeto);
        projetoMap.setDataAtualizacao(OffsetDateTime.now());
        return projetoMapper.toUpdateProjetoResponse(projetoMap);
    }

    @Transactional
    public void delete(Long idProjeto, Long idAdmin, Jwt principal) {
        var projeto = projetoRepository.findById(idProjeto).orElseThrow(ProjetoNotFoundException::new);
        boolean isAdminPlataforma = principal.getClaimAsStringList("roles").contains("ADMIN_PLATAFORMA");

        if (!isAdminPlataforma) {
            var ong = ongRepository.findById(projeto.getOng().getId())
                    .orElseThrow(OngNotFoundException::new);

            if (!ong.getUsuarioResponsavel().getId().equals(idAdmin)) {
                throw new AccessDeniedException("Apenas o admin da ong pode excluir.");
            }
        }

        projetoRepository.delete(projeto);
    }
}

package com.svg.voluntariado.services;

import com.svg.voluntariado.domain.dto.atividade.*;
import com.svg.voluntariado.domain.dto.ong.OngContextoResponse;
import com.svg.voluntariado.domain.dto.projeto.ProjetoContextoResponse;
import com.svg.voluntariado.domain.entities.AtividadeEntity;
import com.svg.voluntariado.domain.mapper.AtividadeMapper;
import com.svg.voluntariado.domain.mapper.OngMapper;
import com.svg.voluntariado.domain.mapper.ProjetoMapper;
import com.svg.voluntariado.exceptions.AtividadeNotFoundException;
import com.svg.voluntariado.exceptions.OngNotFoundException;
import com.svg.voluntariado.exceptions.ProjetoNotFoundException;
import com.svg.voluntariado.repositories.AtividadeRepository;
import com.svg.voluntariado.repositories.OngRepository;
import com.svg.voluntariado.repositories.ProjetoRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class AtividadeService {

    private final AtividadeRepository atividadeRepository;
    private final ProjetoRepository projetoRepository;
    private final AtividadeMapper atividadeMapper;
    private final OngRepository ongRepository;
    private final ProjetoMapper projetoMapper;
    private final OngMapper ongMapper;

    public AtividadeService(AtividadeRepository atividadeRepository, ProjetoRepository projetoRepository,
                            AtividadeMapper atividadeMapper, OngRepository ongRepository,
                            ProjetoMapper projetoMapper, OngMapper ongMapper) {
        this.atividadeRepository = atividadeRepository;
        this.projetoRepository = projetoRepository;
        this.atividadeMapper = atividadeMapper;
        this.ongRepository = ongRepository;
        this.projetoMapper = projetoMapper;
        this.ongMapper = ongMapper;
    }

    @Transactional
    public Long create(CreateAtividadeRequest createAtividadeRequest, Long idAdmin) throws AccessDeniedException {
        var projeto = projetoRepository.findById(createAtividadeRequest.idProjeto());
        if (projeto.isEmpty()) {
            throw  new ProjetoNotFoundException();
        }

        var ong = ongRepository.findById(projeto.get().getOng().getId()).orElseThrow(OngNotFoundException::new);
        if (!ong.getUsuarioResponsavel().getId().equals(idAdmin)) {
            throw new AccessDeniedException("Somente o admin da ong pode criar atividades.");
        }

        var atividade = atividadeMapper.toAtividadeEntity(createAtividadeRequest);
        atividade.setProjeto(projeto.get());
        atividadeRepository.save(atividade);
        return atividade.getId();
    }

    @Transactional(readOnly = true)
    public List<SimpleInfoAtividadeResponse> getAllActivities(int page, int itens) throws AtividadeNotFoundException {
        var atividades = atividadeRepository.findAll(PageRequest.of(page, itens));
        if (atividades.isEmpty()) {
            throw new AtividadeNotFoundException("Nenhuma atividade foi criada.");
        }
        return atividadeMapper.toSimpleInfoAtividadeResponse(atividades);
    }

    @Transactional
    public UpdateAtividadeResponse update(Long idAtividade, Long idAdmin, UpdateAtividadeRequest atividadeRequest)
            throws AtividadeNotFoundException, AccessDeniedException {
        var atividade = atividadeRepository.findById(idAtividade).orElseThrow(AtividadeNotFoundException::new);
        var projeto = atividade.getProjeto();

        var ong = projeto.getOng();
        if (!ong.getUsuarioResponsavel().getId().equals(idAdmin)) {
            throw new AccessDeniedException("Somente o admin da ong pode criar atividades.");
        }

        var updateEntity = atividadeMapper.toAtividadeEntity(atividadeRequest, atividade);
        updateEntity.setUltimaAtualizacao(OffsetDateTime.now());
        atividadeRepository.save(updateEntity);
        return atividadeMapper.toUpdateAtividadeResponse(updateEntity);
    }

    @Transactional(readOnly = true)
    public InfoAtividadeResponse get(Long id) {
        var atividade = atividadeRepository.findById(id).orElseThrow(NoSuchElementException::new);
        var projeto = atividade.getProjeto();
        var ong = projeto.getOng();

        var ongContexto = ongMapper.toOngContextoResponse(ong);
        var projetoContexto = projetoMapper.toProjetoContextoResponse(projeto);

        return toInfoAtividadeResponse(atividade, ongContexto, projetoContexto);
    }

    /*
    * Maneira mais simples e fácil (do que mapstruct) de conseguir controlar o DTO
    * */
    private InfoAtividadeResponse toInfoAtividadeResponse(AtividadeEntity atividade, OngContextoResponse ongContexto, ProjetoContextoResponse projetoContexto) {
        var vagasRestantes = atividade.getVagasDisponiveisAtividade() - atividade.getVagasPreenchidasAtividade();
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

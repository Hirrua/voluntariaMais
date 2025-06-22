package com.svg.voluntariado.services;

import com.svg.voluntariado.domain.dto.atividade.CreateAtividadeRequest;
import com.svg.voluntariado.domain.mapper.AtividadeMapper;
import com.svg.voluntariado.exceptions.OngNotFoundException;
import com.svg.voluntariado.exceptions.ProjetoNotFoundException;
import com.svg.voluntariado.repositories.AtividadeRepository;
import com.svg.voluntariado.repositories.OngRepository;
import com.svg.voluntariado.repositories.ProjetoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;

@Service
public class AtividadeService {

    private final AtividadeRepository atividadeRepository;
    private final ProjetoRepository projetoRepository;
    private final AtividadeMapper atividadeMapper;
    private final OngRepository ongRepository;

    public AtividadeService(AtividadeRepository atividadeRepository, ProjetoRepository projetoRepository, AtividadeMapper atividadeMapper, OngRepository ongRepository) {
        this.atividadeRepository = atividadeRepository;
        this.projetoRepository = projetoRepository;
        this.atividadeMapper = atividadeMapper;
        this.ongRepository = ongRepository;
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

    /*
    * TODO  método para buscar as atividade e info simples sobre ong e projeto
    * todas infos sobre atividade
    * nome, data, objetivo, publico alvo projeto
    * nome da ong, contato, data fundação
    * */
}

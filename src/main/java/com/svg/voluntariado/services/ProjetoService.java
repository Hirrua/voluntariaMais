package com.svg.voluntariado.services;

import com.svg.voluntariado.domain.dto.projeto.CreateProjetoRequest;
import com.svg.voluntariado.domain.mapper.ProjetoMapper;
import com.svg.voluntariado.exceptions.OngNotFoundException;
import com.svg.voluntariado.repositories.OngRepository;
import com.svg.voluntariado.repositories.ProjetoRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
            throw new BadCredentialsException("Apenas o admin da ong pode editar");
        }

        var projeto = projetoMapper.toProjetoEntity(createProjetoRequest);
        projeto.setOng(ong);
        projetoRepository.save(projeto);
        return projeto.getId();
    }
}

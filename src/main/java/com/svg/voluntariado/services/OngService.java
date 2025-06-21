package com.svg.voluntariado.services;

import com.svg.voluntariado.domain.dto.ong.CreateOngRequest;
import com.svg.voluntariado.domain.dto.ong.InfoOngResponse;
import com.svg.voluntariado.domain.dto.ong.ListOngResponse;
import com.svg.voluntariado.domain.dto.ong.UpdateInfoOngRequest;
import com.svg.voluntariado.domain.entities.OngEntity;
import com.svg.voluntariado.domain.entities.UsuarioEntity;
import com.svg.voluntariado.domain.mapper.OngMapper;
import com.svg.voluntariado.exceptions.OngNotFoundException;
import com.svg.voluntariado.repositories.OngRepository;
import com.svg.voluntariado.repositories.UsuarioRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class OngService {

    private final OngRepository ongRepository;
    private final OngMapper ongMapper;
    private final UsuarioRepository usuarioRepository;

    public OngService(OngRepository ongRepository, OngMapper ongMapper, UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
        this.ongRepository = ongRepository;
        this.ongMapper = ongMapper;
    }

    @Transactional
    public Long create(CreateOngRequest createOngRequest) {
        UsuarioEntity usuarioResponsavel = usuarioRepository.findById(createOngRequest.idUsuarioResponsavel())
                .orElseThrow(() -> new RuntimeException("Usuário responsável não encontrado com ID: " + createOngRequest.idUsuarioResponsavel())
        );

        var ongEntity = ongMapper.toOngEntity(createOngRequest);
        ongEntity.setUsuarioResponsavel(usuarioResponsavel);
        var newOng = ongRepository.save(ongEntity);

        return newOng.getId();
    }

    public InfoOngResponse get(Long id) {
        var ong = ongRepository.findById(id)
                .orElseThrow(OngNotFoundException::new);

        return ongMapper.toInfoOngResponse(ong);
    }

    // TODO método para buscar ong e os projetos vinculados a ela

    public List<ListOngResponse> findAllOng(int page, int itens) {
        Page<OngEntity> ongEntities = ongRepository.findAll(PageRequest.of(page, itens));
        if (ongEntities.isEmpty()) {
            throw new OngNotFoundException("Nenhuma ong foi criada até o momento");
        }
        return ongMapper.toListOngResponse(ongEntities);
    }

    public InfoOngResponse update(Long idOng, Long idAdmin, UpdateInfoOngRequest updateInfoOngRequest) {
        var ong = ongRepository.findById(idOng)
                .orElseThrow(OngNotFoundException::new);

        if (!ong.getUsuarioResponsavel().getId().equals(idAdmin)) {
            throw new BadCredentialsException("Somente o admin da ong pode realizar alterações");
        }

        var ongEntity = ongMapper.toOngEntity(updateInfoOngRequest, ong);
        return ongMapper.toInfoOngResponse(ongEntity);
    }

    public void delete(Long idOng, Long idAdmin) {
        var ong = ongRepository.findById(idOng)
                .orElseThrow(OngNotFoundException::new);

        if (!ong.getUsuarioResponsavel().getId().equals(idAdmin)) {
            throw new BadCredentialsException("Somente o admin da ong pode realizar alterações");
        }

        ongRepository.delete(ong);
    }
}

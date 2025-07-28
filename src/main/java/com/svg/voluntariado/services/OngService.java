package com.svg.voluntariado.services;

import com.svg.voluntariado.domain.dto.ong.*;
import com.svg.voluntariado.domain.entities.OngEntity;
import com.svg.voluntariado.domain.entities.UsuarioEntity;
import com.svg.voluntariado.exceptions.ProjectNotFoundException;
import com.svg.voluntariado.mapper.OngMapper;
import com.svg.voluntariado.exceptions.OngNotFoundException;
import com.svg.voluntariado.repositories.OngRepository;
import com.svg.voluntariado.repositories.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OngService {

    private final OngRepository ongRepository;
    private final OngMapper ongMapper;
    private final UserRepository userRepository;

    public OngService(OngRepository ongRepository, OngMapper ongMapper, UserRepository userRepository) {
        this.userRepository = userRepository;
        this.ongRepository = ongRepository;
        this.ongMapper = ongMapper;
    }

    @Transactional
    public Long create(CreateOngRequest createOngRequest) {
        UsuarioEntity responsibleUser = userRepository.findById(createOngRequest.idUsuarioResponsavel())
                .orElseThrow(() -> new RuntimeException("Usuário responsável não encontrado com ID: " + createOngRequest.idUsuarioResponsavel())
        );

        var ongEntity = ongMapper.toOngEntity(createOngRequest);
        ongEntity.setUsuarioResponsavel(responsibleUser);
        var newOng = ongRepository.save(ongEntity);

        return newOng.getId();
    }

    public InfoOngResponse get(Long id) {
        var ong = ongRepository.findById(id)
                .orElseThrow(OngNotFoundException::new);

        return ongMapper.toInfoOngResponse(ong);
    }

    public InfoOngAndProjectResponse findOngAndProjects(Long idOng) {
        var ongEntity = ongRepository.findById(idOng);
        if (ongEntity.isEmpty()) {
            throw new OngNotFoundException();
        }

        var projectEntity = ongEntity.get().getProjetos();
        if (projectEntity.isEmpty()) {
            throw new ProjectNotFoundException("Essa ong não possui nenhum projeto cadastrado.");
        }

        return ongMapper.toInfoOngAndProjectResponse(ongEntity.get());
    }

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

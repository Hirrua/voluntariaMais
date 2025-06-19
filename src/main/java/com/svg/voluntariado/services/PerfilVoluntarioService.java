package com.svg.voluntariado.services;

import com.svg.voluntariado.domain.dto.CreateProfileRequest;
import com.svg.voluntariado.domain.dto.InfoPerfilResponse;
import com.svg.voluntariado.domain.dto.UpdateInfoProfileRequest;
import com.svg.voluntariado.domain.entities.PerfilVoluntarioEntity;
import com.svg.voluntariado.domain.entities.UsuarioEntity;
import com.svg.voluntariado.domain.mapper.ProfileMapper;
import com.svg.voluntariado.repositories.PerfilVoluntarioRepository;
import com.svg.voluntariado.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
public class PerfilVoluntarioService {

    private final UsuarioRepository usuarioRepository;
    private final PerfilVoluntarioRepository perfilVoluntarioRepository;
    private final ProfileMapper profileMapper;

    @Autowired
    public PerfilVoluntarioService(PerfilVoluntarioRepository perfilVoluntarioRepository, UsuarioRepository usuarioRepository, ProfileMapper profileMapper) {
        this.usuarioRepository = usuarioRepository;
        this.perfilVoluntarioRepository = perfilVoluntarioRepository;
        this.profileMapper = profileMapper;
    }

    @Transactional
    public Long create(CreateProfileRequest createProfileRequest) {

        UsuarioEntity usuario = usuarioRepository.findById(createProfileRequest.id_usuario())
                .orElseThrow(() -> new NoSuchElementException("Usuário não encontrado.")
        );

        if (usuario.getPerfilVoluntario() != null) {
            throw new IllegalStateException("Este usuário já possui um perfil de voluntário.");
        }

        var novoPerfil = profileMapper.toPerfilVoluntarioEntity(createProfileRequest);
        novoPerfil.setUsuario(usuario);

        usuario.setPerfilVoluntario(novoPerfil);
        usuarioRepository.save(usuario);

        return novoPerfil.getId();
    }

    @Transactional(readOnly = true)
    public InfoPerfilResponse read(Long id) {

        var perfil = perfilVoluntarioRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Perfil não encontrado.")
        );


        return profileMapper.toInfoPerfilResponse(perfil);
    }

    @Transactional
    public InfoPerfilResponse update(Long id, UpdateInfoProfileRequest updateInfo) {

        var perfil = perfilVoluntarioRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Perfil não encontrado.")
        );

        var infosMap = profileMapper.toPerfilVoluntarioEntity(updateInfo, perfil);
        return profileMapper.toInfoPerfilResponse(infosMap);
    }

    @Transactional
    public void delete(Long id) {

        var perfil = perfilVoluntarioRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Perfil não encontrado.")
        );

        perfilVoluntarioRepository.delete(perfil);
    }
}

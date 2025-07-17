package com.svg.voluntariado.services;

import com.svg.voluntariado.domain.dto.CreateProfileRequest;
import com.svg.voluntariado.domain.dto.InfoPerfilResponse;
import com.svg.voluntariado.domain.dto.UpdateInfoProfileRequest;
import com.svg.voluntariado.domain.entities.UsuarioEntity;
import com.svg.voluntariado.mapper.ProfileMapper;
import com.svg.voluntariado.exceptions.ProfileNotFoundException;
import com.svg.voluntariado.exceptions.UserNotFoundException;
import com.svg.voluntariado.repositories.VolunteerProfileRepository;
import com.svg.voluntariado.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PerfilVoluntarioService {

    private final UserRepository userRepository;
    private final VolunteerProfileRepository volunteerProfileRepository;
    private final ProfileMapper profileMapper;

    @Autowired
    public PerfilVoluntarioService(VolunteerProfileRepository volunteerProfileRepository, UserRepository userRepository, ProfileMapper profileMapper) {
        this.userRepository = userRepository;
        this.volunteerProfileRepository = volunteerProfileRepository;
        this.profileMapper = profileMapper;
    }

    @Transactional
    public Long create(CreateProfileRequest createProfileRequest) {

        UsuarioEntity usuario = userRepository.findById(createProfileRequest.id_usuario())
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado.")
        );

        if (usuario.getPerfilVoluntario() != null) {
            throw new IllegalStateException("Este usuário já possui um perfil de voluntário.");
        }

        var novoPerfil = profileMapper.toPerfilVoluntarioEntity(createProfileRequest);
        novoPerfil.setUsuario(usuario);

        usuario.setPerfilVoluntario(novoPerfil);
        userRepository.save(usuario);

        return novoPerfil.getId();
    }

    @Transactional(readOnly = true)
    public InfoPerfilResponse get(Long id) {

        var perfil = volunteerProfileRepository.findById(id)
                .orElseThrow(() -> new ProfileNotFoundException("Perfil não encontrado.")
        );


        return profileMapper.toInfoPerfilResponse(perfil);
    }

    @Transactional
    public InfoPerfilResponse update(Long id, UpdateInfoProfileRequest updateInfo) {

        var perfil = volunteerProfileRepository.findById(id)
                .orElseThrow(() -> new ProfileNotFoundException("Perfil não encontrado.")
        );

        var infosMap = profileMapper.toPerfilVoluntarioEntity(updateInfo, perfil);
        return profileMapper.toInfoPerfilResponse(infosMap);
    }

    @Transactional
    public void delete(Long id) {

        var perfil = volunteerProfileRepository.findById(id)
                .orElseThrow(() -> new ProfileNotFoundException("Perfil não encontrado.")
        );

        volunteerProfileRepository.delete(perfil);
    }
}

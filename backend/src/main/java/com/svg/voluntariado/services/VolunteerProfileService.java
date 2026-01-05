package com.svg.voluntariado.services;

import com.svg.voluntariado.domain.dto.profile.CreateProfileRequest;
import com.svg.voluntariado.domain.dto.profile.InfoProfileResponse;
import com.svg.voluntariado.domain.dto.profile.UpdateInfoProfileRequest;
import com.svg.voluntariado.domain.entities.PerfilVoluntarioEntity;
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
public class VolunteerProfileService {

    private final UserRepository userRepository;
    private final VolunteerProfileRepository volunteerProfileRepository;
    private final ProfileMapper profileMapper;
    private final StorageService storageService;

    @Autowired
    public VolunteerProfileService(VolunteerProfileRepository volunteerProfileRepository, UserRepository userRepository, ProfileMapper profileMapper, StorageService storageService) {
        this.userRepository = userRepository;
        this.volunteerProfileRepository = volunteerProfileRepository;
        this.profileMapper = profileMapper;
        this.storageService = storageService;
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
    public InfoProfileResponse get(Long id) {

        var perfil = volunteerProfileRepository.findById(id)
                .orElseThrow(() -> new ProfileNotFoundException("Perfil não encontrado.")
        );

        return toInfoProfileResponse(perfil);
    }

    @Transactional
    public InfoProfileResponse update(Long id, UpdateInfoProfileRequest updateInfo) {

        var perfil = volunteerProfileRepository.findById(id)
                .orElseThrow(() -> new ProfileNotFoundException("Perfil não encontrado.")
        );

        var infosMap = profileMapper.toPerfilVoluntarioEntity(updateInfo, perfil);
        return toInfoProfileResponse(infosMap);
    }

    @Transactional
    public void delete(Long id) {

        var perfil = volunteerProfileRepository.findById(id)
                .orElseThrow(() -> new ProfileNotFoundException("Perfil não encontrado.")
        );

        volunteerProfileRepository.delete(perfil);
    }

    public InfoProfileResponse toInfoProfileResponse(PerfilVoluntarioEntity entity) {
        String fotoPerfilUrl = storageService.buildPublicUrl(entity.getFotoPerfilUrl());
        return new InfoProfileResponse(
                entity.getId(),
                entity.getUsuario().getNome(),
                entity.getUsuario().getSobrenome(),
                entity.getUsuario().getEmail(),
                entity.getBio(),
                entity.getDisponibilidade(),
                entity.getDataNascimento(),
                entity.getTelefoneContato(),
                fotoPerfilUrl,
                entity.getUsuario().getEndereco()
        );
    }
}

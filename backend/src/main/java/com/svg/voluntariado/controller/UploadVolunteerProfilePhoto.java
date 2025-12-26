package com.svg.voluntariado.controller;

import com.svg.voluntariado.domain.enums.StorageFolder;
import com.svg.voluntariado.exceptions.UserNotFoundException;
import com.svg.voluntariado.repositories.UserRepository;
import com.svg.voluntariado.services.StorageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UploadVolunteerProfilePhoto {

    private final StorageService storageService;
    private final UserRepository userRepository;

    public UploadVolunteerProfilePhoto(StorageService storageService, UserRepository userRepository) {
        this.storageService = storageService;
        this.userRepository = userRepository;
    }

    @PostMapping("/{userId}/photo")
    public ResponseEntity<?> uploadPhoto(@PathVariable("userId") Long id,
                                         @RequestParam("file") MultipartFile file) throws IOException {

        var user = userRepository.findById(id);
        if (user.isEmpty()) {
            throw new UserNotFoundException();
        }

        String fileName = UUID.randomUUID().toString();
        String key = storageService.upload(
                StorageFolder.VOLUNTARIO,
                user.get().getId(),
                fileName,
                file.getBytes(),
                file.getContentType()
        );

        var perfil = user.get().getPerfilVoluntario();
        if (perfil != null) {
            perfil.setFotoPerfilUrl(key);
            userRepository.save(user.get());
        }

        return ResponseEntity.ok().body(key);
    }
}

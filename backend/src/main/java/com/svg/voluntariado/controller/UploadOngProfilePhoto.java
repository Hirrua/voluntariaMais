package com.svg.voluntariado.controller;

import com.svg.voluntariado.domain.enums.StorageFolder;
import com.svg.voluntariado.exceptions.OngNotFoundException;
import com.svg.voluntariado.repositories.OngRepository;
import com.svg.voluntariado.services.StorageService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api/ong")
public class UploadOngProfilePhoto {

    private final StorageService storageService;
    private final OngRepository ongRepository;

    public UploadOngProfilePhoto(StorageService storageService, OngRepository ongRepository) {
        this.storageService = storageService;
        this.ongRepository = ongRepository;
    }

    @PostMapping("/{ongId}/photo")
    @PreAuthorize("hasRole('ADMIN_ONG')")
    public ResponseEntity<?> uploadPhoto(@PathVariable("ongId") Long id,
                                         @RequestParam("file") MultipartFile file,
                                         @AuthenticationPrincipal Jwt principal) throws IOException {

        var ong = ongRepository.findById(id);
        if (ong.isEmpty()) {
            throw new OngNotFoundException();
        }

        Long adminId = Long.parseLong(principal.getSubject());
        if (ong.get().getUsuarioResponsavel() == null
                || !ong.get().getUsuarioResponsavel().getId().equals(adminId)) {
            throw new AccessDeniedException("Acesso negado.");
        }

        String fileName = UUID.randomUUID().toString();
        String key = storageService.upload(
                StorageFolder.ONG,
                ong.get().getId(),
                fileName,
                file.getBytes(),
                file.getContentType()
        );

        ong.get().setLogoUrl(key);
        ongRepository.save(ong.get());

        return ResponseEntity.ok().body(key);
    }
}

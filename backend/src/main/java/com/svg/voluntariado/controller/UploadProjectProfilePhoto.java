package com.svg.voluntariado.controller;

import com.svg.voluntariado.domain.enums.StorageFolder;
import com.svg.voluntariado.exceptions.ProjectNotFoundException;
import com.svg.voluntariado.repositories.ProjectRepository;
import com.svg.voluntariado.services.StorageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api/projetos")
public class UploadProjectProfilePhoto {

    private final StorageService storageService;
    private final ProjectRepository projectRepository;

    public UploadProjectProfilePhoto(StorageService storageService, ProjectRepository projectRepository) {
        this.storageService = storageService;
        this.projectRepository = projectRepository;
    }

    @PostMapping("/{projectId}/photo")
    public ResponseEntity<?> uploadPhoto(@PathVariable("projectId") Long id,
                                         @RequestParam("file") MultipartFile file) throws IOException {

        var project = projectRepository.findById(id);
        if (project.isEmpty()) {
            throw new ProjectNotFoundException();
        }

        String fileName = UUID.randomUUID().toString();
        String key = storageService.upload(
                StorageFolder.PROJETO,
                project.get().getId(),
                fileName,
                file.getBytes(),
                file.getContentType()
        );

        project.get().setUrlImagemDestaque(key);
        projectRepository.save(project.get());

        return ResponseEntity.ok().body(key);
    }
}

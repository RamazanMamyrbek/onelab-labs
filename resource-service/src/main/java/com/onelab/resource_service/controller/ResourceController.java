package com.onelab.resource_service.controller;

import com.onelab.resource_service.entity.Resource;
import com.onelab.resource_service.mapper.ResourceMapper;
import com.onelab.resource_service.service.ResourceService;
import lombok.RequiredArgsConstructor;
import org.onelab.common.dto.response.FileDto;
import org.onelab.common.dto.response.ResourceResponseDto;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/resources")
@RequiredArgsConstructor
public class ResourceController {
    private final ResourceService resourceService;
    private final ResourceMapper resourceMapper;

    @GetMapping("/file/{resourceId}")
    public ResponseEntity<InputStreamResource> getResource(@PathVariable("resourceId") Long resourceId) {
        FileDto fileDto = resourceService.getFileDtoById(resourceId);
        String encodedFileName = URLEncoder.encode(fileDto.name(), StandardCharsets.UTF_8);
        return ResponseEntity
                .status(HttpStatus.OK)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=%s".formatted(encodedFileName))
                .header("File-Name", encodedFileName)
                .contentType(MediaType.valueOf(fileDto.contentType()))
                .body(fileDto.file());
    }


    @GetMapping("/{id}")
    public ResponseEntity<ResourceResponseDto> getResourceDetails(@PathVariable Long id) {
        Resource resource = resourceService.getResourceById(id);
        return ResponseEntity.ok(resourceMapper.mapToResourceResponseDto(resource));
    }

    @PostMapping("/{folder}")
    public ResponseEntity<ResourceResponseDto> uploadFile(@PathVariable("file") MultipartFile file,
                                               @PathVariable("folder") String folder) {
        Resource resource = resourceService.saveResource(file, folder);
        return ResponseEntity.status(HttpStatus.CREATED).body(resourceMapper.mapToResourceResponseDto(resource));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResourceResponseDto> updateFile(@PathVariable Long id,
                                                          @PathVariable("file") MultipartFile file) {
        Resource updatedResource = resourceService.updateResource(id, file);
        return ResponseEntity.ok(resourceMapper.mapToResourceResponseDto(updatedResource));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFile(@PathVariable Long id) {
        resourceService.deleteResource(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/check/image")
    public ResponseEntity<Void> checkImage(@PathVariable("file") MultipartFile file) {
        resourceService.checkImage(file);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/check/file")
    public ResponseEntity<Void> checkFile(@PathVariable("file") MultipartFile file) {
        resourceService.checkFile(file);
        return ResponseEntity.ok().build();
    }
}

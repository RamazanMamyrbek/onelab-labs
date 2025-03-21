package com.onelab.resource_service.service.impl;

import com.onelab.resource_service.entity.Resource;
import com.onelab.resource_service.mapper.ResourceMapper;
import com.onelab.resource_service.repository.ResourceRepository;
import com.onelab.resource_service.service.MinioService;
import com.onelab.resource_service.service.ResourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.onelab.common.dto.response.FileDto;
import org.onelab.common.enums.ResourceType;
import org.onelab.common.exception.FileException;
import org.onelab.common.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ResourceServiceImpl implements ResourceService {
    private final ResourceRepository resourceRepository;
    private final MinioService minioService;
    private final ResourceMapper resourceMapper;
    @Value("${minio.allowedTypes.allowedAchievementTypes}")
    private Set<String> allowedAchievementTypes;
    @Value("${minio.allowedTypes.allowedImageTypes}")
    private Set<String> allowedImageTypes;

    @Override
    public FileDto getFileDtoById(Long resourceId) {
        Resource resource = getResourceById(resourceId);
        InputStream inputStream = minioService.downloadFile(resource.getKey());
        return new FileDto(
                new InputStreamResource(inputStream),
                resource.getName(),
                getMimeType(resource.getContentType())
        );
    }

    @Override
    public Resource getResourceById(Long resourceId) {
        return resourceRepository.findById(resourceId).orElseThrow(
                () -> ResourceNotFoundException.resourceNotFoundById(resourceId)
        );
    }

    @Override
    @Transactional
    public Resource saveResource(MultipartFile file, String folder) {
        Resource resource = minioService.uploadFile(file, folder);
        return resourceRepository.save(resource);
    }

    @Override
    @Transactional
    public Resource updateResource(Long resourceId, MultipartFile file) {
        Resource oldResource = getResourceById(resourceId);
        minioService.deleteFile(oldResource.getKey());
        Resource newResource = minioService.uploadFile(file, oldResource.getFolder());
        resourceMapper.updateResource(oldResource, newResource);
        resourceRepository.save(oldResource);
        return oldResource;
    }

    @Override
    @Transactional
    public void deleteResource(Long resourceId) {
        Resource resource = getResourceById(resourceId);
        minioService.deleteFile(resource.getKey());
        resourceRepository.deleteById(resourceId);
    }

    @Override
    public void checkImage(MultipartFile file) {
        if (file == null) {
            log.error("File is null");
            throw new FileException("File is null");
        }
        if (file.isEmpty()) {
            log.error("File is empty");
            throw new FileException("File is empty");
        }
        if (file.getOriginalFilename() == null) {
            log.error("File has no original filename");
            throw new FileException("File has no original filename");
        }
        if (file.getContentType() == null) {
            log.error("File has no content type");
            throw new FileException("File has no content type");
        }
        if (!allowedImageTypes.contains(file.getContentType())) {
            log.error("Invalid file type: {}", file.getContentType());
            throw new FileException("Invalid file type. Submit only image/png,image/jpeg");
        }
    }

    @Override
    public void checkFile(MultipartFile file) {
        if (file == null) {
            log.error("File is null");
            throw new FileException("File is null");
        }
        if (file.isEmpty()) {
            log.error("File is empty");
            throw new FileException("File is empty");
        }
        if (file.getOriginalFilename() == null) {
            log.error("File has no original filename");
            throw new FileException("File has no original filename");
        }
        if (file.getContentType() == null) {
            log.error("File has no content type");
            throw new FileException("File has no content type");
        }
        if (!allowedAchievementTypes.contains(file.getContentType())) {
            log.error("Invalid file type: {}", file.getContentType());
            throw new FileException("Invalid file type. Submit only image/png,image/jpeg,application/pdf,application/vnd.openxmlformats-officedocument.wordprocessingml.document,application/msword files");
        }
    }

    private String getMimeType(ResourceType contentType) {
            return switch (contentType) {
                case IMAGE -> "image/png";
                case VIDEO -> "video/mp4";
                case AUDIO -> "audio/mpeg";
                case PDF -> "application/pdf";
                case MSWORD -> "application/msword";
                case MSEXCEL -> "application/vnd.ms-excel";
                case ZIP -> "application/zip";
                case TEXT -> "text/plain";
                default -> "application/octet-stream";
            };
    }
}

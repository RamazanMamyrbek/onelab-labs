package com.onelab.resource_service.service;

import com.onelab.resource_service.entity.Resource;
import org.onelab.common.dto.response.FileDto;
import org.springframework.web.multipart.MultipartFile;

public interface ResourceService {
    FileDto getFileDtoById(Long resourceId);

    Resource getResourceById(Long resourceId);

    Resource saveResource(MultipartFile file, String folder);

    Resource updateResource(Long resourceId, MultipartFile file);

    void deleteResource(Long resourceId);

    void checkImage(MultipartFile file);

    void checkFile(MultipartFile file);

}

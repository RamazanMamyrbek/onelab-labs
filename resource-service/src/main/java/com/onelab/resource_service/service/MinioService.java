package com.onelab.resource_service.service;

import com.onelab.resource_service.entity.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

public interface MinioService {
    InputStream downloadFile(String key);

    void deleteFile(String key);

    Resource uploadFile(MultipartFile file, String folder);
}

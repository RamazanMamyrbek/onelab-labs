package com.onelab.resource_service.service.impl;

import com.onelab.resource_service.entity.Resource;
import com.onelab.resource_service.service.MinioService;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.onelab.common.enums.ResourceType;
import org.onelab.common.exception.MinioFileException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.math.BigInteger;

@Service
@RequiredArgsConstructor
@Slf4j
public class MinioServiceImpl implements MinioService {
    private final MinioClient minioClient;
    @Value("${minio.bucketName}")
    private String bucketName;

    @Override
    public Resource uploadFile(MultipartFile file, String folder) {
        String key = String.format("%s/%d%s".formatted(folder, System.currentTimeMillis(), file.getOriginalFilename()));
        try {
            minioClient.putObject(PutObjectArgs
                    .builder()
                            .bucket(bucketName)
                            .object(key)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                    .build());
            Resource resource = new Resource();
            resource.setName(file.getOriginalFilename());
            resource.setSize(BigInteger.valueOf(file.getSize()));
            resource.setKey(key);
            resource.setFolder(folder);
            resource.setContentType(ResourceType.getResourceType(file.getContentType()));
            return resource;
        } catch (Exception e) {
            log.error("Error occurred while uploading file to minio {}", e.getMessage());
            throw new MinioFileException(e.getMessage());
        }
    }

    @Override
    public InputStream downloadFile(String key) {
        try {
            return minioClient.getObject(GetObjectArgs
                    .builder()
                    .bucket(bucketName)
                    .object(key)
                    .build());
        } catch (Exception e) {
            log.error("Error occurred while downloading file from minio {}", e.getMessage());
            throw new MinioFileException(e.getMessage());
        }
    }

    @Override
    public void deleteFile(String key) {
        try {

            minioClient.removeObject(RemoveObjectArgs
                    .builder()
                    .bucket(bucketName)
                    .object(key)
                    .build());
        } catch (Exception e) {
            log.error("Error occurred while deleting file from minio {}", e.getMessage());
            throw new MinioFileException(e.getMessage());
        }
    }
}

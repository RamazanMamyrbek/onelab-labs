package com.onelab.resource_service.service;

import com.onelab.resource_service.entity.Resource;
import com.onelab.resource_service.service.impl.MinioServiceImpl;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.onelab.common.enums.ResourceType;
import org.onelab.common.exception.MinioFileException;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigInteger;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MinioServiceImplTest {

    @Mock
    private MinioClient minioClient;

    @Mock
    private MultipartFile file;

    @InjectMocks
    private MinioServiceImpl minioService;

    @Test
    void uploadFile_ShouldUploadAndReturnResource() throws Exception {
        String bucketName = "test-bucket";
        String folder = "test-folder";
        String filename = "test.txt";
        String key = folder + "/" + System.currentTimeMillis() + filename;
        long fileSize = 1024L;

        ReflectionTestUtils.setField(minioService, "bucketName", bucketName);

        when(file.getOriginalFilename()).thenReturn(filename);
        when(file.getSize()).thenReturn(fileSize);
        when(file.getInputStream()).thenReturn(new ByteArrayInputStream(new byte[0]));
        when(file.getContentType()).thenReturn("text/plain");

        Resource result = minioService.uploadFile(file, folder);

        assertNotNull(result);
        assertEquals(filename, result.getName());
        assertEquals(BigInteger.valueOf(fileSize), result.getSize());
        assertTrue(result.getKey().startsWith(folder + "/"));
        assertEquals(folder, result.getFolder());
        assertEquals(ResourceType.TEXT, result.getContentType());

        verify(minioClient).putObject(any(PutObjectArgs.class));
    }

    @Test
    void uploadFile_ShouldThrowExceptionWhenUploadFails() throws Exception {
        ReflectionTestUtils.setField(minioService, "bucketName", "test-bucket");

        when(file.getOriginalFilename()).thenReturn("test.txt");
        when(file.getSize()).thenReturn(1024L);
        when(file.getInputStream()).thenReturn(new ByteArrayInputStream(new byte[0]));
        when(file.getContentType()).thenReturn("text/plain");
        doThrow(new RuntimeException("Upload failed")).when(minioClient).putObject(any(PutObjectArgs.class));

        assertThrows(MinioFileException.class, () -> minioService.uploadFile(file, "folder"));
    }

    @Test
    void downloadFile_ShouldThrowExceptionWhenDownloadFails() throws Exception {
        ReflectionTestUtils.setField(minioService, "bucketName", "test-bucket");
        when(minioClient.getObject(any(GetObjectArgs.class))).thenThrow(new RuntimeException("Download failed"));

        assertThrows(MinioFileException.class, () -> minioService.downloadFile("folder/test.txt"));
    }

    @Test
    void deleteFile_ShouldDeleteFile() throws Exception {
        String bucketName = "test-bucket";
        String key = "folder/test.txt";

        ReflectionTestUtils.setField(minioService, "bucketName", bucketName);

        minioService.deleteFile(key);

        verify(minioClient).removeObject(argThat(args ->
                args.bucket().equals(bucketName) &&
                        args.object().equals(key)
        ));
    }

    @Test
    void deleteFile_ShouldThrowExceptionWhenDeleteFails() throws Exception {
        ReflectionTestUtils.setField(minioService, "bucketName", "test-bucket");
        doThrow(new RuntimeException("Delete failed")).when(minioClient).removeObject(any(RemoveObjectArgs.class));

        assertThrows(MinioFileException.class, () -> minioService.deleteFile("folder/test.txt"));
    }
}

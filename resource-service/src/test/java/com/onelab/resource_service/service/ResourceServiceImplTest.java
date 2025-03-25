package com.onelab.resource_service.service;

import com.onelab.resource_service.entity.Resource;
import com.onelab.resource_service.mapper.ResourceMapper;
import com.onelab.resource_service.repository.ResourceRepository;
import com.onelab.resource_service.service.impl.ResourceServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.onelab.common.dto.response.FileDto;
import org.onelab.common.enums.ResourceType;
import org.onelab.common.exception.FileException;
import org.springframework.core.io.InputStreamResource;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Optional;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResourceServiceImplTest {

    @Mock
    private ResourceRepository resourceRepository;

    @Mock
    private MinioService minioService;

    @Mock
    private ResourceMapper resourceMapper;

    @Mock
    private MultipartFile file;

    @InjectMocks
    private ResourceServiceImpl resourceService;

    @Test
    void getFileDtoById_ShouldReturnFileDto() throws IOException {
        Resource resource = new Resource();
        resource.setId(1L);
        resource.setName("test.png");
        resource.setKey("folder/test.png");
        resource.setContentType(ResourceType.IMAGE);

        InputStreamResource inputStreamResource = new InputStreamResource(
                new ByteArrayInputStream(new byte[0])
        );

        when(resourceRepository.findById(1L)).thenReturn(Optional.of(resource));
        when(minioService.downloadFile("folder/test.png")).thenReturn(
                new ByteArrayInputStream(new byte[0])
        );

        FileDto result = resourceService.getFileDtoById(1L);

        assertEquals("test.png", result.name());
        assertEquals("image/png", result.contentType());
        assertNotNull(result.file());
    }

    @Test
    void saveResource_ShouldSaveAndReturnResource() throws IOException {
        Resource resource = new Resource();
        resource.setId(1L);
        resource.setName("test.png");

        when(minioService.uploadFile(file, "folder")).thenReturn(resource);
        when(resourceRepository.save(resource)).thenReturn(resource);

        Resource result = resourceService.saveResource(file, "folder");

        assertEquals(1L, result.getId());
        verify(minioService).uploadFile(file, "folder");
        verify(resourceRepository).save(resource);
    }

    @Test
    void updateResource_ShouldUpdateResource() throws IOException {
        Resource oldResource = new Resource();
        oldResource.setId(1L);
        oldResource.setKey("old/key");
        oldResource.setFolder("folder");

        Resource newResource = new Resource();
        newResource.setName("new.png");
        newResource.setKey("new/key");

        when(resourceRepository.findById(1L)).thenReturn(Optional.of(oldResource));
        when(minioService.uploadFile(file, "folder")).thenReturn(newResource);
        when(resourceRepository.save(oldResource)).thenReturn(oldResource);

        Resource result = resourceService.updateResource(1L, file);

        assertEquals(1L, result.getId());
        verify(minioService).deleteFile("old/key");
        verify(minioService).uploadFile(file, "folder");
        verify(resourceMapper).updateResource(oldResource, newResource);
    }

    @Test
    void deleteResource_ShouldDeleteResource() {
        Resource resource = new Resource();
        resource.setId(1L);
        resource.setKey("folder/test.png");

        when(resourceRepository.findById(1L)).thenReturn(Optional.of(resource));

        resourceService.deleteResource(1L);

        verify(minioService).deleteFile("folder/test.png");
        verify(resourceRepository).deleteById(1L);
    }

    @Test
    void checkImage_ShouldThrowWhenFileIsNull() {
        assertThrows(FileException.class, () -> resourceService.checkImage(null));
    }

    @Test
    void checkImage_ShouldThrowWhenFileIsEmpty() {
        when(file.isEmpty()).thenReturn(true);

        assertThrows(FileException.class, () -> resourceService.checkImage(file));
    }

    @Test
    void checkImage_ShouldThrowWhenInvalidType() {
        ReflectionTestUtils.setField(resourceService, "allowedImageTypes", Set.of("image/png"));

        when(file.getContentType()).thenReturn("image/jpg");
        when(file.isEmpty()).thenReturn(false);
        when(file.getOriginalFilename()).thenReturn("test.jpg");

        assertThrows(FileException.class, () -> resourceService.checkImage(file));

        verify(file, atLeastOnce()).getContentType();
    }

    @Test
    void checkFile_ShouldThrowWhenFileIsNull() {
        assertThrows(FileException.class, () -> resourceService.checkFile(null));
    }

    @Test
    void checkFile_ShouldThrowWhenFileIsEmpty() {
        when(file.isEmpty()).thenReturn(true);

        assertThrows(FileException.class, () -> resourceService.checkFile(file));
    }

}

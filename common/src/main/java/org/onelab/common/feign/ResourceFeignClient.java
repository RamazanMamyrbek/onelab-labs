package org.onelab.common.feign;

import org.onelab.common.dto.response.ResourceResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@FeignClient(name = "resource-service")
public interface ResourceFeignClient {

    @GetMapping("/api/resources/file/{resourceId}")
    ResponseEntity<ResourceResponseDto> getResource(@PathVariable("resourceId") Long resourceId);

    @GetMapping("/api/resources/{id}")
    ResponseEntity<ResourceResponseDto> getResourceDetails(@PathVariable Long id);

    @PostMapping(value = "/api/resources/{folder}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<ResourceResponseDto> uploadFile(@PathVariable("file") MultipartFile file,
                                                   @PathVariable("folder") String folder);

    @PutMapping(value = "/api/resources/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<ResourceResponseDto> updateFile(@PathVariable Long id,
                                                   @PathVariable("file") MultipartFile file);

    @DeleteMapping("/api/resources/{id}")
    ResponseEntity<Void> deleteFile(@PathVariable Long id);

    @PostMapping(value = "/api/resources/check/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<Void> checkImage(@PathVariable("file") MultipartFile file);

    @PostMapping(value = "/api/resources/check/file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<Void> checkFile(@PathVariable("file") MultipartFile file);
}

package org.onelab.common.dto.response;

import org.springframework.core.io.InputStreamResource;

public record FileDto(
        InputStreamResource file,
        String name,
        String contentType
) {
}
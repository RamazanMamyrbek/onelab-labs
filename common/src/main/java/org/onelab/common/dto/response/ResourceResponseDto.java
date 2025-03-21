package org.onelab.common.dto.response;

import org.onelab.common.enums.ResourceType;

import java.math.BigInteger;


public record ResourceResponseDto(
        Long id,
        String name,
        String key,
        String folder,
        BigInteger size,
        ResourceType contentType
) {
}

package com.onelab.resource_service.mapper;

import com.onelab.resource_service.entity.Resource;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.onelab.common.dto.response.ResourceResponseDto;

@Mapper(componentModel = "spring")
public interface ResourceMapper {
    @Mapping(target = "id", ignore = true)
    void updateResource(@MappingTarget Resource oldResource, Resource newResource);

    ResourceResponseDto mapToResourceResponseDto(Resource resource);
}

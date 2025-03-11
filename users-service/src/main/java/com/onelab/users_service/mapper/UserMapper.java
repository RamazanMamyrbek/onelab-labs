package com.onelab.users_service.mapper;

import com.onelab.users_service.entity.Users;
import com.onelab.users_service.entity.enums.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.onelab.common.dto.response.UsersResponseDto;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "role", source = "role", qualifiedByName = "mapRoleToString")
    UsersResponseDto mapToUserResponseDTO(Users user);

    @Named("mapRoleToString")
    default String mapRoleToString(Role role) {
        return role.name();
    }
}

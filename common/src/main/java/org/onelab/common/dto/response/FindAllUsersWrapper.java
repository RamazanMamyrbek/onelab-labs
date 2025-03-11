package org.onelab.common.dto.response;

import java.util.List;

public record FindAllUsersWrapper(
        List<UsersResponseDto> list
) {
}

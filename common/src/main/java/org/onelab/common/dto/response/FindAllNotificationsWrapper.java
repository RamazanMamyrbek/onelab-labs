package org.onelab.common.dto.response;

import org.onelab.common.dto.request.NotificationDto;

import java.util.List;

public record FindAllNotificationsWrapper(
        List<NotificationResponseDto> list
) {
}

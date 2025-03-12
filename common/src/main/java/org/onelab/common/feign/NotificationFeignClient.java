package org.onelab.common.feign;

import org.onelab.common.dto.response.NotificationResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

@FeignClient(name = "notification-service")
public interface NotificationFeignClient {

    @GetMapping("/api/notifications/{userId}")
    List<NotificationResponseDto> getNotificationsByUserId(@RequestHeader(name = "Authorization") String token, @PathVariable Long userId);
}

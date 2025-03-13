package org.onelab.common.feign;

import org.onelab.common.dto.response.UsersResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "users-service", contextId = "users-service")
public interface UserFeignClient {

    @GetMapping("/api/users/profile")
    ResponseEntity<UsersResponseDto> getProfileInfo(@RequestHeader(name = "Authorization") String token);
}

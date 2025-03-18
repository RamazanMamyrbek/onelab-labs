package org.onelab.common.feign;

import org.onelab.common.dto.response.UsersResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.w3c.dom.html.HTMLDocument;

import java.util.List;

@FeignClient(name = "users-service", contextId = "users-service")
public interface UserFeignClient {

    @GetMapping("/api/users/profile")
    ResponseEntity<UsersResponseDto> getProfileInfo(@RequestHeader(name = "Authorization") String token);

    @GetMapping("/api/users/courses/{courseId}/students")
    ResponseEntity<List<UsersResponseDto>> getStudentsForCourse(@PathVariable Long courseId,
                                                                @RequestHeader(name = "Authorization") String token);
}

package org.onelab.common.feign;

import org.onelab.common.dto.response.CourseResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@FeignClient(name = "courses-service")
public interface CourseFeignClient {

    @PostMapping("/api/courses/byIds")
    ResponseEntity<List<CourseResponseDto>> getCoursesByIds(@RequestBody Set<Long> ids,
                                                            @RequestHeader("Authorization") String token);

    @GetMapping("/api/courses/{id}")
    ResponseEntity<CourseResponseDto> getCourseById(@PathVariable Long id, @RequestHeader("Authorization") String token);
}

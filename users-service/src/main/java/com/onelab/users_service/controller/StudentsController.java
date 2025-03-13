package com.onelab.users_service.controller;

import com.onelab.users_service.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.onelab.common.dto.request.AssignCourseDto;
import org.onelab.common.dto.response.CourseResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users/students")
@Tag(name = "StudentsController")
@SecurityRequirement(name = "JWT")
public class StudentsController {
    private final UserService userService;

    @PostMapping
    @Operation(summary = "Enroll to course (ONLY FOR TEACHERS)")
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    public ResponseEntity<Void> enrollToCourse(@RequestBody AssignCourseDto assignCourseDto,
                                               HttpServletRequest request) {
        userService.assignCourseToStudent(assignCourseDto, request.getHeader("Authorization"));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{studentId}/courses")
    @Operation(summary = "Get enrolled course for student")
    public ResponseEntity<List<CourseResponseDto>> getStudentCourses(@PathVariable Long studentId,
                                                                     HttpServletRequest request) {
        List<CourseResponseDto> responseDtoList = userService.getStudentCourses(studentId, request.getHeader("Authorization"));
        return ResponseEntity.ok(responseDtoList);
    }
}

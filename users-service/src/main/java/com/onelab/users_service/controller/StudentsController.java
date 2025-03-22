package com.onelab.users_service.controller;

import com.onelab.users_service.service.UserService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.onelab.common.dto.request.AssignCourseDto;
import org.onelab.common.dto.request.ExpelFromCourseDto;
import org.onelab.common.dto.response.CourseResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users/students")
@Tag(name = "StudentsController")
@SecurityRequirement(name = "JWT")
public class StudentsController {
    private final UserService userService;

    @PostMapping("/courses/enroll")
    @Operation(summary = "Enroll to student to your course (ONLY FOR TEACHERS)")
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    public ResponseEntity<Void> enrollToCourse(@RequestBody AssignCourseDto assignCourseDto,
                                               Principal principal,
                                               HttpServletRequest request) {
        userService.assignCourseToStudent(assignCourseDto, principal.getName(), request.getHeader("Authorization"));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/courses/expel")
    @Operation(summary = "Expel student from your course (ONLY FOR TEACHERS)")
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    public ResponseEntity<Void> expelFromCourse(@RequestBody ExpelFromCourseDto expelFromCourseDto,
                                               Principal principal,
                                               HttpServletRequest request) {
        userService.expelStudentFromCourse(expelFromCourseDto, principal.getName(), request.getHeader("Authorization"));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{studentId}/courses")
    @Operation(summary = "Get enrolled course for student")
    public ResponseEntity<List<CourseResponseDto>> getStudentCourses(@PathVariable Long studentId,
                                                                     HttpServletRequest request) {
        List<CourseResponseDto> responseDtoList = userService.getStudentCourses(studentId, request.getHeader("Authorization"));
        return ResponseEntity.ok(responseDtoList);
    }

    @Hidden
    @DeleteMapping("/courses/{courseId}")
    public ResponseEntity<Void> removeCourseFromStudents(@PathVariable Long courseId) {
        userService.removeCourseFromStudents(courseId);
        return ResponseEntity.noContent().build();
    }
}

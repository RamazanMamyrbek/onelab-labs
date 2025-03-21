package com.onelab.courses_service.controller;

import com.onelab.courses_service.service.CourseService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.onelab.common.dto.request.*;
import org.onelab.common.dto.response.CourseResponseDto;
import org.onelab.common.dto.response.LessonResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/courses")
@Tag(name = "CoursesRestController")
@SecurityRequirement(name = "JWT")
@Validated
public class CoursesRestController {
    private final CourseService courseService;

    @GetMapping
    @Operation(summary = "Get all courses")
    public ResponseEntity<List<CourseResponseDto>> getAll() {
        List<CourseResponseDto> courseResponseDtos = courseService.getAllCourses();
        return ResponseEntity.ok(courseResponseDtos);
    }


    @GetMapping("/{id}")
    @Operation(summary = "Get course by id")
    public ResponseEntity<CourseResponseDto> getCourseById(@PathVariable Long id) {
        CourseResponseDto courseResponseDto = courseService.getCourse(id);
        return ResponseEntity.ok(courseResponseDto);
    }

    @PostMapping("/byIds")
    @Operation(summary = "Get courses by ids")
    @Hidden
    public ResponseEntity<List<CourseResponseDto>> getCoursesByIds(@RequestBody Set<Long> ids) {
        List<CourseResponseDto> responseDtoList = courseService.findAllById(ids);
        return ResponseEntity.ok(responseDtoList);
    }

    @GetMapping("/{courseId}/courses")
    @Operation(summary = "Get lessons for course")
    public ResponseEntity<List<LessonResponseDto>> getLessons(@PathVariable Long courseId) {
        List<LessonResponseDto> responseDtos = courseService.getLessonsForCourse(courseId);
        return ResponseEntity.ok(responseDtos);
    }

    @PostMapping
    @Operation(summary = "Create a course (ONLY FOR TEACHERS)")
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    public ResponseEntity<CourseResponseDto> createCourse(@RequestBody @Validated CourseRequestDto courseRequestDto,
                                                          HttpServletRequest request) {
        CourseResponseDto responseDto = courseService.createCourse(courseRequestDto, request.getHeader("Authorization"));
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @PostMapping("/lessons")
    @Operation(summary = "Add lesson to a course (ONLY FOR TEACHERS)")
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    public ResponseEntity<LessonResponseDto> addLessonToCourse(@RequestBody @Validated LessonRequestDto lessonRequestDto,
                                                          Principal principal,
                                                          HttpServletRequest request) {
        LessonResponseDto responseDto = courseService.addLessonToCourse(lessonRequestDto, principal.getName(), request.getHeader("Authorization"));
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @PutMapping
    @Operation(summary = "Edit course (ONLY FOR TEACHERS)")
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    public ResponseEntity<CourseResponseDto> editCourse(@RequestBody @Validated CourseUpdateRequestDto requestDto,
                                                        HttpServletRequest request) {
        CourseResponseDto responseDto = courseService.updateCourse(requestDto, request.getHeader("Authorization"));
        return ResponseEntity.ok(responseDto);
    }

    @PutMapping("/lessons")
    @Operation(summary = "Edit lesson (ONLY FOR TEACHERS)")
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    public ResponseEntity<LessonResponseDto> editLesson(@RequestBody @Validated LessonUpdateRequestDto requestDto,
                                                        HttpServletRequest request) {
        LessonResponseDto responseDto = courseService.updateLesson(requestDto, request.getHeader("Authorization"));
        return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping("/{courseId}")
    @Operation(summary = "Delete a course (ONLY FOR TEACHERS)")
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    public ResponseEntity<Void> deleteCourse(@PathVariable Long courseId,
                                             HttpServletRequest request) {
        courseService.deleteCourse(new CourseDeleteRequestDto(courseId), request.getHeader("Authorization"));
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/lessons/{lessonId}")
    @Operation(summary = "Delete a lesson (ONLY FOR TEACHERS)")
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    public ResponseEntity<Void> deleteLesson(@PathVariable Long lessonId,
                                             HttpServletRequest request) {
        courseService.deleteLesson(new LessonDeleteRequestDto(lessonId), request.getHeader("Authorization"));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    @Operation(summary = "Search courses by criteries")
    public ResponseEntity<List<CourseResponseDto>> searchCourses(
            @RequestParam(value = "query", required = false) String query,
            @RequestParam(value = "minPrice", required = false, defaultValue = "0") Long minPrice,
            @RequestParam(value = "maxPrice", required = false) Long maxPrice,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        List<CourseResponseDto> courses = courseService.searchCourses(query, minPrice, maxPrice, page, size);
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/{courseId}/students")
    @Operation(summary = "Get students count for course")
    public ResponseEntity<Long> getStudentCount(@PathVariable Long courseId,
                                                HttpServletRequest httpServletRequest) {
        Long studentsCount = courseService.getStudentCount(courseId, httpServletRequest);
        return ResponseEntity.ok(studentsCount);
    }

    @PostMapping(value = "/lessons/{lessonId}/upload-file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload a file for a lesson")
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    public ResponseEntity<LessonResponseDto> uploadFileForLesson(@PathVariable Long lessonId,
                                                                 @RequestParam MultipartFile file,
                                                                 Principal principal,
                                                                 HttpServletRequest servletRequest){
        LessonResponseDto responseDto = courseService.uploadFileForLesson(lessonId, file, principal.getName(), servletRequest.getHeader("Authorization"));
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

}

package com.onelab.course_service.junit.controller;

import com.onelab.users_service.controller.StudentsController;
import com.onelab.users_service.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.onelab.common.dto.request.AssignCourseDto;
import org.onelab.common.dto.request.ExpelFromCourseDto;
import org.onelab.common.dto.response.CourseResponseDto;
import org.springframework.http.ResponseEntity;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class StudentsControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private Principal principal;

    @InjectMocks
    private StudentsController studentsController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(request.getHeader("Authorization")).thenReturn("Bearer token");
        when(principal.getName()).thenReturn("testUser");
    }

    @Test
    void enrollToCourse_ShouldReturnNoContent() {
        AssignCourseDto assignCourseDto = new AssignCourseDto(1L, 1L);

        ResponseEntity<Void> response = studentsController.enrollToCourse(assignCourseDto, principal, request);

        assertEquals(204, response.getStatusCodeValue());
        verify(userService, times(1)).assignCourseToStudent(assignCourseDto, "testUser", "Bearer token");
    }

    @Test
    void expelFromCourse_ShouldReturnNoContent() {
        ExpelFromCourseDto expelFromCourseDto = new ExpelFromCourseDto(1L, 1L);

        ResponseEntity<Void> response = studentsController.expelFromCourse(expelFromCourseDto, principal, request);

        assertEquals(204, response.getStatusCodeValue());
        verify(userService, times(1)).expelStudentFromCourse(expelFromCourseDto, "testUser", "Bearer token");
    }

    @Test
    void getStudentCourses_ShouldReturnListOfCourses() {
        CourseResponseDto course = new CourseResponseDto(1L, "Java", "Java Course", 100L, LocalDateTime.now(), 2L);
        List<CourseResponseDto> courses = List.of(course);

        when(userService.getStudentCourses(1L, "Bearer token")).thenReturn(courses);

        ResponseEntity<List<CourseResponseDto>> response = studentsController.getStudentCourses(1L, request);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(courses, response.getBody());
        verify(userService, times(1)).getStudentCourses(1L, "Bearer token");
    }

    @Test
    void buyCourse_ShouldReturnCourseResponseDto() {
        CourseResponseDto course = new CourseResponseDto(1L, "Java", "Java Course", 100L, LocalDateTime.now(), 2L);

        when(userService.buyCourse(1L, "testUser", "Bearer token")).thenReturn(course);

        ResponseEntity<CourseResponseDto> response = studentsController.buyCourse(1L, principal, request);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(course, response.getBody());
        verify(userService, times(1)).buyCourse(1L, "testUser", "Bearer token");
    }

    @Test
    void removeCourseFromStudents_ShouldReturnNoContent() {
        ResponseEntity<Void> response = studentsController.removeCourseFromStudents(1L);

        assertEquals(204, response.getStatusCodeValue());
        verify(userService, times(1)).removeCourseFromStudents(1L);
    }


}
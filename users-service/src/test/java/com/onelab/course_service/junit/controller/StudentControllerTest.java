package com.onelab.course_service.junit.controller;

import com.onelab.users_service.controller.StudentsController;
import com.onelab.users_service.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.onelab.common.dto.request.AssignCourseDto;
import org.onelab.common.dto.response.CourseResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import jakarta.servlet.http.HttpServletRequest;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentControllerTest {

    @InjectMocks
    private StudentsController studentsController;

    @Mock
    private UserService userService;

    @Test
    void shouldEnrollToCourseSuccessfully() {
        AssignCourseDto assignCourseDto = new AssignCourseDto(1L, 102L);
        String token = "Bearer test-token";
        HttpServletRequest request = mock(HttpServletRequest.class);

        when(request.getHeader("Authorization")).thenReturn(token);

        ResponseEntity<Void> response = studentsController.enrollToCourse(assignCourseDto, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(userService, times(1)).assignCourseToStudent(assignCourseDto, token, request.getHeader("Authorization"));
    }

    @Test
    void shouldGetStudentCoursesSuccessfully() {
        Long studentId = 1L;
        String token = "Bearer test-token";
        HttpServletRequest request = mock(HttpServletRequest.class);

        List<CourseResponseDto> courses = List.of(
                new CourseResponseDto(102L, "Test Course 1", "DESCRIPTION", 100000L, LocalDateTime.now(), 1L),
                new CourseResponseDto(103L, "Test Course 2", "DESCRIPTION", 100000L, LocalDateTime.now(),1L)
        );

        when(request.getHeader("Authorization")).thenReturn(token);
        when(userService.getStudentCourses(studentId, token)).thenReturn(courses);

        ResponseEntity<List<CourseResponseDto>> response = studentsController.getStudentCourses(studentId, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(2);
        assertThat(response.getBody().get(0).id()).isEqualTo(102L);
        assertThat(response.getBody().get(0).name()).isEqualTo("Test Course 1");
        assertThat(response.getBody().get(1).id()).isEqualTo(103L);
        assertThat(response.getBody().get(1).name()).isEqualTo("Test Course 2");

        verify(userService, times(1)).getStudentCourses(studentId, token);
    }
}

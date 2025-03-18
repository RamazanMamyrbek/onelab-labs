package com.onelab.student_service.unit.controller;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.onelab.courses_service.controller.CoursesRestController;
import com.onelab.courses_service.service.CourseService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.onelab.common.dto.request.CourseRequestDto;
import org.onelab.common.dto.request.CourseUpdateRequestDto;
import org.onelab.common.dto.request.LessonRequestDto;
import org.onelab.common.dto.response.CourseResponseDto;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class CourseRestControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CourseService courseService;

    @InjectMocks
    private CoursesRestController coursesRestController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(coursesRestController).build();
    }

    @Test
    void shouldGetAllCourses() throws Exception {
        CourseResponseDto course = new CourseResponseDto(1L, "Java Basics", "",100000L, LocalDateTime.now(),100L);
        when(courseService.getAllCourses()).thenReturn(List.of(course));

        String response = mockMvc.perform(get("/api/courses"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        assertThat(response).contains("Java Basics");
        verify(courseService, times(1)).getAllCourses();
    }

    @Test
    void shouldGetCourseById() throws Exception {
        CourseResponseDto course = new CourseResponseDto(1L, "Java Basics", "",100000L, LocalDateTime.now(),100L);
        when(courseService.getCourse(1L)).thenReturn(course);

        String response = mockMvc.perform(get("/api/courses/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        assertThat(response).contains("Java Basics");
        verify(courseService, times(1)).getCourse(1L);
    }

    @Test
    void shouldGetCoursesByIds() throws Exception {
        CourseResponseDto course = new CourseResponseDto(1L, "Java Basics", "",100000L, LocalDateTime.now(),100L);
        when(courseService.findAllById(Set.of(1L))).thenReturn(List.of(course));

        String response = mockMvc.perform(post("/api/courses/byIds")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Set.of(1L))))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertThat(response).contains("Java Basics");
        verify(courseService, times(1)).findAllById(Set.of(1L));
    }

    @Test
    void shouldCreateCourse() throws Exception {
        CourseRequestDto requestDto = new CourseRequestDto("Spring Boot", "", 100000l);
        CourseResponseDto responseDto = new CourseResponseDto(1L, "Spring Boot", "",100000L, LocalDateTime.now(),100L);

        when(courseService.createCourse(any(), any())).thenReturn(responseDto);

        String response = mockMvc.perform(post("/api/courses")
                        .header("Authorization", "Bearer token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        assertThat(response).contains("Spring Boot");
        verify(courseService, times(1)).createCourse(any(), any());
    }

    @Test
    void shouldEditCourse() throws Exception {
        CourseUpdateRequestDto requestDto = new CourseUpdateRequestDto(1L, "Updated Java Basics", "", 100000L);
        CourseResponseDto responseDto = new CourseResponseDto(1L, "Updated Java Basics","",100000L, LocalDateTime.now(),100L);

        when(courseService.updateCourse(any(), any())).thenReturn(responseDto);

        String response = mockMvc.perform(put("/api/courses")
                        .header("Authorization", "Bearer token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertThat(response).contains("Updated Java Basics");
        verify(courseService, times(1)).updateCourse(any(), any());
    }

    @Test
    void shouldDeleteCourse() throws Exception {
        doNothing().when(courseService).deleteCourse(any(), any());

        mockMvc.perform(delete("/api/courses/1")
                        .header("Authorization", "Bearer token"))
                .andExpect(status().isNoContent());

        verify(courseService, times(1)).deleteCourse(any(), any());
    }

    @Test
    void shouldAddLessonToCourse() throws Exception {
        LessonRequestDto requestDto = new LessonRequestDto("Lesson 1", 1L);
        Principal mockPrincipal = mock(Principal.class);
        when(mockPrincipal.getName()).thenReturn("teacher1");

        when(courseService.addLessonToCourse(any(), any(), any())).thenReturn(null);

        mockMvc.perform(post("/api/courses/lessons")
                        .principal(mockPrincipal) // <-- Добавляем mock Principal
                        .header("Authorization", "Bearer token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated());

        verify(courseService, times(1)).addLessonToCourse(any(), any(), any());
    }

    @Test
    void getStudentCount_ShouldReturnStudentCount_WhenCourseExists() throws Exception {
        Long courseId = 1L;
        Long expectedCount = 42L;

        when(courseService.getStudentCount(eq(courseId), any(HttpServletRequest.class)))
                .thenReturn(expectedCount);

        mockMvc.perform(get("/api/courses/{courseId}/students", courseId))
                .andExpect(status().isOk())
                .andExpect(content().string("42"));

        verify(courseService).getStudentCount(eq(courseId), any(HttpServletRequest.class));
    }

    @Test
    void searchCourses_ShouldReturnFilteredCourses() throws Exception {
        List<CourseResponseDto> mockCourses = List.of(
                new CourseResponseDto(1L, "Java Basics", "",5000L, LocalDateTime.now(), 1L),
                new CourseResponseDto(2L, "Spring Boot", "",7000L, LocalDateTime.now(), 1L)
        );

        when(courseService.searchCourses(anyString(), anyLong(), anyLong(), anyInt(), anyInt()))
                .thenReturn(mockCourses);

        mockMvc.perform(get("/api/courses/search")
                        .param("query", "Java")
                        .param("minPrice", "1000")
                        .param("maxPrice", "10000")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Java Basics"))
                .andExpect(jsonPath("$[0].price").value(5000))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Spring Boot"))
                .andExpect(jsonPath("$[1].price").value(7000));

        verify(courseService).searchCourses("Java", 1000L, 10000L, 0, 10);
    }
}
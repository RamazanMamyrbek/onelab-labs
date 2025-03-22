package com.onelab.student_service.unit.service;

import com.onelab.courses_service.entity.Course;
import com.onelab.courses_service.entity.Lesson;
import com.onelab.courses_service.entity.elastic.CourseIndex;
import com.onelab.courses_service.mapper.CourseMapper;
import com.onelab.courses_service.repository.elastic.CourseSearchRepository;
import com.onelab.courses_service.repository.jpa.CourseRepository;
import com.onelab.courses_service.repository.jpa.LessonRepository;
import com.onelab.courses_service.service.impl.CourseServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.onelab.common.dto.request.*;
import org.onelab.common.dto.response.CourseResponseDto;
import org.onelab.common.dto.response.LessonResponseDto;
import org.onelab.common.dto.response.UsersResponseDto;
import org.onelab.common.enums.Role;
import org.onelab.common.exception.BadRequestException;
import org.onelab.common.feign.UserFeignClient;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CoursesServiceImplTest {

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private LessonRepository lessonRepository;

    @Mock
    private CourseMapper courseMapper;

    @Mock
    private CourseSearchRepository courseSearchRepository;

    @Mock
    private UserFeignClient userFeignClient;

    @InjectMocks
    private CourseServiceImpl courseService;

    private Course course;
    private Lesson lesson;
    private UsersResponseDto userDto;
    private CourseResponseDto courseResponseDto;
    private LessonResponseDto lessonResponseDto;

    @BeforeEach
    void setUp() {
        course = new Course();
        course.setId(1L);
        course.setName("Java Course");
        course.setTeacherId(100L);

        lesson = new Lesson();
        lesson.setId(10L);
        lesson.setTitle("Introduction");
        lesson.setCourse(course);

        userDto = new UsersResponseDto(100L, "teacher@gmail.com", "Teacher Name","Kazakhstan", 25L, Role.ROLE_TEACHER.name());

        courseResponseDto = new CourseResponseDto(1L, "Java Course", "",100000L, LocalDateTime.now(), 100L);
        lessonResponseDto = new LessonResponseDto(10L, "Introduction", 1L);
    }

    @Test
    void shouldReturnAllCourses() {
        when(courseRepository.findAll()).thenReturn(List.of(course));
        when(courseMapper.mapToCourseResponseDto(course)).thenReturn(courseResponseDto);

        List<CourseResponseDto> courses = courseService.getAllCourses();

        assertThat(courses).hasSize(1);
        assertThat(courses.get(0).id()).isEqualTo(1L);
        verify(courseRepository).findAll();
    }

    @Test
    void shouldReturnLessonsForCourse() {
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(lessonRepository.findByCourse(course)).thenReturn(List.of(lesson));
        when(courseMapper.mapToLessonResponseDto(lesson)).thenReturn(lessonResponseDto);

        List<LessonResponseDto> lessons = courseService.getLessonsForCourse(1L, principal.getName(), servletRequest.getHeader("Authorization"));

        assertThat(lessons).hasSize(1);
        assertThat(lessons.get(0).id()).isEqualTo(10L);
        verify(lessonRepository).findByCourse(course);
    }

    @Test
    void shouldCreateCourse() {
        CourseRequestDto requestDto = new CourseRequestDto("Java Course", "", 100000L);
        when(userFeignClient.getProfileInfo("token")).thenReturn(ResponseEntity.ok(userDto));
        when(courseMapper.mapToCourseResponseDto(any())).thenReturn(courseResponseDto);

        CourseResponseDto result = courseService.createCourse(requestDto, "token");

        assertThat(result.name()).isEqualTo("Java Course");
        verify(courseRepository).save(any(Course.class));
    }

    @Test
    void shouldAddLessonToCourse() {
        LessonRequestDto requestDto = new LessonRequestDto("New Lesson", 1L);
        when(userFeignClient.getProfileInfo("token")).thenReturn(ResponseEntity.ok(userDto));
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(lessonRepository.save(any())).thenReturn(lesson);
        when(courseMapper.mapToLessonResponseDto(any())).thenReturn(lessonResponseDto);

        LessonResponseDto result = courseService.addLessonToCourse(requestDto, "teacher@gmail.com", "token");

        assertThat(result.title()).isEqualTo("Introduction");
        verify(lessonRepository).save(any());
    }

    @Test
    void shouldUpdateCourse() {
        CourseUpdateRequestDto requestDto = new CourseUpdateRequestDto(1L, "Updated Course", "", 100000L);
        when(userFeignClient.getProfileInfo("token")).thenReturn(ResponseEntity.ok(userDto));
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(courseRepository.save(any())).thenReturn(course);
        when(courseMapper.mapToCourseResponseDto(any())).thenReturn(courseResponseDto);

        CourseResponseDto result = courseService.updateCourse(requestDto, "token");

        assertThat(result.name()).isEqualTo("Java Course");
        verify(courseRepository).save(any());
    }

    @Test
    void shouldThrowExceptionIfNotTeacherUpdatingCourse() {
        CourseUpdateRequestDto requestDto = new CourseUpdateRequestDto(1L, "Updated Course", "", 100000L);
        UsersResponseDto anotherUser = new UsersResponseDto(200L, "teacher2@gmail.com","Other Teacher","Kazakhstan", 25L, Role.ROLE_TEACHER.name());

        when(userFeignClient.getProfileInfo("token")).thenReturn(ResponseEntity.ok(anotherUser));
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));

        assertThatThrownBy(() -> courseService.updateCourse(requestDto, "token"))
                .isInstanceOf(BadRequestException.class);

        verify(courseRepository, never()).save(any());
    }

    @Test
    void shouldDeleteCourse() {
        CourseDeleteRequestDto requestDto = new CourseDeleteRequestDto(1L);
        when(userFeignClient.getProfileInfo("token")).thenReturn(ResponseEntity.ok(userDto));
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));

        courseService.deleteCourse(requestDto, "token");

        verify(courseRepository).deleteById(1L);
    }

    @Test
    void shouldFindCoursesByIdSet() {
        lenient().when(courseRepository.findAllById(Set.of(1L))).thenReturn(List.of(course));
        lenient().when(courseMapper.mapToCourseResponseDto(course)).thenReturn(courseResponseDto);

        List<CourseResponseDto> result = courseService.findAllById(Set.of(1L));

        assertThat(result).hasSize(1);
        assertThat(result.get(0).id()).isEqualTo(1L);
    }

    @Test
    void shouldReturnStudentCountForCourse() {
        Long courseId = 1L;
        String token = "Bearer some-token";
        HttpServletRequest request = mock(HttpServletRequest.class);

        List<UsersResponseDto> students = List.of(
                new UsersResponseDto(1L, "student1@gmail.com", "Student One", "Kazakhstan", 20L, Role.ROLE_STUDENT.name()),
                new UsersResponseDto(2L, "student2@gmail.com", "Student Two", "Kazakhstan", 22L, Role.ROLE_STUDENT.name())
        );

        when(request.getHeader("Authorization")).thenReturn(token);
        when(userFeignClient.getStudentsForCourse(courseId, token)).thenReturn(ResponseEntity.ok(students));

        Long studentCount = courseService.getStudentCount(courseId, request);

        assertThat(studentCount).isEqualTo(2);
        verify(userFeignClient).getStudentsForCourse(courseId, token);
    }


    @Test
    void shouldReturnFilteredCoursesWhenQueryIsNotBlank() {
        String query = "Java";
        Long minPrice = 1000L;
        Long maxPrice = 5000L;
        List<CourseIndex> courseIndices = List.of(new CourseIndex(1L, "Java Basics"));
        Set<Long> idsSet = Set.of(1L);
        List<CourseResponseDto> courses = List.of(
                new CourseResponseDto(1L, "Java Basics", "Learn Java", 3000L, LocalDateTime.now(),1L)
        );

        when(courseSearchRepository.findByNameContainingIgnoreCase(query)).thenReturn(courseIndices);
        when(courseRepository.findAllById(idsSet)).thenReturn(List.of(new Course(1L, "Java Basics", "Learn Java", 3000L,  LocalDateTime.now(),List.of(),1L)));
        when(courseMapper.mapToCourseResponseDto(any(Course.class))).thenReturn(courses.get(0));

        List<CourseResponseDto> filteredCourses = courseService.searchCourses(query, minPrice, maxPrice, 0, 10);

        assertThat(filteredCourses).hasSize(1);
        assertThat(filteredCourses.get(0).name()).isEqualTo("Java Basics");
    }

}

package com.onelab.student_service.service;


import com.onelab.courses_service.entity.Course;
import com.onelab.courses_service.entity.Lesson;
import com.onelab.courses_service.mapper.CourseMapper;
import com.onelab.courses_service.repository.jpa.CourseRepository;
import com.onelab.courses_service.repository.elastic.CourseSearchRepository;
import com.onelab.courses_service.repository.jpa.LessonRepository;
import com.onelab.courses_service.service.impl.CourseServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.onelab.common.dto.request.*;
import org.onelab.common.dto.response.CourseResponseDto;
import org.onelab.common.dto.response.LessonResponseDto;
import org.onelab.common.exception.ResourceNotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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

    @InjectMocks
    private CourseServiceImpl courseService;

    private Course course;
    private Lesson lesson;
    private CourseResponseDto courseResponseDto;
    private LessonResponseDto lessonResponseDto;

    @BeforeEach
    void setUp() {
        course = new Course();
        course.setId(1L);
        course.setName("Test Course");

        lesson = new Lesson();
        lesson.setId(1L);
        lesson.setTitle("Test Lesson");
        lesson.setCourse(course);

        courseResponseDto = new CourseResponseDto(course.getId(), course.getName(), null);
        lessonResponseDto = new LessonResponseDto(lesson.getId(), lesson.getTitle(), course.getId());
    }

    @Test
    void getAllCourses_ShouldReturnCourseList() {
        when(courseRepository.findAll()).thenReturn(List.of(course));
        when(courseMapper.mapToCourseResponseDto(course)).thenReturn(courseResponseDto);

        List<CourseResponseDto> result = courseService.getAllCourses();

        assertThat(result).isNotEmpty().hasSize(1);
        assertThat(result.get(0).id()).isEqualTo(1L);
    }

    @Test
    void getLessonsForCourse_ShouldReturnLessonsList() {
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(lessonRepository.findByCourse(course)).thenReturn(List.of(lesson));
        when(courseMapper.mapToLessonResponseDto(lesson)).thenReturn(lessonResponseDto);

        List<LessonResponseDto> result = courseService.getLessonsForCourse(1L);

        assertThat(result).isNotEmpty().hasSize(1);
        assertThat(result.get(0).id()).isEqualTo(1L);
    }

    @Test
    void createCourse_ShouldSaveAndReturnCourse() {
        CourseRequestDto requestDto = new CourseRequestDto("New Course");
        Course newCourse = new Course();
        newCourse.setId(2L);
        newCourse.setName("New Course");

        when(courseRepository.save(any(Course.class))).thenReturn(newCourse);
        when(courseMapper.mapToCourseResponseDto(any(Course.class)))
                .thenReturn(new CourseResponseDto(2L, "New Course", null));

        CourseResponseDto result = courseService.createCourse(requestDto);

        assertThat(result.id()).isEqualTo(2L);
        assertThat(result.name()).isEqualTo("New Course");

        verify(courseRepository).save(any(Course.class));
        verify(courseMapper).mapToCourseResponseDto(any(Course.class));
    }



    @Test
    void updateCourse_ShouldUpdateAndReturnCourse() {
        CourseUpdateRequestDto requestDto = new CourseUpdateRequestDto(1L, "Updated Course");

        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(courseRepository.save(any(Course.class))).thenReturn(course);
        when(courseMapper.mapToCourseResponseDto(course)).thenReturn(new CourseResponseDto(1L, "Updated Course", null));

        CourseResponseDto result = courseService.updateCourse(requestDto);

        assertThat(result.name()).isEqualTo("Updated Course");
    }

    @Test
    void updateLesson_ShouldUpdateAndReturnLesson() {
        LessonUpdateRequestDto requestDto = new LessonUpdateRequestDto(1L, "Updated Lesson");

        when(lessonRepository.findById(1L)).thenReturn(Optional.of(lesson));
        when(lessonRepository.save(any(Lesson.class))).thenReturn(lesson);
        when(courseMapper.mapToLessonResponseDto(lesson)).thenReturn(new LessonResponseDto(1L, "Updated Lesson", 1L));

        LessonResponseDto result = courseService.updateLesson(requestDto);

        assertThat(result.title()).isEqualTo("Updated Lesson");
    }

    @Test
    void deleteCourse_ShouldDeleteCourse() {
        doNothing().when(courseRepository).deleteById(1L);

        courseService.deleteCourse(new CourseDeleteRequestDto(1L));

        verify(courseRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteLesson_ShouldDeleteLesson() {
        doNothing().when(lessonRepository).deleteById(1L);

        courseService.deleteLesson(new LessonDeleteRequestDto(1L));

        verify(lessonRepository, times(1)).deleteById(1L);
    }

    @Test
    void setTeacher_ShouldAssignTeacherAndReturnCourse() {
        AssignCourseDto requestDto = new AssignCourseDto(1L, 100L);

        when(courseRepository.findById(anyLong())).thenReturn(Optional.of(course));
        when(courseRepository.save(any(Course.class))).thenReturn(course);
        when(courseMapper.mapToCourseResponseDto(course))
                .thenReturn(new CourseResponseDto(1L, "Test Course", 100L));

        CourseResponseDto result = courseService.setTeacher(requestDto);

        assertThat(result.teacherId()).isEqualTo(100L);
    }


    @Test
    void getCourse_ShouldReturnCourseById() {
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(courseMapper.mapToCourseResponseDto(course)).thenReturn(courseResponseDto);

        CourseResponseDto result = courseService.getCourse(1L);

        assertThat(result.id()).isEqualTo(1L);
    }

    @Test
    void getAllCoursesByTeacher_ShouldReturnCoursesForTeacher() {
        course.setTeacherId(100L);
        lenient().when(courseRepository.findAllByTeacherId(100L)).thenReturn(List.of(course));
        lenient().when(courseMapper.mapToCourseResponseDto(course)).thenReturn(new CourseResponseDto(1L, "Test Course", 100L));

        List<CourseResponseDto> result = courseService.getAllCoursesByTeacher(100L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).teacherId()).isEqualTo(100L);
    }


    @Test
    void findAllById_ShouldReturnCoursesByIds() {
        Set<Long> ids = Set.of(1L);

        lenient().when(courseRepository.findAllById(ids)).thenReturn(List.of(course));
        lenient().when(courseMapper.mapToCourseResponseDto(course)).thenReturn(courseResponseDto);

        List<CourseResponseDto> result = courseService.findAllById(ids);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).id()).isEqualTo(1L);
    }


    @Test
    void getCourseById_ShouldThrowExceptionIfNotFound() {
        when(courseRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> courseService.getCourse(999L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void searchCoursesByName_ShouldReturnMatchingCourses() {
        Course course1 = new Course(1L, "Java Basics", new ArrayList<>(), null);
        Course course2 = new Course(2L, "Advanced Java", new ArrayList<>(), null);

        CourseResponseDto dto1 = new CourseResponseDto(1L, "Java Basics", null);
        CourseResponseDto dto2 = new CourseResponseDto(2L, "Advanced Java", null);

        when(courseSearchRepository.findByNameContainingIgnoreCase("java"))
                .thenReturn(List.of(course1, course2));

        when(courseMapper.mapToCourseResponseDto(course1)).thenReturn(dto1);
        when(courseMapper.mapToCourseResponseDto(course2)).thenReturn(dto2);

        List<CourseResponseDto> result = courseService.searchCoursesByName("java");

        assertThat(result).hasSize(2);
        assertThat(result.get(0).name()).containsIgnoringCase("java");
        assertThat(result.get(1).name()).containsIgnoringCase("java");
    }

}


package com.onelab.student_service.unit.service;

import com.onelab.courses_service.entity.Course;
import com.onelab.courses_service.entity.Lesson;
import com.onelab.courses_service.mapper.CourseMapper;
import com.onelab.courses_service.repository.elastic.CourseSearchRepository;
import com.onelab.courses_service.repository.jpa.CourseRepository;
import com.onelab.courses_service.repository.jpa.LessonRepository;
import com.onelab.courses_service.service.impl.CourseServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.onelab.common.dto.request.CourseDeleteRequestDto;
import org.onelab.common.dto.request.CourseRequestDto;
import org.onelab.common.dto.request.CourseUpdateRequestDto;
import org.onelab.common.dto.request.LessonRequestDto;
import org.onelab.common.dto.response.CourseResponseDto;
import org.onelab.common.dto.response.LessonResponseDto;
import org.onelab.common.dto.response.ResourceResponseDto;
import org.onelab.common.dto.response.UsersResponseDto;
import org.onelab.common.enums.ResourceType;
import org.onelab.common.feign.ResourceFeignClient;
import org.onelab.common.feign.UserFeignClient;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CourseServiceImplTest {

    @Mock private CourseRepository courseRepository;
    @Mock private LessonRepository lessonRepository;
    @Mock private CourseMapper courseMapper;
    @Mock private CourseSearchRepository courseSearchRepository;
    @Mock private UserFeignClient userFeignClient;
    @Mock private ResourceFeignClient resourceFeignClient;
    @Mock private HttpServletRequest request;
    @Mock private MultipartFile file;

    @InjectMocks
    private CourseServiceImpl courseService;

    @Test
    void getAllCourses_ShouldReturnAllCourses() {
        Course course = new Course();
        course.setId(1L);
        CourseResponseDto dto = new CourseResponseDto(1L, "Course", "", 100L, null, 1L);

        when(courseRepository.findAll()).thenReturn(List.of(course));
        when(courseMapper.mapToCourseResponseDto(course)).thenReturn(dto);

        List<CourseResponseDto> result = courseService.getAllCourses();

        assertEquals(1, result.size());
        assertEquals(dto, result.get(0));
    }

    @Test
    void getLessonsForCourse_ShouldReturnLessonsForTeacher() {
        Course course = new Course();
        course.setId(1L);
        course.setTeacherId(1L);

        UsersResponseDto user = new UsersResponseDto(1L, "teacher@test.com", "", "", 30L, "TEACHER", BigDecimal.ZERO, "USD");
        Lesson lesson = new Lesson();
        LessonResponseDto lessonDto = new LessonResponseDto(1L, "Lesson", 1L, null);

        when(userFeignClient.getProfileInfo("token")).thenReturn(ResponseEntity.ok(user));
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(lessonRepository.findByCourse(course)).thenReturn(List.of(lesson));
        when(courseMapper.mapToLessonResponseDto(lesson)).thenReturn(lessonDto);

        List<LessonResponseDto> result = courseService.getLessonsForCourse(1L, "teacher@test.com", "token");

        assertEquals(1, result.size());
        assertEquals(lessonDto, result.get(0));
    }

    @Test
    void createCourse_ShouldCreateNewCourse() {
        CourseRequestDto request = new CourseRequestDto("New Course", "Desc", 100L);
        UsersResponseDto user = new UsersResponseDto(1L, "teacher@test.com", "", "", 30L, "TEACHER", BigDecimal.ZERO, "USD");

        Course savedCourse = new Course();
        savedCourse.setId(1L);
        savedCourse.setName("New Course");
        savedCourse.setDescription("Desc");
        savedCourse.setPrice(100L);
        savedCourse.setTeacherId(1L);

        CourseResponseDto dto = new CourseResponseDto(1L, "New Course", "Desc", 100L, null, 1L);

        when(userFeignClient.getProfileInfo("token")).thenReturn(ResponseEntity.ok(user));

        when(courseRepository.save(any(Course.class))).thenAnswer(invocation -> {
            Course courseToSave = invocation.getArgument(0);
            courseToSave.setId(1L);
            return courseToSave;
        });

        when(courseMapper.mapToCourseResponseDto(any(Course.class))).thenReturn(dto);

        CourseResponseDto result = courseService.createCourse(request, "token");

        assertEquals(dto, result);
        verify(courseSearchRepository).saveAll(any());
    }

    @Test
    void addLessonToCourse_ShouldAddLesson() {
        LessonRequestDto request = new LessonRequestDto("New Lesson", 1L);
        UsersResponseDto user = new UsersResponseDto(1L, "teacher@test.com", "", "", 30L, "TEACHER", BigDecimal.ZERO, "USD");
        Course course = new Course();
        course.setId(1L);
        course.setTeacherId(1L);
        Lesson lesson = new Lesson();
        LessonResponseDto dto = new LessonResponseDto(1L, "New Lesson", 1L, null);

        when(userFeignClient.getProfileInfo("token")).thenReturn(ResponseEntity.ok(user));
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(lessonRepository.save(any(Lesson.class))).thenReturn(lesson);
        when(courseMapper.mapToLessonResponseDto(lesson)).thenReturn(dto);

        LessonResponseDto result = courseService.addLessonToCourse(request, "teacher@test.com", "token");

        assertEquals(dto, result);
        assertTrue(course.getLessons().contains(lesson));
    }

    @Test
    void uploadFileForLesson_ShouldUploadNewFile() {
        UsersResponseDto user = new UsersResponseDto(1L, "teacher@test.com", "", "", 30L, "TEACHER", BigDecimal.ZERO, "USD");
        Course course = new Course();
        course.setTeacherId(1L);
        Lesson lesson = new Lesson();
        lesson.setCourse(course);
        ResourceResponseDto resource = new ResourceResponseDto(1L, "file.txt", "url", "", new BigInteger("23"), ResourceType.IMAGE);
        LessonResponseDto dto = new LessonResponseDto(1L, "Lesson", 1L, 1L);

        ReflectionTestUtils.setField(courseService, "lessonsFolder", "lessons");

        when(userFeignClient.getProfileInfo("token")).thenReturn(ResponseEntity.ok(user));
        when(lessonRepository.findById(1L)).thenReturn(Optional.of(lesson));
        when(resourceFeignClient.uploadFile(file, "lessons")).thenReturn(ResponseEntity.ok(resource));
        when(lessonRepository.save(lesson)).thenReturn(lesson);
        when(courseMapper.mapToLessonResponseDto(lesson)).thenReturn(dto);

        LessonResponseDto result = courseService.uploadFileForLesson(1L, file, "teacher@test.com", "token");

        assertEquals(1L, lesson.getResourceId());
        assertEquals(dto, result);
        verify(resourceFeignClient).uploadFile(file, "lessons");
    }

    @Test
    void getStudentCount_ShouldReturnCount() {
        List<UsersResponseDto> students = List.of(
                new UsersResponseDto(1L, "", "", "", 20L, "STUDENT", BigDecimal.ZERO, "USD"),
                new UsersResponseDto(2L, "", "", "", 21L, "STUDENT", BigDecimal.ZERO, "USD")
        );

        when(userFeignClient.getStudentsForCourse(1L, "token")).thenReturn(ResponseEntity.ok(students));
        when(request.getHeader("Authorization")).thenReturn("token");

        Long result = courseService.getStudentCount(1L, request);

        assertEquals(2L, result);
    }

    @Test
    void updateCourse_ShouldUpdateCourse() {
        CourseUpdateRequestDto request = new CourseUpdateRequestDto(1L, "New Name", "Desc", 150L);
        UsersResponseDto user = new UsersResponseDto(1L, "teacher@test.com", "", "", 30L, "TEACHER", BigDecimal.ZERO, "USD");
        Course course = new Course();
        course.setId(1L);
        course.setTeacherId(1L);
        CourseResponseDto dto = new CourseResponseDto(1L, "New Name", "Desc", 150L, null, 1L);

        when(userFeignClient.getProfileInfo("token")).thenReturn(ResponseEntity.ok(user));
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(courseRepository.save(course)).thenReturn(course);
        when(courseMapper.mapToCourseResponseDto(course)).thenReturn(dto);

        CourseResponseDto result = courseService.updateCourse(request, "token");

        assertEquals("New Name", course.getName());
        assertEquals(dto, result);
    }

    @Test
    void deleteCourse_ShouldDeleteCourse() {
        CourseDeleteRequestDto request = new CourseDeleteRequestDto(1L);
        UsersResponseDto user = new UsersResponseDto(1L, "teacher@test.com", "", "", 30L, "TEACHER", BigDecimal.ZERO, "USD");
        Course course = new Course();
        course.setId(1L);
        course.setTeacherId(1L);
        course.setLessons(new ArrayList<>());

        when(userFeignClient.getProfileInfo("token")).thenReturn(ResponseEntity.ok(user));
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));

        courseService.deleteCourse(request, "token");

        verify(courseRepository).deleteById(1L);
        verify(userFeignClient).removeCourseFromStudents(1L, "token");
    }
}
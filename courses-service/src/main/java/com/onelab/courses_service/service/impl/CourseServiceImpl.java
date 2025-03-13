package com.onelab.courses_service.service.impl;

import com.onelab.courses_service.entity.Course;
import com.onelab.courses_service.entity.Lesson;
import com.onelab.courses_service.mapper.CourseMapper;
import com.onelab.courses_service.repository.elastic.CourseSearchRepository;
import com.onelab.courses_service.repository.jpa.LessonRepository;
import com.onelab.courses_service.repository.jpa.CourseRepository;
import com.onelab.courses_service.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.onelab.common.dto.request.*;
import org.onelab.common.dto.response.CourseResponseDto;
import org.onelab.common.dto.response.LessonResponseDto;
import org.onelab.common.dto.response.UsersResponseDto;
import org.onelab.common.exception.BadRequestException;
import org.onelab.common.exception.ResourceNotFoundException;
import org.onelab.common.feign.UserFeignClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CourseServiceImpl implements CourseService {
    private final CourseRepository courseRepository;
    private final LessonRepository lessonRepository;
    private final CourseMapper courseMapper;
    private final CourseSearchRepository courseSearchRepository;
    private final UserFeignClient userFeignClient;

    @Override
    public List<CourseResponseDto> getAllCourses() {
        return courseRepository.findAll()
                .stream()
                .map(courseMapper::mapToCourseResponseDto)
                .toList();
    }

    @Override
    public List<LessonResponseDto> getLessonsForCourse(Long courseId) {
        Course course = getCourseById(courseId);
        return lessonRepository.findByCourse(course)
                .stream()
                .map(courseMapper::mapToLessonResponseDto)
                .toList();
    }

    @Override
    @Transactional
    public CourseResponseDto createCourse(CourseRequestDto requestDto, String token) {
        UsersResponseDto usersResponseDto = userFeignClient.getProfileInfo(token).getBody();
        Course course = new Course();
        course.setName(requestDto.name());
        course.setTeacherId(usersResponseDto.id());
        courseRepository.save(course);
        return courseMapper.mapToCourseResponseDto(course);
    }

    @Override
    @Transactional
    public LessonResponseDto addLessonToCourse(LessonRequestDto requestDto, String name, String token) {
        UsersResponseDto usersResponseDto = userFeignClient.getProfileInfo(token).getBody();
        Course course = getCourseById(requestDto.courseId());
        if(!usersResponseDto.id().equals(course.getTeacherId())) {
            throw BadRequestException.invalidTeacherException(usersResponseDto.id(), course.getId());
        }
        Lesson lesson = Lesson.builder()
                .title(requestDto.title())
                .course(course)
                .build();
        course.getLessons().add(lesson);
        return courseMapper.mapToLessonResponseDto(lessonRepository.save(lesson));
    }

    @Override
    @Transactional
    public CourseResponseDto updateCourse(CourseUpdateRequestDto requestDto, String token) {
        UsersResponseDto usersResponseDto = userFeignClient.getProfileInfo(token).getBody();
        Course course = getCourseById(requestDto.courseId());
        if(!usersResponseDto.id().equals(course.getTeacherId())) {
            throw BadRequestException.invalidTeacherException(usersResponseDto.id(), course.getId());
        }
        course.setName(requestDto.newName());
        return courseMapper.mapToCourseResponseDto(courseRepository.save(course));
    }

    @Override
    @Transactional
    public LessonResponseDto updateLesson(LessonUpdateRequestDto requestDto, String token) {
        UsersResponseDto usersResponseDto = userFeignClient.getProfileInfo(token).getBody();
        Lesson lesson = lessonRepository.findById(requestDto.lessonId())
                .orElseThrow(() -> ResourceNotFoundException.lessonNotFound(requestDto.lessonId()));
        Course course = lesson.getCourse();
        if(!usersResponseDto.id().equals(course.getTeacherId())) {
            throw BadRequestException.invalidTeacherException(usersResponseDto.id(), course.getId());
        }
        lesson.setTitle(requestDto.newTitle());
        return courseMapper.mapToLessonResponseDto(lessonRepository.save(lesson));
    }

    @Override
    @Transactional
    public void deleteCourse(CourseDeleteRequestDto requestDto, String token) {
        Course course = getCourseById(requestDto.courseId());
        UsersResponseDto usersResponseDto = userFeignClient.getProfileInfo(token).getBody();
        if(!usersResponseDto.id().equals(course.getTeacherId())) {
            throw BadRequestException.invalidTeacherException(usersResponseDto.id(), course.getId());
        }
        courseRepository.deleteById(requestDto.courseId());
    }

    @Override
    @Transactional
    public void deleteLesson(LessonDeleteRequestDto requestDto, String token) {
        Lesson lesson = getLessonById(requestDto.lessonId());
        Course course = lesson.getCourse();
        UsersResponseDto usersResponseDto = userFeignClient.getProfileInfo(token).getBody();
        if(!usersResponseDto.id().equals(course.getTeacherId())) {
            throw BadRequestException.invalidTeacherException(usersResponseDto.id(), course.getId());
        }
        lessonRepository.deleteById(requestDto.lessonId());
    }


    @Override
    @Transactional
    public CourseResponseDto setTeacher(AssignCourseDto requestDto) {
        Course course = getCourseById(requestDto.courseId());
        course.setTeacherId(requestDto.userId());
        return courseMapper.mapToCourseResponseDto(courseRepository.save(course));
    }

    @Override
    public CourseResponseDto getCourse(Long courseId) {
        return courseMapper.mapToCourseResponseDto(getCourseById(courseId));
    }

    @Override
    public List<CourseResponseDto> getAllCoursesByTeacher(Long teacherId) {
        return courseRepository.findAllByTeacherId(teacherId)
                .stream()
                .map(course -> new CourseResponseDto(course.getId(), course.getName(), course.getTeacherId()))
                .toList();
    }

    @Override
    public List<CourseResponseDto> findAllById(Set<Long> set) {
        return courseRepository.findAllById(set)
                .stream()
                .map(course -> new CourseResponseDto(course.getId(), course.getName(), course.getTeacherId()))
                .toList();
    }

    @Override
    public List<CourseResponseDto> searchCoursesByName(String name) {
        return courseSearchRepository.findByNameContainingIgnoreCase(name)
                .stream()
                .map(courseMapper::mapToCourseResponseDto)
                .toList();
    }

    private Course getCourseById(Long courseId) {
        return courseRepository.findById(courseId)
                .orElseThrow(() -> ResourceNotFoundException.courseNotFound(courseId));
    }

    private Lesson getLessonById(Long lessonId) {
        return lessonRepository.findById(lessonId).orElseThrow(
                () -> ResourceNotFoundException.lessonNotFound(lessonId)
        );
    }
}

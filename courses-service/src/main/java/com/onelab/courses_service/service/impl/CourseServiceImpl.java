package com.onelab.courses_service.service.impl;

import com.onelab.courses_service.entity.Course;
import com.onelab.courses_service.entity.Lesson;
import com.onelab.courses_service.entity.elastic.CourseIndex;
import com.onelab.courses_service.mapper.CourseMapper;
import com.onelab.courses_service.repository.elastic.CourseSearchRepository;
import com.onelab.courses_service.repository.jpa.CourseRepository;
import com.onelab.courses_service.repository.jpa.LessonRepository;
import com.onelab.courses_service.service.CourseService;
import jakarta.servlet.http.HttpServletRequest;
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
import java.util.stream.Collectors;

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
        course.setDescription(requestDto.description());
        course.setTeacherId(usersResponseDto.id());
        course.setPrice(requestDto.price());
        courseRepository.save(course);
        reindexCourses();
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
    public List<CourseResponseDto> searchCourses(String query, Long minPrice, Long maxPrice, int page, int size) {
//        Pageable pageRequest = PageRequest.of(page, size);
        if(query== null || query.isBlank()) {
            return getAllCourses()
                    .stream()
                    .filter(courseResponseDto ->
                            courseResponseDto.price() >= (minPrice != null ? minPrice : 0) &&
                                    (maxPrice == null || courseResponseDto.price() <= maxPrice)
                    )
                    .collect(Collectors.toList());
        }
//        Page<CourseIndex> courseIndexPage = courseSearchRepository.findByNameContainingIgnoreCase(query, pageRequest);
//        Set<Long> idsSet = courseIndexPage.stream()
//                .map(courseIndex -> courseIndex.getId())
//                .collect(Collectors.toSet());
        List<CourseIndex> courseIndexList = courseSearchRepository.findByNameContainingIgnoreCase(query);
        Set<Long> idsSet = courseIndexList.stream()
                .map(courseIndex -> courseIndex.getId())
                .collect(Collectors.toSet());
        List<CourseResponseDto> courseResponseDtos = courseRepository.findAllById(idsSet)
                .stream()
                .map(course -> courseMapper.mapToCourseResponseDto(course))
                .filter(courseResponseDto ->
                        courseResponseDto.price() >= (minPrice != null ? minPrice : 0) &&
                                (maxPrice == null || courseResponseDto.price() <= maxPrice)
                )
                .collect(Collectors.toList());

        return courseResponseDtos;
    }

    @Override
    public Long getStudentCount(Long courseId, HttpServletRequest httpServletRequest) {
        List<UsersResponseDto> students = userFeignClient.getStudentsForCourse(courseId, httpServletRequest.getHeader("Authorization")).getBody();
        return students.stream().map(user -> 1L).reduce(0L, Long::sum);
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
        course.setDescription(requestDto.description());
        course.setPrice(requestDto.price());
        course = courseRepository.save(course);
        reindexCourses();
        return courseMapper.mapToCourseResponseDto(course);
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
        reindexCourses();
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
    public CourseResponseDto getCourse(Long courseId) {
        return courseMapper.mapToCourseResponseDto(getCourseById(courseId));
    }

    @Override
    public List<CourseResponseDto> findAllById(Set<Long> set) {
        return courseRepository.findAllById(set)
                .stream()
                .map(course -> courseMapper.mapToCourseResponseDto(course))
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

    private void reindexCourses() {
        courseSearchRepository.saveAll(
                courseRepository.findAll().stream().map(course -> courseMapper.toCourseIndex(course)).collect(Collectors.toList())
        );
    }
}

package com.onelab.courses_service.service;


import org.onelab.common.dto.request.*;
import org.onelab.common.dto.response.CourseResponseDto;
import org.onelab.common.dto.response.LessonResponseDto;

import java.util.List;
import java.util.Set;

public interface CourseService {
    List<CourseResponseDto> getAllCourses();

    List<LessonResponseDto> getLessonsForCourse(Long courseId);

    CourseResponseDto createCourse(CourseRequestDto requestDto, String token);
    CourseResponseDto updateCourse(CourseUpdateRequestDto requestDto, String token);

    LessonResponseDto updateLesson(LessonUpdateRequestDto requestDto, String token);

    void deleteCourse(CourseDeleteRequestDto requestDto, String token);

    void deleteLesson(LessonDeleteRequestDto requestDto, String token);


    CourseResponseDto getCourse(Long courseId);


    List<CourseResponseDto> findAllById(Set<Long> set);

    LessonResponseDto addLessonToCourse(LessonRequestDto lessonRequestDto, String name, String authorization);

    List<CourseResponseDto> searchCourses(String query, Long minPrice, Long maxPrice, int page, int size);
}


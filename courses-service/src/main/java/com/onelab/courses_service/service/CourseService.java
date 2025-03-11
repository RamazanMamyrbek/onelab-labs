package com.onelab.courses_service.service;


import org.onelab.common.dto.request.*;
import org.onelab.common.dto.response.CourseResponseDto;
import org.onelab.common.dto.response.LessonResponseDto;

import java.util.List;
import java.util.Set;

public interface CourseService {
    List<CourseResponseDto> getAllCourses();

    List<LessonResponseDto> getLessonsForCourse(Long courseId);

    CourseResponseDto createCourse(CourseRequestDto requestDto);

    LessonResponseDto addLessonToCourse(LessonRequestDto requestDto);

    CourseResponseDto updateCourse(CourseUpdateRequestDto requestDto);

    LessonResponseDto updateLesson(LessonUpdateRequestDto requestDto);

    void deleteCourse(CourseDeleteRequestDto requestDto);

    void deleteLesson(LessonDeleteRequestDto requestDto);

    CourseResponseDto setTeacher(AssignCourseDto requestDto);

    CourseResponseDto getCourse(Long courseId);

    List<CourseResponseDto> getAllCoursesByTeacher(Long teacherId);

    List<CourseResponseDto> findAllById(Set<Long> set);

    List<CourseResponseDto> searchCoursesByName(String name);

}


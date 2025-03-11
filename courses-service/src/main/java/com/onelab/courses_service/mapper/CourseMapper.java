package com.onelab.courses_service.mapper;

import com.onelab.courses_service.entity.Course;
import com.onelab.courses_service.entity.Lesson;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.onelab.common.dto.response.CourseResponseDto;
import org.onelab.common.dto.response.LessonResponseDto;

@Mapper(componentModel = "spring")
public interface CourseMapper {

    CourseResponseDto mapToCourseResponseDto(Course course);

    @Mapping(target = "courseId", source = "course", qualifiedByName = "mapCourseToId")
    LessonResponseDto mapToLessonResponseDto(Lesson lesson);

    @Named("mapCourseToId")
    default Long mapCourseToId(Course course) {
        return course != null ? course.getId() : null;
    }
}

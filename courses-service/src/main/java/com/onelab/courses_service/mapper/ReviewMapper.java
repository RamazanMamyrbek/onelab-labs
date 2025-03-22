package com.onelab.courses_service.mapper;

import com.onelab.courses_service.entity.Course;
import com.onelab.courses_service.entity.Review;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.onelab.common.dto.response.ReviewResponseDto;

@Mapper(componentModel = "spring")
public interface ReviewMapper {

    @Mapping(target = "courseId", source = "course", qualifiedByName = "mapCourseToId")
    ReviewResponseDto toReviewResponseDto(Review review);

    @Named("mapCourseToId")
    default Long mapCourseToId(Course course) {
        return course != null ? course.getId() : null;
    }
}

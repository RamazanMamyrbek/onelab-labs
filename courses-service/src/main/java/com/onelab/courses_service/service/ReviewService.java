package com.onelab.courses_service.service;

import org.onelab.common.dto.request.ReviewCreateRequestDto;
import org.onelab.common.dto.response.OverallRatingResponseDto;
import org.onelab.common.dto.response.ReviewResponseDto;

import java.util.List;

public interface ReviewService {
    List<ReviewResponseDto> getReviewsForCourse(Long courseId);

    ReviewResponseDto createReview(Long courseId, ReviewCreateRequestDto reviewCreateRequestDto, String email, String token);

    void deleteReview(Long courseId, Long reviewId, String token);

    OverallRatingResponseDto getOverallRating(Long courseId);

    void approveReview(Long aLong);

    void declineReview(Long aLong);
}

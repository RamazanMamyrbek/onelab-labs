package com.onelab.courses_service.service.impl;

import com.onelab.courses_service.entity.Course;
import com.onelab.courses_service.entity.Review;
import com.onelab.courses_service.mapper.ReviewMapper;
import com.onelab.courses_service.producer.NotificationProducer;
import com.onelab.courses_service.repository.jpa.ReviewRepository;
import com.onelab.courses_service.service.CourseService;
import com.onelab.courses_service.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.onelab.common.dto.request.NotificationDto;
import org.onelab.common.dto.request.ReviewCreateRequestDto;
import org.onelab.common.dto.response.OverallRatingResponseDto;
import org.onelab.common.dto.response.ReviewResponseDto;
import org.onelab.common.dto.response.UsersResponseDto;
import org.onelab.common.exception.BadRequestException;
import org.onelab.common.exception.ResourceNotFoundException;
import org.onelab.common.feign.UserFeignClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewServiceImpl implements ReviewService {
    private final CourseService courseService;
    private final ReviewMapper reviewMapper;
    private final ReviewRepository reviewRepository;
    private final UserFeignClient userFeignClient;
    private final NotificationProducer notificationProducer;

    @Override
    public List<ReviewResponseDto> getReviewsForCourse(Long courseId) {
        return reviewRepository.findAllByCourse_Id(courseId)
                .stream()
                .map(reviewMapper::toReviewResponseDto)
                .toList();
    }

    @Override
    @Transactional
    public ReviewResponseDto createReview(Long courseId, ReviewCreateRequestDto reviewCreateRequestDto, String email, String token) {
        Course course = courseService.getCourseById(courseId);
        UsersResponseDto user = userFeignClient.getProfileInfo(token).getBody();
        if(!userFeignClient.studentHasCourse(courseId, token)) {
            throw BadRequestException.accessDeniedForCourse(user.id(),courseId);
        }
        if(reviewRepository.existsByCourseAndUserId(course, user.id())) {
            throw BadRequestException.reviewExistsByCourseAndUser(courseId, user.id());
        }
        Review review = Review
                .builder()
                .text(reviewCreateRequestDto.text())
                .rating(reviewCreateRequestDto.rating())
                .course(course)
                .userId(user.id())
                .build();
        review = reviewRepository.save(review);
        notificationProducer.sendNotification(
                new NotificationDto(course.getTeacherId(), "User with id %s left a review for your course '%s': '%s'"
                        .formatted(review.getUserId(), course.getName(), review.getText()))
        );
        return reviewMapper.toReviewResponseDto(review);
    }

    @Override
    @Transactional
    public void deleteReview(Long courseId, Long reviewId, String token) {
        UsersResponseDto user = userFeignClient.getProfileInfo(token).getBody();
        Review review = getReviewById(reviewId);
        if(!review.getUserId().equals(user.id())) {
            throw BadRequestException.accessDeniedForReview(user.id(),reviewId);
        }
        reviewRepository.delete(review);
    }

    @Override
    public OverallRatingResponseDto getOverallRating(Long courseId) {
        courseService.getCourseById(courseId);
        List<Review> reviews = reviewRepository.findAllByCourse_Id(courseId);
        double overallRating = reviews.stream()
                .mapToDouble(Review::getRating)
                .average()
                .orElse(0.0);
        return new OverallRatingResponseDto(courseId, overallRating);
    }

    private Review getReviewById(Long reviewId) {
        return reviewRepository.findById(reviewId).orElseThrow(
                () -> ResourceNotFoundException.reviewNotFound(reviewId)
        );
    }


}

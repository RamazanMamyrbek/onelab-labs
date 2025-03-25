package com.onelab.student_service.unit.service;

import com.onelab.courses_service.entity.Course;
import com.onelab.courses_service.entity.Review;
import com.onelab.courses_service.mapper.ReviewMapper;
import com.onelab.courses_service.producer.NotificationProducer;
import com.onelab.courses_service.repository.jpa.ReviewRepository;
import com.onelab.courses_service.service.CourseService;
import com.onelab.courses_service.service.impl.ReviewServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.onelab.common.dto.request.NotificationDto;
import org.onelab.common.dto.request.ReviewCreateRequestDto;
import org.onelab.common.dto.response.OverallRatingResponseDto;
import org.onelab.common.dto.response.ReviewCheckDto;
import org.onelab.common.dto.response.ReviewResponseDto;
import org.onelab.common.dto.response.UsersResponseDto;
import org.onelab.common.enums.ReviewStatus;
import org.onelab.common.feign.UserFeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceImplTest {

    @Mock private CourseService courseService;
    @Mock private ReviewMapper reviewMapper;
    @Mock private ReviewRepository reviewRepository;
    @Mock private UserFeignClient userFeignClient;
    @Mock private NotificationProducer notificationProducer;
    @Mock private KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks
    private ReviewServiceImpl reviewService;

    @Test
    void getReviewsForCourse_ShouldReturnReviews() {
        Review review = Review.builder()
                .id(1L)
                .rating(5L)
                .text("Great course")
                .build();

        ReviewResponseDto dto = new ReviewResponseDto(
                1L, "Great course", 5L, 1L, 1L,
                LocalDateTime.now(), ReviewStatus.APPROVED);

        when(reviewRepository.findAllByCourse_Id(1L)).thenReturn(List.of(review));
        when(reviewMapper.toReviewResponseDto(review)).thenReturn(dto);

        List<ReviewResponseDto> result = reviewService.getReviewsForCourse(1L);

        assertEquals(1, result.size());
        assertEquals(dto, result.get(0));
    }

    @Test
    @Transactional
    void createReview_ShouldCreateNewReview() {
        Course course = new Course();
        course.setId(1L);
        course.setName("Test Course");
        course.setTeacherId(2L);

        UsersResponseDto user = new UsersResponseDto(
                1L, "user@test.com", "User", "Country",
                25L, "STUDENT", BigDecimal.ZERO, "USD");

        ReviewCreateRequestDto request = new ReviewCreateRequestDto(
                1L, "Good course", 4L);

        Review savedReview = Review.builder()
                .id(1L)
                .text("Good course")
                .rating(4L)
                .course(course)
                .userId(1L)
                .reviewStatus(ReviewStatus.PENDING)
                .build();

        ReviewResponseDto responseDto = new ReviewResponseDto(
                1L, "Good course", 4L, 1L, 1L,
                LocalDateTime.now(), ReviewStatus.PENDING);

        when(courseService.getCourseById(1L)).thenReturn(course);
        when(userFeignClient.getProfileInfo("token")).thenReturn(ResponseEntity.ok(user));
        when(userFeignClient.studentHasCourse(1L, "token")).thenReturn(true);
        when(reviewRepository.existsByCourseAndUserId(course, 1L)).thenReturn(false);
        when(reviewRepository.save(any(Review.class))).thenReturn(savedReview);
        when(reviewMapper.toReviewResponseDto(savedReview)).thenReturn(responseDto);

        ReviewResponseDto result = reviewService.createReview(
                1L, request, "user@test.com", "token");

        assertEquals(responseDto, result);
        verify(kafkaTemplate).send(eq("review.created"), any(ReviewCheckDto.class));
        verify(notificationProducer).sendNotification(any(NotificationDto.class));
    }

    @Test
    @Transactional
    void deleteReview_ShouldDeleteReview() {
        Review review = Review.builder()
                .id(1L)
                .userId(1L)
                .build();

        UsersResponseDto user = new UsersResponseDto(
                1L, "user@test.com", "User", "Country",
                25L, "STUDENT", BigDecimal.ZERO, "USD");

        when(userFeignClient.getProfileInfo("token")).thenReturn(ResponseEntity.ok(user));
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));

        reviewService.deleteReview(1L, 1L, "token");

        verify(reviewRepository).delete(review);
    }

    @Test
    void getOverallRating_ShouldCalculateAverage() {
        Review review1 = Review.builder().rating(4L).build();
        Review review2 = Review.builder().rating(5L).build();

        when(reviewRepository.findAllByCourse_Id(1L)).thenReturn(List.of(review1, review2));

        OverallRatingResponseDto result = reviewService.getOverallRating(1L);

        assertEquals(4.5, result.overallRating());
        assertEquals(1L, result.courseId());
    }

    @Test
    @Transactional
    void approveReview_ShouldUpdateStatus() {
        Review review = Review.builder()
                .id(1L)
                .reviewStatus(ReviewStatus.PENDING)
                .build();

        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));

        reviewService.approveReview(1L);

        assertEquals(ReviewStatus.APPROVED, review.getReviewStatus());
        verify(reviewRepository).save(review);
    }

    @Test
    @Transactional
    void declineReview_ShouldUpdateStatus() {
        Review review = Review.builder()
                .id(1L)
                .reviewStatus(ReviewStatus.PENDING)
                .build();

        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));

        reviewService.declineReview(1L);

        assertEquals(ReviewStatus.DECLINED, review.getReviewStatus());
        verify(reviewRepository).save(review);
    }
}
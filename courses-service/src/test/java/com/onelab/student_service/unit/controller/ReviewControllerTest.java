package com.onelab.student_service.unit.controller;

import com.onelab.courses_service.controller.ReviewController;
import com.onelab.courses_service.service.ReviewService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.onelab.common.dto.request.ReviewCreateRequestDto;
import org.onelab.common.dto.response.OverallRatingResponseDto;
import org.onelab.common.dto.response.ReviewResponseDto;
import org.onelab.common.enums.ReviewStatus;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewControllerTest {

    @Mock
    private ReviewService reviewService;

    @Mock
    private Principal principal;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private ReviewController reviewController;

    @Test
    void getReviewsForCourse_ShouldReturnReviews() {
        ReviewResponseDto review = new ReviewResponseDto(
                1L, "Great course", 5L, 1L, 1L,
                LocalDateTime.now(), ReviewStatus.APPROVED
        );
        when(reviewService.getReviewsForCourse(1L)).thenReturn(List.of(review));

        ResponseEntity<List<ReviewResponseDto>> response =
                reviewController.getReviewsForCourse(1L);

        assertEquals(1, response.getBody().size());
        assertEquals(review, response.getBody().get(0));
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void createReview_ShouldCreateAndReturnReview() {
        ReviewCreateRequestDto requestDto =
                new ReviewCreateRequestDto(1L, "Good course", 4L);
        ReviewResponseDto responseDto =
                new ReviewResponseDto(1L, "Good course", 4L, 1L, 1L,
                        LocalDateTime.now(), ReviewStatus.APPROVED);

        when(principal.getName()).thenReturn("user@test.com");
        when(request.getHeader("Authorization")).thenReturn("token");
        when(reviewService.createReview(1L, requestDto, "user@test.com", "token"))
                .thenReturn(responseDto);

        ResponseEntity<ReviewResponseDto> response =
                reviewController.createReview(1L, requestDto, principal, request);

        assertEquals(responseDto, response.getBody());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void deleteReview_ShouldDeleteReview() {
        when(request.getHeader("Authorization")).thenReturn("token");

        ResponseEntity<Void> response =
                reviewController.deleteReview(1L, 1L, request);

        verify(reviewService).deleteReview(1L, 1L, "token");
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void getOverallRating_ShouldReturnRating() {
        OverallRatingResponseDto ratingDto =
                new OverallRatingResponseDto(1L, 4.5);
        when(reviewService.getOverallRating(1L)).thenReturn(ratingDto);

        ResponseEntity<OverallRatingResponseDto> response =
                reviewController.getOverallRating(1L);

        assertEquals(ratingDto, response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}
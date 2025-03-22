package com.onelab.courses_service.controller;

import com.onelab.courses_service.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.onelab.common.dto.request.ReviewCreateRequestDto;
import org.onelab.common.dto.response.OverallRatingResponseDto;
import org.onelab.common.dto.response.ReviewResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/courses/{courseId}")
@Tag(name = "ReviewController", description = "Endpoints for reviews management")
@SecurityRequirement(name = "JWT")
public class ReviewController {
    private final ReviewService reviewService;

    @GetMapping("/reviews")
    @Operation(summary = "Get all reviews for the course")
    public ResponseEntity<List<ReviewResponseDto>> getReviewsForCourse(@PathVariable Long courseId) {
        List<ReviewResponseDto> responseDtoList = reviewService.getReviewsForCourse(courseId);
        return ResponseEntity.ok(responseDtoList);
    }

    @PostMapping("/reviews")
    @Operation(summary = "Create a review for the course")
    public ResponseEntity<ReviewResponseDto> createReview(@PathVariable Long courseId,
                                                          ReviewCreateRequestDto reviewCreateRequestDto,
                                                          Principal principal,
                                                          HttpServletRequest request) {
        ReviewResponseDto responseDto = reviewService.createReview(courseId, reviewCreateRequestDto, principal.getName(), request.getHeader("Authorization"));
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @DeleteMapping("/reviews/{reviewId}")
    @Operation(summary = "Delete a review")
    public ResponseEntity<Void> deleteReview(@PathVariable Long courseId,
                                             @PathVariable Long reviewId,
                                             HttpServletRequest request) {
        reviewService.deleteReview(courseId, reviewId, request.getHeader("Authorization"));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/reviews/overall")
    @Operation(summary = "Get overall rating for course")
    public ResponseEntity<OverallRatingResponseDto> getOverallRating(@PathVariable Long courseId) {
        OverallRatingResponseDto responseDto = reviewService.getOverallRating(courseId);
        return ResponseEntity.ok(responseDto);
    }

}

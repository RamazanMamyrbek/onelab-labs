package com.onelab.courses_service.consumer;

import com.onelab.courses_service.service.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.onelab.common.dto.request.ReviewDecisionDto;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaConsumer {
    private final ReviewService reviewService;

    @KafkaListener(topics = {"review.decision"})
    public void consumeReviewDecision(ReviewDecisionDto reviewDecisionDto) {
        if(reviewDecisionDto.isApproved()) {
            log.info("Review with id {} is approved".formatted(reviewDecisionDto.reviewId()));
            reviewService.approveReview(reviewDecisionDto.reviewId());
        } else {
            log.info("Review with id {} is declined".formatted(reviewDecisionDto.reviewId()));
            reviewService.declineReview(reviewDecisionDto.reviewId());
        }
    }
}

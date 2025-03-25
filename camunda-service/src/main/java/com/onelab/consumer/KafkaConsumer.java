package com.onelab.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.RuntimeService;
import org.onelab.common.dto.response.ReviewCheckDto;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaConsumer {
    private final RuntimeService runtimeService;

    @KafkaListener(topics = "review.created")
    public void consumeReviewCreated(ReviewCheckDto reviewCheckDto) {
        log.info("Starting Camunda process for review: {}", reviewCheckDto);

        Map<String, Object> variables = new HashMap<>();
        variables.put("reviewId", reviewCheckDto.reviewId());
        variables.put("text", reviewCheckDto.text());
        variables.put("userId", reviewCheckDto.userId());
        runtimeService.startProcessInstanceByKey("camunda-service-process", variables);
    }
}

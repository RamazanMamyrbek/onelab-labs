package org.onelab.reviewcheckerservice.worker;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.client.spring.annotation.ExternalTaskSubscription;
import org.camunda.bpm.client.task.ExternalTask;
import org.camunda.bpm.client.task.ExternalTaskHandler;
import org.camunda.bpm.client.task.ExternalTaskService;
import org.onelab.common.dto.request.ReviewDecisionDto;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@ExternalTaskSubscription("DecisionMaking")
@RequiredArgsConstructor
public class DecisionMakingWorker implements ExternalTaskHandler {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    @Override
    public void execute(ExternalTask externalTask, ExternalTaskService externalTaskService) {
        String decision = externalTask.getVariable("decision");
        Long reviewId = externalTask.getVariable("reviewId");
        if (decision.equals("Approve")) {
            kafkaTemplate.send("review.decision", new ReviewDecisionDto(
                    reviewId,
                    true
            ));
        } else {
            kafkaTemplate.send("review.decision", new ReviewDecisionDto(
                    reviewId,
                    false
            ));
        }
        externalTaskService.complete(externalTask);
    }
}

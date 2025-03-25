package org.onelab.reviewcheckerservice.worker;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.client.spring.annotation.ExternalTaskSubscription;
import org.camunda.bpm.client.task.ExternalTask;
import org.camunda.bpm.client.task.ExternalTaskHandler;
import org.camunda.bpm.client.task.ExternalTaskService;
import org.onelab.reviewcheckerservice.service.ForbiddenWordService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@ExternalTaskSubscription(
        topicName = "ForbiddenWordsCheck",
        processDefinitionKey = "camunda-service-process",
        includeExtensionProperties = true,
        variableNames = {"text", "forbidden"})
public class ForbiddenWordsCheckWorker implements ExternalTaskHandler {
    private final ForbiddenWordService forbiddenWordService;
    @Override
    public void execute(ExternalTask externalTask, ExternalTaskService externalTaskService) {
        String text = externalTask.getVariable("text");
        boolean isForbidden = forbiddenWordService.isForbidden(text);

        Map<String, Object> variables = new HashMap<>();
        variables.put("forbidden", isForbidden);
        externalTaskService.setVariables(externalTask, externalTask.getAllVariables());
        externalTaskService.complete(externalTask, variables);
    }
}

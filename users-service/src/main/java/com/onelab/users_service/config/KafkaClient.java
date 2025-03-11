package com.onelab.users_service.config;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Component
@RequiredArgsConstructor
public class KafkaClient {
    private final ReplyingKafkaTemplate<String, Object, Object> replyingKafkaTemplate;

    public <T> T sendAndReceive(String topic, Object request, Class<T> responseType) {
        ProducerRecord<String, Object> record = new ProducerRecord<>(topic, request);
        RequestReplyFuture<String, Object, Object> future = replyingKafkaTemplate.sendAndReceive(record);

        try {
            ConsumerRecord<String, Object> response = future.get(10, TimeUnit.SECONDS); // Ждем максимум 10 секунд
            return responseType.cast(response.value());
        } catch (TimeoutException e) {
            throw new RuntimeException("Kafka request timed out after 10 seconds", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Kafka request was interrupted", e);
        } catch (ExecutionException e) {
            throw new RuntimeException("Error while sending Kafka request", e);
        }
    }
}

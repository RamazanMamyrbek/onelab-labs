package com.onelab.users_service.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {
    @Bean
    public NewTopic getAllStudentsResponseTopic() {
        return TopicBuilder.name("users-service.responses-topic").build();
    }


    @Bean
    public NewTopic getAllStudentsTopic() {
        return TopicBuilder.name("user.request.getAllStudents").build();
    }

    @Bean
    public NewTopic getAllTeachersTopic() {
        return TopicBuilder.name("user.request.getAllTeachers").build();
    }

    @Bean
    public NewTopic createStudentTopic() {
        return TopicBuilder.name("user.request.createStudent").build();
    }

    @Bean
    public NewTopic createTeacherTopic() {
        return TopicBuilder.name("user.request.createTeacher").build();
    }

    @Bean
    public NewTopic assignCourseToTeacherTopic() {
        return TopicBuilder.name("user.request.assignCourseToTeacher").build();
    }

    @Bean
    public NewTopic assignCourseToStudentTopic() {
        return TopicBuilder.name("user.request.assignCourseToStudent").build();
    }

    @Bean
    public NewTopic deleteTeacherTopic() {
        return TopicBuilder.name("user.request.deleteTeacher").build();
    }

    @Bean
    public NewTopic deleteStudentTopic() {
        return TopicBuilder.name("user.request.deleteStudent").build();
    }
}

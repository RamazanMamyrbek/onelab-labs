package com.onelab.courses_service.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic newTopic() {
        return TopicBuilder
                .name("courses-response-topic")
                .build();
    }

    @Bean
    public NewTopic coursesResponseTopic() {
        return TopicBuilder
                .name("course-service.responses-topic")
                .build();
    }
    @Bean
    public NewTopic getAllCoursesTopic() {
        return TopicBuilder.name("course.request.getAllCourses").build();
    }

    @Bean
    public NewTopic getLessonsForCourseTopic() {
        return TopicBuilder.name("course.request.getLessonsForCourse").build();
    }

    @Bean
    public NewTopic setTeacher() {
        return TopicBuilder.name("course.request.setTeacher").build();
    }

    @Bean
    public NewTopic seCourse() {
        return TopicBuilder.name("course.request.getCourse").build();
    }

    @Bean
    public NewTopic setCoursesByTeacher() {
        return TopicBuilder.name("course.request.getCoursesByTeacher").build();
    }

    @Bean
    public NewTopic createCourseTopic() {
        return TopicBuilder.name("course.request.createCourse").build();
    }

    @Bean
    public NewTopic addLessonToCourseTopic() {
        return TopicBuilder.name("course.request.addLessonToCourse").build();
    }

    @Bean
    public NewTopic updateCourseTopic() {
        return TopicBuilder.name("course.request.updateCourse").build();
    }

    @Bean
    public NewTopic updateLessonTopic() {
        return TopicBuilder.name("course.request.updateLesson").build();
    }

    @Bean
    public NewTopic deleteCourseTopic() {
        return TopicBuilder.name("course.request.deleteCourse").build();
    }

    @Bean
    public NewTopic deleteLessonTopic() {
        return TopicBuilder.name("course.request.deleteLesson").build();
    }

    @Bean
    public NewTopic findAllByIdTopic() {
        return TopicBuilder.name("course.request.findAllById").build();
    }

}

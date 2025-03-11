package com.onelab.console_service.service;

import com.onelab.console_service.config.KafkaClient;
import lombok.extern.slf4j.Slf4j;
import org.onelab.common.dto.request.*;
import org.onelab.common.dto.response.*;

import java.util.Scanner;

@Slf4j
public class CourseService {

    public static void viewAllCourses(Scanner scanner, KafkaClient kafkaClient) {
        FindAllCoursesWrapper responseFindAllCoursesWrapper = kafkaClient.sendAndReceive(
                "course.request.getAllCourses",
                null,
                FindAllCoursesWrapper.class
        );
        log.info("Courses list: ");
        responseFindAllCoursesWrapper.list().
                forEach(response -> log.info("Id: {}, Name: {}, Teacher id: {}", response.id(), response.name(), response.teacherId()));
        log.info("\n");
    }

    public static void addCourse(Scanner scanner, KafkaClient kafkaClient) {
        log.info("Enter a course name: ");
        String name = scanner.nextLine();
        Object response = kafkaClient.sendAndReceive(
          "course.request.createCourse",
          new CourseRequestDto(name),
          Object.class
        );
        if(response instanceof ErrorResponse) {
            log.error(((ErrorResponse) response).message());
        } else if(response instanceof CourseResponseDto){
            log.info("Course was created successfully: ");
            log.info("Id: {}, Name: {}", ((CourseResponseDto) response).id(),((CourseResponseDto) response).name());
        }
        log.info("");
    }

    public static void getLessonsForCourse(Scanner scanner, KafkaClient kafkaClient) {
        log.info("Enter a course id: ");
        Long courseId = scanner.nextLong();
        scanner.nextLine();
        Object response = kafkaClient.sendAndReceive(
                "course.request.getLessonsForCourse",
                courseId,
                Object.class
        );
        if(response instanceof ErrorResponse) {
            log.error(((ErrorResponse) response).message());
        } else if(response instanceof FindAllLessonsWrapper){
            log.info("Lessons list: ");
            ((FindAllLessonsWrapper) response).list()
                    .forEach(lesson -> log.info("Lesson ID: {}, Lesson title: {}", lesson.id(), lesson.title()));
        }
        log.info("");
    }

    public static void addLessonToCourse(Scanner scanner, KafkaClient kafkaClient) {
        log.info("Enter a course id: ");
        Long courseId = scanner.nextLong();
        scanner.nextLine();
        log.info("Enter a lesson title: ");
        String title = scanner.nextLine();
        Object response = kafkaClient.sendAndReceive(
                "course.request.addLessonToCourse",
                new LessonRequestDto(title, courseId),
                Object.class
        );
        if(response instanceof ErrorResponse) {
            log.error(((ErrorResponse) response).message());
        } else if(response instanceof LessonResponseDto){
            log.info("Lesson was added successfully");
        }
        log.info("");
    }

    public static void updateCourse(Scanner scanner, KafkaClient kafkaClient) {
        log.info("Enter a course id: ");
        Long courseId = scanner.nextLong();
        scanner.nextLine();
        log.info("Enter a new course name: ");
        String newName = scanner.nextLine();
        Object response = kafkaClient.sendAndReceive(
                "course.request.updateCourse",
                new CourseUpdateRequestDto(courseId, newName),
                Object.class
        );
        if(response instanceof ErrorResponse) {
            log.error(((ErrorResponse) response).message());
        } else if(response instanceof CourseResponseDto){
            log.info("Course was updated successfully");
        }
        log.info("");
    }

    public static void updateLesson(Scanner scanner, KafkaClient kafkaClient) {
        log.info("Enter a lesson id: ");
        Long lessonId = scanner.nextLong();
        scanner.nextLine();
        log.info("Enter a new lesson title: ");
        String newTitle = scanner.nextLine();
        Object response = kafkaClient.sendAndReceive(
                "course.request.updateLesson",
                new LessonUpdateRequestDto(lessonId, newTitle),
                Object.class
        );
        if(response instanceof ErrorResponse) {
            log.error(((ErrorResponse) response).message());
        } else if(response instanceof LessonResponseDto){
            log.info("Lesson was updated successfully");
        }
        log.info("");
    }

    public static void deleteCourse(Scanner scanner, KafkaClient kafkaClient) {
        log.info("Enter a course id: ");
        Long courseId = scanner.nextLong();
        Object response = kafkaClient.sendAndReceive(
                "course.request.deleteCourse",
                new CourseDeleteRequestDto(courseId),
                Object.class
        );
        if(response instanceof ErrorResponse) {
            log.error(((ErrorResponse) response).message());
        } else if(response instanceof String){
            log.info("Course was deleted successfully");
        }
        log.info("");
    }

    public static void deleteLesson(Scanner scanner, KafkaClient kafkaClient) {
        log.info("Enter a lesson id: ");
        Long lessonId = scanner.nextLong();
        Object response = kafkaClient.sendAndReceive(
                "course.request.deleteLesson",
                new LessonDeleteRequestDto(lessonId),
                Object.class
        );
        if(response instanceof ErrorResponse) {
            log.error(((ErrorResponse) response).message());
        } else if(response instanceof String){
            log.info("Lesson was deleted successfully");
        }
        log.info("");
    }
}

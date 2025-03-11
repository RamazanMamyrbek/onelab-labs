package com.onelab.console_service.service;

import com.onelab.console_service.config.KafkaClient;
import lombok.extern.slf4j.Slf4j;
import org.onelab.common.dto.request.AssignCourseDto;
import org.onelab.common.dto.request.DeleteUserDto;
import org.onelab.common.dto.request.UserRequestDto;
import org.onelab.common.dto.response.*;

import java.util.Scanner;

@Slf4j
public class TeacherService {
    public static void viewAllTeachers(Scanner scanner, KafkaClient kafkaClient) {
        Object response = kafkaClient.sendAndReceive(
                "user.request.getAllTeachers",
                null,
                Object.class
        );
        if(response instanceof ErrorResponse) {
            log.error(((ErrorResponse) response).message());
        } else if(response instanceof FindAllUsersWrapper) {
            log.info("Teachers list");
            ((FindAllUsersWrapper) response).list().forEach(
                    teacher -> log.info("Id: {}, Name: {}, Role: {}", teacher.id(), teacher.name(), teacher.role())
            );
        }
        log.info("");
    }

    public static void addTeacher(Scanner scanner, KafkaClient kafkaClient) {
        log.info("Enter a name: ");
        String name = scanner.nextLine();
        Object response = kafkaClient.sendAndReceive(
                "user.request.createTeacher",
                new UserRequestDto(name),
                Object.class
        );
        if(response instanceof ErrorResponse) {
            log.error(((ErrorResponse) response).message());
        } else if(response instanceof UsersResponseDto) {
            log.info("Teacher was created successfully");
            log.info("Id: {}, Name: {}, Role: {}",((UsersResponseDto) response).id(), ((UsersResponseDto) response).name(),((UsersResponseDto) response).role() );
        }
        log.info("");
    }

    public static void removeTeacher(Scanner scanner, KafkaClient kafkaClient) {
        log.info("Enter a teacher id: ");
        Long id = scanner.nextLong();
        Object response = kafkaClient.sendAndReceive(
                "user.request.deleteTeacher",
                new DeleteUserDto(id),
                Object.class
        );
        if(response instanceof ErrorResponse) {
            log.error(((ErrorResponse) response).message());
        } else if(response instanceof String) {
            log.info("Teacher was deleted successfully");
        }
        log.info("");
    }

    public static void assignCourseToTeacher(Scanner scanner, KafkaClient kafkaClient) {
        log.info("Enter a teacher id: ");
        Long teacherId = scanner.nextLong();
        log.info("Enter a course id: ");
        Long courseId = scanner.nextLong();
        Object response = kafkaClient.sendAndReceive(
                "user.request.assignCourseToTeacher",
                new AssignCourseDto(teacherId, courseId),
                Object.class
        );
        if(response instanceof ErrorResponse) {
            log.error(((ErrorResponse) response).message());
        } else if(response instanceof String) {
            log.info(((String) response));
        }
        log.info("");
    }

    public static void viewTeacherNotifications(Scanner scanner, KafkaClient kafkaClient) {
        log.info("Enter a teacher id: ");
        Long teacherId = scanner.nextLong();
        Object response = kafkaClient.sendAndReceive(
                "notification.request.getNotifications",
                teacherId,
                Object.class
        );
        if(response instanceof ErrorResponse) {
            log.error(((ErrorResponse) response).message());
        } else if(response instanceof FindAllNotificationsWrapper) {
            log.info("Notifications: \n");
            ((FindAllNotificationsWrapper) response).list().forEach(
                    notification -> log.info("User id: {}, \nMessage: {}, \nDateTime: {}\n", notification.userId(), notification.message(), notification.dateTime())
            );
            log.info("");
        }
    }

    public static void viewTeacherCourses(Scanner scanner, KafkaClient kafkaClient) {
        log.info("Enter a teacher id: ");
        Long teacherId = scanner.nextLong();
        Object response = kafkaClient.sendAndReceive(
                "course.request.getCoursesByTeacher",
                teacherId,
                Object.class
        );
        if(response instanceof ErrorResponse) {
            log.error(((ErrorResponse) response).message());
        } else if(response instanceof FindAllCoursesWrapper) {
            log.info("Courses: ");
            ((FindAllCoursesWrapper) response).list().forEach(
                    course -> log.info("Course id: {}, \nCourse name: {}, \nTeacher id: {}\n\n", course.id(), course.name(), course.teacherId())
            );
            log.info("");
        }
    }
}

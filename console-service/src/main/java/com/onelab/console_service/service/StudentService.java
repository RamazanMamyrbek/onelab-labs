package com.onelab.console_service.service;

import com.onelab.console_service.config.KafkaClient;
import lombok.extern.slf4j.Slf4j;
import org.onelab.common.dto.request.AssignCourseDto;
import org.onelab.common.dto.request.DeleteUserDto;
import org.onelab.common.dto.request.UserRequestDto;
import org.onelab.common.dto.response.*;

import java.util.Scanner;

@Slf4j
public class StudentService {
    public static void viewAllStudents(Scanner scanner, KafkaClient kafkaClient) {
        Object response = kafkaClient.sendAndReceive(
                "user.request.getAllStudents",
                null,
                Object.class
        );
        if(response instanceof ErrorResponse) {
            log.error(((ErrorResponse) response).message());
        } else if(response instanceof FindAllUsersWrapper) {
            log.info("Students list");
            ((FindAllUsersWrapper) response).list().forEach(
                    student -> log.info("Id: {}, Name: {}, Role: {}", student.id(), student.name(), student.role())
            );
        }
        log.info("");
    }

    public static void addStudent(Scanner scanner, KafkaClient kafkaClient) {
        log.info("Enter a name: ");
        String name = scanner.nextLine();
        Object response = kafkaClient.sendAndReceive(
                "user.request.createStudent",
                new UserRequestDto(name),
                Object.class
        );
        if(response instanceof ErrorResponse) {
            log.error(((ErrorResponse) response).message());
        } else if(response instanceof UsersResponseDto) {
            log.info("Student was created successfully");
            log.info("Id: {}, Name: {}, Role: {}",((UsersResponseDto) response).id(), ((UsersResponseDto) response).name(),((UsersResponseDto) response).role() );
        }
        log.info("");
    }

    public static void removeStudent(Scanner scanner, KafkaClient kafkaClient) {
        log.info("Enter a student id: ");
        Long id = scanner.nextLong();
        Object response = kafkaClient.sendAndReceive(
                "user.request.deleteStudent",
                new DeleteUserDto(id),
                Object.class
        );
        if(response instanceof ErrorResponse) {
            log.error(((ErrorResponse) response).message());
        } else if(response instanceof String) {
            log.info("Student was deleted successfully");
        }
        log.info("");
    }

    public static void addStudentToTheCourse(Scanner scanner, KafkaClient kafkaClient) {
        log.info("Enter a student id: ");
        Long studentId = scanner.nextLong();
        log.info("Enter a course id: ");
        Long courseId = scanner.nextLong();
        Object response = kafkaClient.sendAndReceive(
                "user.request.assignCourseToStudent",
                new AssignCourseDto(studentId, courseId),
                Object.class
        );
        if(response instanceof ErrorResponse) {
            log.error(((ErrorResponse) response).message());
        } else if(response instanceof String) {
            log.info(((String) response));
        }
        log.info("");
    }

    public static void viewStudentNotifications(Scanner scanner, KafkaClient kafkaClient) {
        log.info("Enter a student id: ");
        Long studentId = scanner.nextLong();
        Object response = kafkaClient.sendAndReceive(
                "notification.request.getNotifications",
                studentId,
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

    public static void viewStudentCourses(Scanner scanner, KafkaClient kafkaClient) {
        log.info("Enter a student id: ");
        Long teacherId = scanner.nextLong();
        Object response = kafkaClient.sendAndReceive(
                "user.request.getStudentCourses",
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

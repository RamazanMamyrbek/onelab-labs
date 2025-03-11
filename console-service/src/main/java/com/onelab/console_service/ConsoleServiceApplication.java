package com.onelab.console_service;

import com.onelab.console_service.config.KafkaClient;
import com.onelab.console_service.service.CourseService;
import com.onelab.console_service.service.StudentService;
import com.onelab.console_service.service.TeacherService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Scanner;

@Slf4j
@SpringBootApplication
public class ConsoleServiceApplication {
	public static void main(String[] args) {
		SpringApplication.run(ConsoleServiceApplication.class, args);
	}

	@Bean
	public ApplicationRunner runner(KafkaClient kafkaClient) {
		return args -> {
			Scanner scanner = new Scanner(System.in);
			while (true) {
				log.info("Главное меню");
				log.info("1. Courses");
				log.info("2. Teachers");
				log.info("3. Students");
				log.info("0. Exit");
				log.info("Choose an option:");

				int option = scanner.nextInt();
				scanner.nextLine();

				switch (option) {
					case 1 -> showCoursesMenu(scanner, kafkaClient);
					case 2 -> showTeachersMenu(scanner, kafkaClient);
					case 3 -> showStudentsMenu(scanner, kafkaClient);
					case 0 -> {
						log.info("Exiting...");
						return;
					}
					default -> log.info("Invalid option. Try again.");
				}
			}
		};
	}

	private void showCoursesMenu(Scanner scanner, KafkaClient kafkaClient) {
		while (true) {
			log.info("Courses Menu");
			log.info("1. View all courses");
			log.info("2. Add a course");
			log.info("3. Get lessons for course");
			log.info("4. Add lesson to a course");
			log.info("5. Update a course");
			log.info("6. Update a lesson");
			log.info("7. Delete a course");
			log.info("8. Delete a lesson");
			log.info("0. Back");
			log.info("Choose an option:");

			int option = scanner.nextInt();
			scanner.nextLine();

			switch (option) {
				case 1 -> CourseService.viewAllCourses(scanner, kafkaClient);
				case 2 -> CourseService.addCourse(scanner, kafkaClient);
				case 3 -> CourseService.getLessonsForCourse(scanner, kafkaClient);
				case 4 -> CourseService.addLessonToCourse(scanner, kafkaClient);
				case 5 -> CourseService.updateCourse(scanner, kafkaClient);
				case 6 -> CourseService.updateLesson(scanner, kafkaClient);
				case 7 -> CourseService.deleteCourse(scanner, kafkaClient);
				case 8 -> CourseService.deleteLesson(scanner, kafkaClient);
				case 0 -> {
					return;
				}
				default -> log.info("Invalid option. Try again.");
			}
		}
	}

	private void showTeachersMenu(Scanner scanner, KafkaClient kafkaClient) {
		while (true) {
			log.info("Teachers Menu");
			log.info("1. View all teachers");
			log.info("2. Add a teacher");
			log.info("3. Remove a teacher");
			log.info("4. Assign a course to a teacher");
			log.info("5. Get notifications of a teacher");
			log.info("6. View courses of a teacher");
			log.info("0. Back");
			log.info("Choose an option:");

			int option = scanner.nextInt();
			scanner.nextLine();

			switch (option) {
				case 1 -> TeacherService.viewAllTeachers(scanner, kafkaClient);
				case 2 -> TeacherService.addTeacher(scanner, kafkaClient);
				case 3 -> TeacherService.removeTeacher(scanner, kafkaClient);
				case 4 -> TeacherService.assignCourseToTeacher(scanner, kafkaClient);
				case 5 -> TeacherService.viewTeacherNotifications(scanner, kafkaClient);
				case 6 -> TeacherService.viewTeacherCourses(scanner, kafkaClient);
				case 0 -> {
					return;
				}
				default -> log.info("Invalid option. Try again.");
			}
		}
	}

	private void showStudentsMenu(Scanner scanner, KafkaClient kafkaClient) {
		while (true) {
			log.info("Students Menu");
			log.info("1. View all students");
			log.info("2. Add a student");
			log.info("3. Remove a student");
			log.info("4. Add student to the course");
			log.info("5. Get notifications of the student");
			log.info("6. View courses of the student");
			log.info("0. Back");
			log.info("Choose an option:");

			int option = scanner.nextInt();
			scanner.nextLine();

			switch (option) {
				case 1 -> StudentService.viewAllStudents(scanner, kafkaClient);
				case 2 -> StudentService.addStudent(scanner, kafkaClient);
				case 3 -> StudentService.removeStudent(scanner, kafkaClient);
				case 4 -> StudentService.addStudentToTheCourse(scanner, kafkaClient);
				case 5 -> StudentService.viewStudentNotifications(scanner, kafkaClient);
				case 6 -> StudentService.viewStudentCourses(scanner, kafkaClient);
				case 0 -> {
					return;
				}
				default -> log.info("Invalid option. Try again.");
			}
		}
	}
}


package com.onelab.users_service.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserKafkaConsumer {
//
//    private final UserService userService;
//
//    @KafkaListener(topics = "${kafka.topics.user.request.getAllStudents}", groupId = "user-service-group")
//    @SendTo
//    public Object listenGetAllStudents() {
//        try {
//            log.info("Обработка запроса getAllStudents");
//            return new FindAllUsersWrapper(userService.getAllStudents());
//        } catch (Exception e) {
//            log.info("Error while getting all students: {}, {}", e.getMessage(), e);
//            return new ErrorResponse("Error while getting all students: %s".formatted(e.getMessage()));
//        }
//    }
//
//    @KafkaListener(topics = "${kafka.topics.user.request.getAllTeachers}", groupId = "user-service-group")
//    @SendTo
//    public Object listenGetAllTeachers() {
//        try {
//            log.info("Обработка запроса getAllTeachers");
//            return new FindAllUsersWrapper(userService.getAllTeachers());
//        } catch (Exception e) {
//            log.info("Error while getting all teachers: {}, {}", e.getMessage(), e);
//            return new ErrorResponse("Error while getting all teachers: %s".formatted(e.getMessage()));
//        }
//    }
//
//    @KafkaListener(topics = "${kafka.topics.user.request.createStudent}", groupId = "user-service-group")
//    @SendTo
//    public Object listenCreateStudent(UserRequestDto studentDto) {
//        try {
//            log.info("Обработка запроса createStudent: {}", studentDto);
//            return userService.createStudent(studentDto);
//        } catch (Exception e) {
//            log.info("Error while creating a student:  {}, {}", e.getMessage(), e);
//            return new ErrorResponse("Error while creating a student: %s".formatted(e.getMessage()));
//        }
//    }
//
//    @KafkaListener(topics = "${kafka.topics.user.request.createTeacher}", groupId = "user-service-group")
//    @SendTo
//    public Object listenCreateTeacher(UserRequestDto teacherDto) {
//        try {
//            log.info("Обработка запроса createTeacher: {}", teacherDto);
//            return userService.createTeacher(teacherDto);
//        } catch (Exception e) {
//            log.info("Error while creating a teacher:  {}, {}", e.getMessage(), e);
//            return new ErrorResponse("Error while creating a teacher: %s".formatted(e.getMessage()));
//        }
//    }
//
//    @KafkaListener(topics = "${kafka.topics.user.request.assignCourseToTeacher}", groupId = "user-service-group")
//    @SendTo
//    public Object listenAssignCourseToTeacher(AssignCourseDto assignCourseDto) {
//        try {
//            log.info("Обработка запроса assignCourseToTeacher: {}", assignCourseDto);
//            userService.assignCourseToTeacher(assignCourseDto);
//            return "Teacher with id %s was assigned to a course with id %s".formatted(assignCourseDto.userId(), assignCourseDto.courseId());
//        } catch (Exception e) {
//            log.info("Error while assigning a teacher to a course:  {}, {}", e.getMessage(), e);
//            return new ErrorResponse("Error while assigning a teacher to a course: %s".formatted(e.getMessage()));
//        }
//    }
//
//    @KafkaListener(topics = "${kafka.topics.user.request.assignCourseToStudent}", groupId = "user-service-group")
//    @SendTo
//    public Object listenAssignCourseToStudent(AssignCourseDto assignCourseDto) {
//        try {
//            log.info("Обработка запроса assignCourseToStudent: {}", assignCourseDto);
//            userService.assignCourseToStudent(assignCourseDto);
//            return "Student with id %s was added to a course with id %s".formatted(assignCourseDto.userId(), assignCourseDto.courseId());
//        } catch (Exception e) {
//            log.info("Error while assigning a student to a course:  {}, {}", e.getMessage(), e);
//            return new ErrorResponse("Error while adding student to a course: %s".formatted(e.getMessage()));
//        }
//    }
//
//    @KafkaListener(topics = "${kafka.topics.user.request.deleteTeacher}", groupId = "user-service-group")
//    @SendTo
//    public Object listenDeleteTeacher(DeleteUserDto deleteUserDto) {
//        try {
//            log.info("Обработка запроса deleteTeacher: {}", deleteUserDto);
//            userService.deleteTeacher(deleteUserDto);
//            return "Teacher was deleted successfully";
//        } catch (Exception e) {
//            log.info("Error while deleting a teacher:  {}, {}", e.getMessage(), e);
//            return new ErrorResponse("Error while deleting a teacher: %s".formatted(e.getMessage()));
//        }
//    }
//
//    @KafkaListener(topics = "${kafka.topics.user.request.deleteStudent}", groupId = "user-service-group")
//    @SendTo
//    public Object listenDeleteStudent(DeleteUserDto deleteUserDto) {
//        try {
//            log.info("Обработка запроса deleteStudent: {}", deleteUserDto);
//            userService.deleteStudent(deleteUserDto);
//            return "Student was deleted successfully";
//        } catch (Exception e) {
//            log.info("Error while deleting a student:  {}, {}", e.getMessage(), e);
//            return new ErrorResponse("Error while deleting a student: %s".formatted(e.getMessage()));
//        }
//    }
//
//    @KafkaListener(topics = "${kafka.topics.user.request.getStudentCourses}", groupId = "user-service-group")
//    @SendTo
//    public Object listenGetStudentCourses(@Payload Long studentId) {
//        try {
//            log.info("Обработка запроса getStudentCourses: {}", studentId);
//            return new FindAllCoursesWrapper(userService.getStudentCourses(studentId));
//        } catch (Exception e) {
//            log.info("Error while getting courses:  {}, {}", e.getMessage(), e);
//            return new ErrorResponse("Error while getting courses: %s".formatted(e.getMessage()));
//        }
//    }
}


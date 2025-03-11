package com.onelab.users_service.service.impl;

import com.onelab.users_service.config.KafkaClient;
import com.onelab.users_service.entity.Users;
import com.onelab.users_service.entity.enums.Role;
import com.onelab.users_service.mapper.UserMapper;
import com.onelab.users_service.producer.UserServiceProducer;
import com.onelab.users_service.repository.UserRepository;
import com.onelab.users_service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.onelab.common.dto.request.AssignCourseDto;
import org.onelab.common.dto.request.DeleteUserDto;
import org.onelab.common.dto.request.NotificationDto;
import org.onelab.common.dto.request.UserRequestDto;
import org.onelab.common.dto.response.*;
import org.onelab.common.exception.BadRequestException;
import org.onelab.common.exception.ResourceNotFoundException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final UserServiceProducer userServiceProducer;
    private final KafkaClient kafkaClient;

    @Override
    public List<UsersResponseDto> getAllStudents() {
        return userRepository
                .findAllByRole(Role.STUDENT)
                .stream()
                .map(userMapper::mapToUserResponseDTO)
                .toList();
    }

    @Override
    public List<UsersResponseDto> getAllTeachers() {
        return userRepository
                .findAllByRole(Role.TEACHER)
                .stream()
                .map(userMapper::mapToUserResponseDTO)
                .toList();
    }

    @Override
    @Transactional
    public UsersResponseDto createStudent(UserRequestDto studentDto) {
        Users student = Users
                .builder()
                .name(studentDto.name())
                .role(Role.STUDENT)
                .build();
        student = userRepository.save(student);
        userServiceProducer.sendNotification(new NotificationDto(
                student.getId(),
                "Student with id %s was created".formatted(student.getId())
        ));
        return userMapper.mapToUserResponseDTO(student);
    }

    @Override
    @Transactional
    public UsersResponseDto createTeacher(UserRequestDto teacherDto) {
        Users teacher = Users
                .builder()
                .name(teacherDto.name())
                .role(Role.TEACHER)
                .build();
        teacher = userRepository.save(teacher);
        userServiceProducer.sendNotification(new NotificationDto(
                teacher.getId(),
                "Teacher with id %s was created".formatted(teacher.getId())
        ));
        return userMapper.mapToUserResponseDTO(teacher);
    }

    @Override
    @Transactional
    public void assignCourseToTeacher(AssignCourseDto assignCourseDto) {
        Users teacher = getTeacherById(assignCourseDto.userId());
        Object response = kafkaClient.sendAndReceive(
                "course.request.setTeacher",
                assignCourseDto,
                Object.class
        );
        if(response instanceof ErrorResponse) {
            throw BadRequestException.errorWhileSettingTeacherToCourse(((ErrorResponse) response).message());
        }
        teacher.getCourseIds().add(assignCourseDto.courseId());
        teacher = userRepository.save(teacher);
        userServiceProducer.sendNotification(new NotificationDto(
                assignCourseDto.userId(),
                "Teacher with id %s was assigned to the course with id %s".formatted(assignCourseDto.userId(), assignCourseDto.courseId())
        ));
    }


    @Override
    @Transactional
    public void assignCourseToStudent(AssignCourseDto assignCourseDto) {
        Users student = getStudentById(assignCourseDto.userId());
        Object response = kafkaClient.sendAndReceive(
                "course.request.getCourse",
                assignCourseDto.courseId(),
                Object.class
        );
        if(response instanceof ErrorResponse) {
            throw BadRequestException.errorWhileGettingCourse(((ErrorResponse) response).message());
        }
        student.getCourseIds().add(assignCourseDto.courseId());
        student = userRepository.save(student);
        userServiceProducer.sendNotification(new NotificationDto(
                assignCourseDto.userId(),
                "Student with id %s was assigned to the course with id %s".formatted(assignCourseDto.userId(), assignCourseDto.courseId())
        ));
    }

    @Override
    @Transactional
    public void deleteTeacher(DeleteUserDto deleteUserDto) {
        Users teacher = getTeacherById(deleteUserDto.userId());
        userRepository.delete(teacher);
        userServiceProducer.sendNotification(new NotificationDto(
                deleteUserDto.userId(),
                "Teacher with id %s was deleted".formatted(deleteUserDto.userId())
        ));
    }

    @Override
    @Transactional
    public void deleteStudent(DeleteUserDto deleteUserDto) {
        Users student = getStudentById(deleteUserDto.userId());
        userRepository.delete(student);
        userServiceProducer.sendNotification(new NotificationDto(
                deleteUserDto.userId(),
                "Student with id %s was deleted".formatted(deleteUserDto.userId())
        ));
    }

    @Override
    public List<CourseResponseDto> getStudentCourses(Long studentId) {
        Users student = getStudentById(studentId);
        Object response = kafkaClient.sendAndReceive(
                "course.request.findAllById",
                new FindAllCoursesSetWrapper(student.getCourseIds()),
                Object.class
        );
        if(response instanceof ErrorResponse) {
            throw BadRequestException.errorWhileGettingCourses(((ErrorResponse) response).message());
        }
        return ((FindAllCoursesWrapper)response).list();
    }

    private Users getTeacherById(Long id) {
        Users user = getUserById(id);
        if(!user.getRole().equals(Role.TEACHER)) {
            throw ResourceNotFoundException.teacherNotFound(id);
        }
        return user;
    }

    private Users getStudentById(Long id) {
        Users user = getUserById(id);
        if(!user.getRole().equals(Role.STUDENT)) {
            throw ResourceNotFoundException.studentNotFound(id);
        }
        return user;
    }

    private Users getUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> ResourceNotFoundException.userNotFound(userId)
        );
    }
}


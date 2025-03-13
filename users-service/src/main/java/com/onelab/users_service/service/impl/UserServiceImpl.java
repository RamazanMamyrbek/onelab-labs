package com.onelab.users_service.service.impl;

import com.onelab.users_service.config.KafkaClient;
import com.onelab.users_service.entity.Users;
import com.onelab.users_service.mapper.UserMapper;
import com.onelab.users_service.producer.UserServiceProducer;
import com.onelab.users_service.repository.UserRepository;
import com.onelab.users_service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.onelab.common.dto.request.*;
import org.onelab.common.dto.response.*;
import org.onelab.common.enums.Role;
import org.onelab.common.exception.BadRequestException;
import org.onelab.common.exception.ResourceNotFoundException;
import org.onelab.common.feign.CourseFeignClient;
import org.onelab.common.feign.NotificationFeignClient;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.ErrorResponse;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final UserServiceProducer userServiceProducer;
    private final KafkaClient kafkaClient;
    private final PasswordEncoder passwordEncoder;
    private final NotificationFeignClient notificationFeignClient;
    private final CourseFeignClient courseFeignClient;


    @Override
    @Transactional
    public void assignCourseToStudent(AssignCourseDto assignCourseDto, String token) {
        Users student = getStudentById(assignCourseDto.userId());
        CourseResponseDto courseResponseDto = courseFeignClient.getCourseById(assignCourseDto.courseId(), token).getBody();
        student.getCourseIds().add(assignCourseDto.courseId());
        student = userRepository.save(student);
        userServiceProducer.sendNotification(new NotificationDto(
                assignCourseDto.userId(),
                "Student with id %s was assigned to the course with id %s".formatted(assignCourseDto.userId(), assignCourseDto.courseId())
        ));
    }


    @Override
    public List<CourseResponseDto> getStudentCourses(Long studentId, String token) {
        Users student = getStudentById(studentId);
        List<CourseResponseDto> courseResponseDtoList = courseFeignClient.getCoursesByIds(student.getCourseIds(), token).getBody();
        return courseResponseDtoList;
    }

    @Override
    public List<UsersResponseDto> getAllUsers(Role role) {
        return userRepository.findAll()
                .stream()
                .filter(user -> role == null || user.getRole().equals(role))
                .map(userMapper::mapToUserResponseDTO)
                .toList();
    }

    @Override
    @Transactional
    public UsersResponseDto registerUser(UserRegisterRequestDto requestDto) {
        if(userRepository.existsByEmail(requestDto.email())) {
            throw BadRequestException.userAlreadyExistsException(requestDto.email());
        }
        Users user = Users
                .builder()
                .email(requestDto.email())
                .name(requestDto.name())
                .passwordHash(passwordEncoder.encode(requestDto.password()))
                .role(requestDto.role())
                .build();
        user = userRepository.save(user);
        userServiceProducer.sendNotification(new NotificationDto(user.getId(), "User with email %s was registered".formatted(user.getEmail())));
        return userMapper.mapToUserResponseDTO(user);
    }

    @Override
    public Users getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(
                () -> new UsernameNotFoundException("User with email %s was not found".formatted(email))
        );
    }

    @Override
    public UsersResponseDto getUserInfoById(Long userId) {
        return userMapper.mapToUserResponseDTO(getUserById(userId));
    }

    @Override
    public UsersResponseDto getUserProfileByEmail(String name) {
        return userMapper.mapToUserResponseDTO(getUserByEmail(name));
    }

    @Override
    @Transactional
    public UsersResponseDto editProfile(UserEditRequestDto requestDto, String email) {
        Users user = getUserByEmail(email);
        user.setRole(requestDto.role());
        user.setName(requestDto.name());
        user = userRepository.save(user);
        userServiceProducer.sendNotification(new NotificationDto(user.getId(), "User with email %s was edited".formatted(email)));
        return userMapper.mapToUserResponseDTO(user);
    }

    @Override
    @Transactional
    public void deleteUserByEmail(String email) {
        Users user = getUserByEmail(email);
        userRepository.delete(user);
        userServiceProducer.sendNotification(new NotificationDto(user.getId(), "User with email %s was deleted".formatted(email)));
    }

    @Override
    public List<NotificationResponseDto> getNotifications(String email, String token) {
        Users user = getUserByEmail(email);
        List<NotificationResponseDto> responseDtoList = notificationFeignClient.getNotificationsByUserId(token, user.getId());
        return responseDtoList;
    }


    private Users getTeacherById(Long id) {
        Users user = getUserById(id);
        if(!user.getRole().equals(Role.ROLE_TEACHER)) {
            throw ResourceNotFoundException.teacherNotFound(id);
        }
        return user;
    }

    private Users getStudentById(Long id) {
        Users user = getUserById(id);
        if(!user.getRole().equals(Role.ROLE_STUDENT)) {
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


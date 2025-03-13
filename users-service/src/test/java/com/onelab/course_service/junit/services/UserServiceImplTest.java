package com.onelab.course_service.junit.services;


import com.onelab.users_service.entity.Users;
import com.onelab.users_service.mapper.UserMapper;
import com.onelab.users_service.producer.UserServiceProducer;
import com.onelab.users_service.repository.UserRepository;
import com.onelab.users_service.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.onelab.common.dto.request.*;
import org.onelab.common.dto.response.*;
import org.onelab.common.enums.Role;
import org.onelab.common.exception.BadRequestException;
import org.onelab.common.exception.ResourceNotFoundException;
import org.onelab.common.feign.CourseFeignClient;
import org.onelab.common.feign.NotificationFeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private UserServiceProducer userServiceProducer;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private NotificationFeignClient notificationFeignClient;
    @Mock
    private CourseFeignClient courseFeignClient;

    @InjectMocks
    private UserServiceImpl userService;

    private Users testUser;

    @BeforeEach
    void setUp() {
        testUser = Users.builder()
                .id(1L)
                .email("test@example.com")
                .name("Test User")
                .passwordHash("hashed_password")
                .role(Role.ROLE_STUDENT)
                .courseIds(new HashSet<>(Set.of(100L, 101L)))
                .build();
    }

    @Test
    void shouldAssignCourseToStudent() {
        AssignCourseDto assignCourseDto = new AssignCourseDto(1L, 102L);
        String token = "test-token";
        CourseResponseDto courseResponse = new CourseResponseDto(102L, "Test Course", 1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(courseFeignClient.getCourseById(102L, token)).thenReturn(ResponseEntity.ok(courseResponse));
        when(userRepository.save(any(Users.class))).thenReturn(testUser);

        userService.assignCourseToStudent(assignCourseDto, token);

        assertThat(testUser.getCourseIds()).contains(102L);
        verify(userRepository, times(1)).save(any(Users.class));
        verify(userServiceProducer, times(1)).sendNotification(any(NotificationDto.class));
    }

    @Test
    void shouldReturnStudentCourses() {
        String token = "test-token";
        List<CourseResponseDto> expectedCourses = List.of(
                new CourseResponseDto(100L, "Course 100", 1L),
                new CourseResponseDto(101L, "Course 101", 1L)
        );

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(courseFeignClient.getCoursesByIds(Set.of(100L, 101L), token)).thenReturn(ResponseEntity.ok(expectedCourses));

        List<CourseResponseDto> actualCourses = userService.getStudentCourses(1L, token);

        assertThat(actualCourses).hasSize(2);
        assertThat(actualCourses).containsExactlyElementsOf(expectedCourses);
        verify(courseFeignClient, times(1)).getCoursesByIds(Set.of(100L, 101L), token);
    }

    @Test
    void registerUser_ShouldRegisterUser_WhenEmailIsUnique() {
        UserRegisterRequestDto requestDto = new UserRegisterRequestDto("test@example.com", "Test User", "password", Role.ROLE_STUDENT);
        when(userRepository.existsByEmail(requestDto.email())).thenReturn(false);
        when(passwordEncoder.encode(requestDto.password())).thenReturn("hashed_password");
        when(userRepository.save(any())).thenReturn(testUser);
        when(userMapper.mapToUserResponseDTO(any())).thenReturn(new UsersResponseDto(1L, "test@example.com", "Test User", Role.ROLE_STUDENT.name()));

        UsersResponseDto response = userService.registerUser(requestDto);

        assertThat(response).isNotNull();
        assertThat(response.email()).isEqualTo("test@example.com");
        verify(userRepository).save(any());
        verify(userServiceProducer).sendNotification(any());
    }

    @Test
    void getAllUsers_ShouldReturnFilteredUsers() {
        when(userRepository.findAll()).thenReturn(List.of(testUser));
        when(userMapper.mapToUserResponseDTO(testUser)).thenReturn(new UsersResponseDto(1L, "test@example.com", "Test User", Role.ROLE_STUDENT.name()));

        List<UsersResponseDto> users = userService.getAllUsers(Role.ROLE_STUDENT);

        assertThat(users).hasSize(1);
        assertThat(users.get(0).email()).isEqualTo("test@example.com");
    }

    @Test
    void getUserByEmail_ShouldThrowException_WhenUserNotFound() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserByEmail("test@example.com"))
                .isInstanceOf(UsernameNotFoundException.class);
    }

    @Test
    void editProfile_ShouldUpdateUser_WhenValid() {
        UserEditRequestDto editRequest = new UserEditRequestDto("newPassword","Updated Name", Role.ROLE_TEACHER);
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(userRepository.save(any())).thenReturn(testUser);
        when(userMapper.mapToUserResponseDTO(any())).thenReturn(new UsersResponseDto(1L, "test@example.com", "Updated Name", Role.ROLE_TEACHER.name()));

        UsersResponseDto response = userService.editProfile(editRequest, "test@example.com");

        assertThat(response.name()).isEqualTo("Updated Name");
        assertThat(response.role()).isEqualTo(Role.ROLE_TEACHER.name());
    }

    @Test
    void deleteUserByEmail_ShouldDeleteUser() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        userService.deleteUserByEmail("test@example.com");

        verify(userRepository).delete(testUser);
        verify(userServiceProducer).sendNotification(any());
    }
}

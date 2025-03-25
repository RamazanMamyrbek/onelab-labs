package com.onelab.course_service.junit.services;

import com.onelab.users_service.entity.PendingUser;
import com.onelab.users_service.entity.Users;
import com.onelab.users_service.mapper.UserMapper;
import com.onelab.users_service.producer.UserServiceProducer;
import com.onelab.users_service.repository.elastic.UsersSearchRepository;
import com.onelab.users_service.repository.jpa.PendingUserRepository;
import com.onelab.users_service.repository.jpa.UserRepository;
import com.onelab.users_service.service.EmailService;
import com.onelab.users_service.service.ExchangeRateService;
import com.onelab.users_service.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.onelab.common.dto.request.AssignCourseDto;
import org.onelab.common.dto.request.ConfirmEmailRequestDto;
import org.onelab.common.dto.request.UserEditRequestDto;
import org.onelab.common.dto.request.UserRegisterRequestDto;
import org.onelab.common.dto.response.CourseResponseDto;
import org.onelab.common.dto.response.NotificationResponseDto;
import org.onelab.common.dto.response.PendingUserResponseDto;
import org.onelab.common.dto.response.UsersResponseDto;
import org.onelab.common.enums.Currency;
import org.onelab.common.enums.Role;
import org.onelab.common.feign.CourseFeignClient;
import org.onelab.common.feign.NotificationFeignClient;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock private UserRepository userRepository;
    @Mock private PendingUserRepository pendingUserRepository;
    @Mock private UserMapper userMapper;
    @Mock private UserServiceProducer userServiceProducer;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private NotificationFeignClient notificationFeignClient;
    @Mock private CourseFeignClient courseFeignClient;
    @Mock private UsersSearchRepository usersSearchRepository;
    @Mock private EmailService emailService;
    @Mock private ExchangeRateService exchangeRateService;

    @InjectMocks
    private UserServiceImpl userService;



    @Test
    void getAllUsers_ShouldFilterByRole() {
        Users student = new Users();
        student.setRole(Role.ROLE_STUDENT);
        Users teacher = new Users();
        teacher.setRole(Role.ROLE_TEACHER);

        when(userRepository.findAll()).thenReturn(List.of(student, teacher));
        when(userMapper.mapToUserResponseDTO(student)).thenReturn(new UsersResponseDto(1L, "", "", "", 20L, "STUDENT", BigDecimal.ZERO, "USD"));

        List<UsersResponseDto> result = userService.getAllUsers(Role.ROLE_STUDENT);

        assertEquals(1, result.size());
        assertEquals("STUDENT", result.get(0).role());
    }

    @Test
    void registerUser_ShouldCreatePendingUser() {
        UserRegisterRequestDto request = new UserRegisterRequestDto(
                "test@test.com",
                "pass",
                "Name",
                "Country",
                20L,
                Role.ROLE_STUDENT
        );

        when(userRepository.existsByEmail("test@test.com")).thenReturn(false);

        when(passwordEncoder.encode("pass")).thenReturn("encoded");
        PendingUser savedPendingUser = PendingUser.builder()
                .email("test@test.com")
                .code("1234")
                .build();
        when(pendingUserRepository.save(any(PendingUser.class))).thenReturn(savedPendingUser);

        PendingUserResponseDto result = userService.registerUser(request);
        assertEquals("test@test.com", result.email());
        verify(emailService).sendConfirmationCode(eq("test@test.com"), anyString());
    }



    @Test
    void deleteUserByEmail_ShouldRemoveUser() {
        Users user = new Users();
        user.setEmail("test@test.com");

        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));

        userService.deleteUserByEmail("test@test.com");

        verify(userRepository).delete(user);
    }

    @Test
    void getNotifications_ShouldReturnNotifications() {
        Users user = new Users();
        user.setId(1L);

        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));
        when(notificationFeignClient.getNotificationsByUserId("token", 1L))
                .thenReturn(List.of(new NotificationResponseDto(1L, "Message", null)));

        List<NotificationResponseDto> result = userService.getNotifications("test@test.com", "token");

        assertEquals(1, result.size());
    }



    @Test
    void replenishBalance_ShouldAddFunds() {
        Users user = new Users();
        user.setBalance(BigDecimal.ZERO);

        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));
        when(exchangeRateService.toUsd(any(), any())).thenReturn(new BigDecimal("100"));

        UsersResponseDto result = userService.replenishBalance("test@test.com", 100L, Currency.USD);

        assertEquals(new BigDecimal("100"), user.getBalance());
    }

    @Test
    void getStudentCourses_ShouldReturnCourses() {
        Users student = new Users();
        student.setId(1L);
        student.setRole(Role.ROLE_STUDENT);
        student.setCourseIds(Set.of(1L, 2L));

        List<CourseResponseDto> courses = List.of(
                new CourseResponseDto(1L, "Course1", "", 100L, null, 1L),
                new CourseResponseDto(2L, "Course2", "", 150L, null, 1L)
        );

        when(userRepository.findById(1L)).thenReturn(Optional.of(student));
        when(courseFeignClient.getCoursesByIds(Set.of(1L, 2L), "token"))
                .thenReturn(ResponseEntity.ok(courses));

        List<CourseResponseDto> result = userService.getStudentCourses(1L, "token");

        assertEquals(2, result.size());
    }



    @Test
    void assignCourseToStudent_ShouldAssignCourse() {
        Users student = new Users();
        student.setId(1L);
        student.setRole(Role.ROLE_STUDENT);

        Users teacher = new Users();
        teacher.setId(2L);
        teacher.setRole(Role.ROLE_TEACHER);

        CourseResponseDto course = new CourseResponseDto(1L, "Course1", "", 100L, null, 2L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(student));
        when(userRepository.findByEmail("teacher@test.com")).thenReturn(Optional.of(teacher));
        when(courseFeignClient.getCourseById(1L, "token")).thenReturn(ResponseEntity.ok(course));

        userService.assignCourseToStudent(new AssignCourseDto(1L, 1L), "teacher@test.com", "token");

        assertTrue(student.getCourseIds().contains(1L));
        verify(userServiceProducer).sendNotification(any());
    }
}
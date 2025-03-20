package com.onelab.users_service.service;


import com.onelab.users_service.entity.Users;
import org.onelab.common.dto.request.AssignCourseDto;
import org.onelab.common.dto.request.UserEditRequestDto;
import org.onelab.common.dto.request.UserRegisterRequestDto;
import org.onelab.common.dto.response.CourseResponseDto;
import org.onelab.common.dto.response.NotificationResponseDto;
import org.onelab.common.dto.response.UsersResponseDto;
import org.onelab.common.enums.Role;

import java.util.List;

public interface UserService {

    void assignCourseToStudent(AssignCourseDto assignCourseDto, String token);

    List<CourseResponseDto> getStudentCourses(Long studentId, String token);

    List<UsersResponseDto> getAllUsers(Role role);

    UsersResponseDto registerUser(UserRegisterRequestDto requestDto);

    Users getUserByEmail(String username);

    UsersResponseDto getUserInfoById(Long userId);

    UsersResponseDto getUserProfileByEmail(String email);

    UsersResponseDto editProfile(UserEditRequestDto requestDto, String email);

    void deleteUserByEmail(String email);

    List<NotificationResponseDto> getNotifications(String email, String authorization);

    List<UsersResponseDto> searchUsers(String nameQuery, Long minAge, Long maxAge, String country,Role role, int page, int size);

    List<UsersResponseDto> getStudentsForCourse(Long courseId);

    Users getUserById(Long senderId);
}

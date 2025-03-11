package com.onelab.users_service.service;


import org.onelab.common.dto.request.AssignCourseDto;
import org.onelab.common.dto.request.DeleteUserDto;
import org.onelab.common.dto.request.UserRequestDto;
import org.onelab.common.dto.response.CourseResponseDto;
import org.onelab.common.dto.response.UsersResponseDto;

import java.util.List;

public interface UserService {
    List<UsersResponseDto> getAllStudents();

    List<UsersResponseDto> getAllTeachers();

    UsersResponseDto createStudent(UserRequestDto studentDto);

    UsersResponseDto createTeacher(UserRequestDto teacherDto);

    void assignCourseToTeacher(AssignCourseDto assignCourseDto);

    void assignCourseToStudent(AssignCourseDto assignCourseDto);

    void deleteTeacher(DeleteUserDto deleteUserDto);

    void deleteStudent(DeleteUserDto deleteUserDto);

    List<CourseResponseDto> getStudentCourses(Long studentId);
}

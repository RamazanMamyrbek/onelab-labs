package org.ramazanmamyrbek.services;

import org.ramazanmamyrbek.entity.Course;
import org.ramazanmamyrbek.entity.Teacher;

import java.util.List;

public interface TeacherService {
    Teacher createTeacher(String name);

    List<Teacher> getAllTeachers();

    Teacher getTeacherById(Long id);

    List<Course> getCoursesByTeacher(Long teacherId);

    void assignCourseToTeacher(Long teacherId, Long courseId);

    void deleteTeacher(Long teacherId);
}

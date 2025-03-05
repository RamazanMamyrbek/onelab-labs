package org.ramazanmamyrbek.services;

import org.ramazanmamyrbek.entity.Course;
import org.ramazanmamyrbek.entity.Student;

import java.util.List;

public interface StudentService {
    Student createStudent(String name);

    List<Student> getAllStudents();

    Student getStudentById(Long id);

    List<Course> getCoursesByStudent(Long studentId);

    void removeStudent(Long studentId);
}

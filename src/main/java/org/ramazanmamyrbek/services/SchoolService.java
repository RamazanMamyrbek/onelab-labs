package org.ramazanmamyrbek.services;

import org.ramazanmamyrbek.entity.Course;
import org.ramazanmamyrbek.entity.Student;
import org.ramazanmamyrbek.entity.Teacher;

import java.util.List;

public interface SchoolService {
    void enrollStudentToCourse(Long studentId, Long courseId);

    void enrollMultipleStudentsToCourse(List<Long> studentIds, Long courseId);

    List<Course> getAllCourses();

    List<Teacher> getAllTeachers();

    List<Student> getAllStudents();

    void removeAllData();

    void generateReport();
}

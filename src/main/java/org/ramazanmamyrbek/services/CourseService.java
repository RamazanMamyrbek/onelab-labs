package org.ramazanmamyrbek.services;

import org.ramazanmamyrbek.entity.Course;

import java.util.List;

public interface CourseService {
    Course createCourse(String courseName, Long teacherId);

    void addStudentToCourse(Long courseId, Long studentId);

    List<Course> getAllCourses();

    Course getCourseById(Long courseId);

    void updateCourseName(Long courseId, String newName);

    boolean isCourseFull(Long courseId, int maxCapacity);

    void removeCourse(Long courseId);
}

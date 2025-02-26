package org.ramazanmamyrbek.services;

import org.ramazanmamyrbek.entity.Course;
import org.ramazanmamyrbek.entity.Student;
import org.ramazanmamyrbek.entity.Teacher;

import java.util.List;

public interface SchoolService {
    void saveStudent(Student student);
    void saveTeacher(Teacher teacher);
    void saveCourse(Course course);

    List<Student> getAllStudents();
    List<Course> getAllCourses();
    List<Teacher> getAllTeachers();

    List<String> getStudentNamesByCourseId(Long courseId);
    List<String> getCoursesByTeacherId(Long teacherId);
}

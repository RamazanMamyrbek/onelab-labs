package org.ramazanmamyrbek.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ramazanmamyrbek.entity.Course;
import org.ramazanmamyrbek.entity.Student;
import org.ramazanmamyrbek.entity.Teacher;
import org.ramazanmamyrbek.services.CourseService;
import org.ramazanmamyrbek.services.SchoolService;
import org.ramazanmamyrbek.services.StudentService;
import org.ramazanmamyrbek.services.TeacherService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class SchoolServiceImpl implements SchoolService {
    private final CourseService courseService;
    private final StudentService studentService;
    private final TeacherService teacherService;

    @Override
    @Transactional
    public void enrollStudentToCourse(Long studentId, Long courseId) {
        courseService.addStudentToCourse(courseId, studentId);
    }

    @Override
    @Transactional
    public void enrollMultipleStudentsToCourse(List<Long> studentIds, Long courseId) {
        for (Long studentId : studentIds) {
            courseService.addStudentToCourse(courseId, studentId);
        }
    }

    @Override
    public List<Course> getAllCourses() {
        return courseService.getAllCourses();
    }

    @Override
    public List<Teacher> getAllTeachers() {
        return teacherService.getAllTeachers();
    }

    @Override
    public List<Student> getAllStudents() {
        return studentService.getAllStudents();
    }

    @Override
    @Transactional
    public void removeAllData() {
        for (Course course : courseService.getAllCourses()) {
            courseService.removeCourse(course.getId());
        }
        for (Student student : studentService.getAllStudents()) {
            studentService.removeStudent(student.getId());
        }
        for (Teacher teacher : teacherService.getAllTeachers()) {
            teacherService.deleteTeacher(teacher.getId());
        }
    }

    @Override
    public void generateReport() {
        log.info("=== ШКОЛЬНЫЙ ОТЧЕТ ===");
        log.info("Количество учителей: " + teacherService.getAllTeachers().size());
        log.info("Количество студентов: " + studentService.getAllStudents().size());
        log.info("Количество курсов: " + courseService.getAllCourses().size());
    }
}

package org.ramazanmamyrbek.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.ramazanmamyrbek.entity.Course;
import org.ramazanmamyrbek.entity.Student;
import org.ramazanmamyrbek.entity.Teacher;
import org.ramazanmamyrbek.services.impl.SchoolServiceImpl;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SchoolServiceTest {

    @Mock
    private CourseService courseService;

    @Mock
    private StudentService studentService;

    @Mock
    private TeacherService teacherService;

    @InjectMocks
    private SchoolServiceImpl schoolService;

    @Test
    void enrollStudentToCourse_ShouldEnrollStudent_WhenValidIdsProvided() {
        Long studentId = 1L;
        Long courseId = 2L;

        doNothing().when(courseService).addStudentToCourse(courseId, studentId);

        schoolService.enrollStudentToCourse(studentId, courseId);

        verify(courseService).addStudentToCourse(courseId, studentId);
    }

    @Test
    void enrollMultipleStudentsToCourse_ShouldEnrollAllStudents_WhenValidIdsProvided() {
        List<Long> studentIds = List.of(1L, 2L, 3L);
        Long courseId = 2L;

        doNothing().when(courseService).addStudentToCourse(anyLong(), anyLong());

        schoolService.enrollMultipleStudentsToCourse(studentIds, courseId);

        for (Long studentId : studentIds) {
            verify(courseService).addStudentToCourse(courseId, studentId);
        }
    }

    @Test
    void getAllCourses_ShouldReturnListOfCourses() {
        List<Course> courses = List.of(new Course(), new Course());
        when(courseService.getAllCourses()).thenReturn(courses);
        List<Course> result = schoolService.getAllCourses();
        assertEquals(2, result.size());
    }

    @Test
    void getAllTeachers_ShouldReturnListOfTeachers() {
        List<Teacher> teachers = List.of(new Teacher(), new Teacher());
        when(teacherService.getAllTeachers()).thenReturn(teachers);
        List<Teacher> result = schoolService.getAllTeachers();
        assertEquals(2, result.size());
    }

    @Test
    void getAllStudents_ShouldReturnListOfStudents() {
        List<Student> students = List.of(new Student(), new Student());
        when(studentService.getAllStudents()).thenReturn(students);
        List<Student> result = schoolService.getAllStudents();
        assertEquals(2, result.size());
    }

    @Test
    void removeAllData_ShouldRemoveAllEntities() {
        List<Course> courses = List.of(new Course(1L, null, null, null), new Course(2L, null, null, null));
        List<Student> students = List.of(new Student(1L, null, null), new Student(2L, null, null));
        List<Teacher> teachers = List.of(new Teacher(1L, null, null), new Teacher(2L, null, null));

        when(courseService.getAllCourses()).thenReturn(courses);
        when(studentService.getAllStudents()).thenReturn(students);
        when(teacherService.getAllTeachers()).thenReturn(teachers);

        doNothing().when(courseService).removeCourse(anyLong());
        doNothing().when(studentService).removeStudent(anyLong());
        doNothing().when(teacherService).deleteTeacher(anyLong());

        schoolService.removeAllData();

        for (Course course : courses) {
            verify(courseService).removeCourse(course.getId());
        }
        for (Student student : students) {
            verify(studentService).removeStudent(student.getId());
        }
        for (Teacher teacher : teachers) {
            verify(teacherService).deleteTeacher(teacher.getId());
        }
    }

    @Test
    void generateReport_ShouldLogCorrectCounts() {
        List<Teacher> teachers = List.of(new Teacher(), new Teacher());
        List<Student> students = List.of(new Student(), new Student(), new Student());
        List<Course> courses = List.of(new Course(), new Course(), new Course(), new Course());

        when(teacherService.getAllTeachers()).thenReturn(teachers);
        when(studentService.getAllStudents()).thenReturn(students);
        when(courseService.getAllCourses()).thenReturn(courses);

        schoolService.generateReport();

        verify(teacherService).getAllTeachers();
        verify(studentService).getAllStudents();
        verify(courseService).getAllCourses();
    }
}

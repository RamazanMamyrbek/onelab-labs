package org.ramazanmamyrbek.services;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.ramazanmamyrbek.entity.Course;
import org.ramazanmamyrbek.entity.Student;
import org.ramazanmamyrbek.entity.Teacher;
import org.ramazanmamyrbek.repository.CourseRepository;
import org.ramazanmamyrbek.repository.StudentRepository;
import org.ramazanmamyrbek.repository.TeacherRepository;
import org.ramazanmamyrbek.services.impl.CourseServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CourseServiceTest {

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private TeacherRepository teacherRepository;

    @Mock
    private StudentRepository studentRepository;

    @InjectMocks
    private CourseServiceImpl courseService;

    @Test
    void createCourse_ShouldCreateCourse_WhenTeacherExists() {
        String courseName = "Math";
        Long teacherId = 1L;
        Teacher teacher = new Teacher();
        teacher.setId(teacherId);

        when(teacherRepository.findById(teacherId)).thenReturn(Optional.of(teacher));
        when(courseRepository.save(any(Course.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Course result = courseService.createCourse(courseName, teacherId);

        assertNotNull(result);
        assertEquals(courseName, result.getCourseName());
        assertEquals(teacher, result.getTeacher());
        verify(courseRepository).save(any(Course.class));
    }

    @Test
    void createCourse_ShouldThrowException_WhenTeacherNotFound() {
        when(teacherRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> courseService.createCourse("Math", 1L));
    }

    @Test
    void addStudentToCourse_ShouldAddStudent_WhenCourseAndStudentExist() {
        Course course = new Course();
        course.setId(1L);
        Student student = new Student();
        student.setId(2L);

        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(studentRepository.findById(2L)).thenReturn(Optional.of(student));

        courseService.addStudentToCourse(1L, 2L);

        assertTrue(course.getStudents().contains(student));
        assertTrue(student.getCourses().contains(course));
        verify(courseRepository).save(course);
    }

    @Test
    void addStudentToCourse_ShouldThrowException_WhenCourseNotFound() {
        when(courseRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> courseService.addStudentToCourse(1L, 2L));
    }

    @Test
    void getAllCourses_ShouldReturnListOfCourses() {
        List<Course> courses = List.of(new Course(), new Course());
        when(courseRepository.findAll()).thenReturn(courses);
        List<Course> result = courseService.getAllCourses();
        assertEquals(2, result.size());
    }

    @Test
    void getCourseById_ShouldReturnCourse_WhenIdExists() {
        Course course = new Course();
        course.setId(1L);
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        Course result = courseService.getCourseById(1L);
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void updateCourseName_ShouldUpdateName_WhenCourseExists() {
        Course course = new Course();
        course.setId(1L);
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        courseService.updateCourseName(1L, "New Name");
        assertEquals("New Name", course.getCourseName());
        verify(courseRepository).save(course);
    }

    @Test
    void isCourseFull_ShouldReturnTrue_WhenCourseIsAtMaxCapacity() {
        Course course = new Course();
        course.setStudents(List.of(new Student(), new Student()));
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        assertTrue(courseService.isCourseFull(1L, 2));
    }

    @Test
    void removeCourse_ShouldDeleteCourseAndUnassignStudents() {
        Course course = new Course();
        course.setId(1L);
        Student student = new Student();
        student.getCourses().add(course);
        course.setStudents(List.of(student));

        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        courseService.removeCourse(1L);
        assertFalse(student.getCourses().contains(course));
        verify(courseRepository).delete(course);
    }
}
package org.ramazanmamyrbek.services;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.ramazanmamyrbek.entity.Course;
import org.ramazanmamyrbek.entity.Student;
import org.ramazanmamyrbek.repository.CourseRepository;
import org.ramazanmamyrbek.repository.StudentRepository;
import org.ramazanmamyrbek.services.impl.StudentServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private CourseRepository courseRepository;

    @InjectMocks
    private StudentServiceImpl studentService;

    @Test
    void createStudent_ShouldCreateStudent_WhenNameIsUnique() {
        String name = "John Doe";
        Student student = new Student();
        student.setName(name);

        when(studentRepository.existsByName(name)).thenReturn(false);
        when(studentRepository.save(any(Student.class))).thenReturn(student);

        Student result = studentService.createStudent(name);

        assertNotNull(result);
        assertEquals(name, result.getName());
        verify(studentRepository).save(any(Student.class));
    }

    @Test
    void createStudent_ShouldThrowException_WhenNameAlreadyExists() {
        String name = "John Doe";

        when(studentRepository.existsByName(name)).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> studentService.createStudent(name));
    }

    @Test
    void getAllStudents_ShouldReturnListOfStudents() {
        List<Student> students = List.of(new Student(), new Student());

        when(studentRepository.findAll()).thenReturn(students);

        List<Student> result = studentService.getAllStudents();

        assertEquals(2, result.size());
        verify(studentRepository).findAll();
    }

    @Test
    void getStudentById_ShouldReturnStudent_WhenIdExists() {
        Student student = new Student();
        student.setId(1L);

        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));

        Student result = studentService.getStudentById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void getStudentById_ShouldThrowException_WhenStudentNotFound() {
        when(studentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> studentService.getStudentById(1L));
    }

    @Test
    void getCoursesByStudent_ShouldReturnCourses_WhenStudentExists() {
        Student student = new Student();
        student.setId(1L);
        student.setCourses(List.of(new Course(), new Course()));

        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));

        List<Course> result = studentService.getCoursesByStudent(1L);

        assertEquals(2, result.size());
    }

    @Test
    void removeStudent_ShouldRemoveStudentAndUnassignCourses() {
        Student student = new Student();
        student.setId(1L);
        Course course = new Course();
        course.getStudents().add(student);
        student.setCourses(List.of(course));

        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));

        studentService.removeStudent(1L);

        assertFalse(course.getStudents().contains(student));
        verify(studentRepository).delete(student);
    }
}


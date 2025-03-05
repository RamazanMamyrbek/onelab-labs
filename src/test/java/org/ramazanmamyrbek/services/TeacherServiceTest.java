package org.ramazanmamyrbek.services;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.ramazanmamyrbek.entity.Course;
import org.ramazanmamyrbek.entity.Teacher;
import org.ramazanmamyrbek.repository.CourseRepository;
import org.ramazanmamyrbek.repository.TeacherRepository;
import org.ramazanmamyrbek.services.impl.TeacherServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TeacherServiceTest {

    @Mock
    private TeacherRepository teacherRepository;

    @Mock
    private CourseRepository courseRepository;

    @InjectMocks
    private TeacherServiceImpl teacherService;

    @Test
    void createTeacher_ShouldCreateTeacher_WhenNameIsUnique() {
        String name = "John Doe";
        Teacher teacher = new Teacher();
        teacher.setName(name);

        when(teacherRepository.existsByName(name)).thenReturn(false);
        when(teacherRepository.save(any(Teacher.class))).thenReturn(teacher);

        Teacher result = teacherService.createTeacher(name);

        assertNotNull(result);
        assertEquals(name, result.getName());
        verify(teacherRepository).save(any(Teacher.class));
    }

    @Test
    void createTeacher_ShouldThrowException_WhenNameAlreadyExists() {
        String name = "John Doe";

        when(teacherRepository.existsByName(name)).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> teacherService.createTeacher(name));
    }

    @Test
    void getAllTeachers_ShouldReturnListOfTeachers() {
        List<Teacher> teachers = List.of(new Teacher(), new Teacher());

        when(teacherRepository.findAll()).thenReturn(teachers);

        List<Teacher> result = teacherService.getAllTeachers();

        assertEquals(2, result.size());
        verify(teacherRepository).findAll();
    }

    @Test
    void getTeacherById_ShouldReturnTeacher_WhenIdExists() {
        Teacher teacher = new Teacher();
        teacher.setId(1L);

        when(teacherRepository.findById(1L)).thenReturn(Optional.of(teacher));

        Teacher result = teacherService.getTeacherById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void getTeacherById_ShouldThrowException_WhenTeacherNotFound() {
        when(teacherRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> teacherService.getTeacherById(1L));
    }

    @Test
    void getCoursesByTeacher_ShouldReturnCourses_WhenTeacherExists() {
        Teacher teacher = new Teacher();
        teacher.setId(1L);
        teacher.setCourses(List.of(new Course(), new Course()));

        when(teacherRepository.findById(1L)).thenReturn(Optional.of(teacher));

        List<Course> result = teacherService.getCoursesByTeacher(1L);

        assertEquals(2, result.size());
    }

    @Test
    void assignCourseToTeacher_ShouldAssignCourse_WhenTeacherAndCourseExist() {
        Teacher teacher = new Teacher();
        teacher.setId(1L);
        Course course = new Course();
        course.setId(2L);

        when(teacherRepository.findById(1L)).thenReturn(Optional.of(teacher));
        when(courseRepository.findById(2L)).thenReturn(Optional.of(course));

        teacherService.assignCourseToTeacher(1L, 2L);

        assertEquals(teacher, course.getTeacher());
        verify(courseRepository).save(course);
    }

    @Test
    void assignCourseToTeacher_ShouldThrowException_WhenTeacherNotFound() {
        when(teacherRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> teacherService.assignCourseToTeacher(1L, 2L));
    }

    @Test
    void deleteTeacher_ShouldDeleteTeacherAndUnassignCourses() {
        Teacher teacher = new Teacher();
        teacher.setId(1L);
        Course course = new Course();
        course.setTeacher(teacher);
        teacher.setCourses(List.of(course));

        when(teacherRepository.findById(1L)).thenReturn(Optional.of(teacher));

        teacherService.deleteTeacher(1L);

        assertNull(course.getTeacher());
        verify(teacherRepository).delete(teacher);
    }
}


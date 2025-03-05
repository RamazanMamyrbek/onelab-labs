package org.ramazanmamyrbek.services.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.ramazanmamyrbek.entity.Course;
import org.ramazanmamyrbek.entity.Student;
import org.ramazanmamyrbek.entity.Teacher;
import org.ramazanmamyrbek.repository.CourseRepository;
import org.ramazanmamyrbek.repository.StudentRepository;
import org.ramazanmamyrbek.repository.TeacherRepository;
import org.ramazanmamyrbek.services.CourseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CourseServiceImpl implements CourseService {
    private final CourseRepository courseRepository;
    private final TeacherRepository teacherRepository;
    private final StudentRepository studentRepository;

    @Override
    @Transactional
    public Course createCourse(String courseName, Long teacherId) {
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new EntityNotFoundException("Учитель не найден"));

        Course course = new Course();
        course.setCourseName(courseName);
        course.setTeacher(teacher);

        return courseRepository.save(course);
    }

    @Override
    @Transactional
    public void addStudentToCourse(Long courseId, Long studentId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new EntityNotFoundException("Курс не найден"));
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new EntityNotFoundException("Студент не найден"));

        if (course.getStudents().contains(student)) {
            throw new IllegalStateException("Студент уже записан на этот курс!");
        }

        course.getStudents().add(student);
        student.getCourses().add(course);

        courseRepository.save(course);
    }

    @Override
    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    @Override
    public Course getCourseById(Long courseId) {
        return courseRepository.findById(courseId)
                .orElseThrow(() -> new EntityNotFoundException("Курс не найден"));
    }

    @Override
    @Transactional
    public void updateCourseName(Long courseId, String newName) {
        Course course = getCourseById(courseId);
        course.setCourseName(newName);
        courseRepository.save(course);
    }

    @Override
    public boolean isCourseFull(Long courseId, int maxCapacity) {
        Course course = getCourseById(courseId);
        return course.getStudents().size() >= maxCapacity;
    }

    @Override
    @Transactional
    public void removeCourse(Long courseId) {
        Course course = getCourseById(courseId);
        course.getStudents().forEach(student -> student.getCourses().remove(course));
        courseRepository.delete(course);
    }
}


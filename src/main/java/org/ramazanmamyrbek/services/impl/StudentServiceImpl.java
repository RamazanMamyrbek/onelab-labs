package org.ramazanmamyrbek.services.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.ramazanmamyrbek.entity.Course;
import org.ramazanmamyrbek.entity.Student;
import org.ramazanmamyrbek.repository.CourseRepository;
import org.ramazanmamyrbek.repository.StudentRepository;
import org.ramazanmamyrbek.services.StudentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudentServiceImpl implements StudentService {
    private final StudentRepository studentRepository;

    @Override
    @Transactional
    public Student createStudent(String name) {
        if (studentRepository.existsByName(name)) {
            throw new IllegalArgumentException("Студент с таким именем уже существует!");
        }
        Student student = new Student();
        student.setName(name);
        return studentRepository.save(student);
    }

    @Override
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    @Override
    public Student getStudentById(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Студент не найден"));
    }

    @Override
    public List<Course> getCoursesByStudent(Long studentId) {
        Student student = getStudentById(studentId);
        return student.getCourses();
    }

    @Override
    @Transactional
    public void removeStudent(Long studentId) {
        Student student = getStudentById(studentId);
        student.getCourses().forEach(course -> course.getStudents().remove(student));
        studentRepository.delete(student);
    }
}


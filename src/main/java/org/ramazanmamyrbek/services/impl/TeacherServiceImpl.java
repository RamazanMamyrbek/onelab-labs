package org.ramazanmamyrbek.services.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.ramazanmamyrbek.entity.Course;
import org.ramazanmamyrbek.entity.Teacher;
import org.ramazanmamyrbek.repository.CourseRepository;
import org.ramazanmamyrbek.repository.TeacherRepository;
import org.ramazanmamyrbek.services.TeacherService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TeacherServiceImpl implements TeacherService {
    private final TeacherRepository teacherRepository;
    private final CourseRepository courseRepository;

    @Override
    @Transactional
    public Teacher createTeacher(String name) {
        if (teacherRepository.existsByName(name)) {
            throw new IllegalArgumentException("Учитель с таким именем уже существует!");
        }
        Teacher teacher = new Teacher();
        teacher.setName(name);
        return teacherRepository.save(teacher);
    }

    @Override
    public List<Teacher> getAllTeachers() {
        return teacherRepository.findAll();
    }

    @Override
    public Teacher getTeacherById(Long id) {
        return teacherRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Учитель не найден"));
    }

    @Override
    public List<Course> getCoursesByTeacher(Long teacherId) {
        Teacher teacher = getTeacherById(teacherId);
        return teacher.getCourses();
    }

    @Override
    @Transactional
    public void assignCourseToTeacher(Long teacherId, Long courseId) {
        Teacher teacher = getTeacherById(teacherId);
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new EntityNotFoundException("Курс не найден"));

        course.setTeacher(teacher);
        courseRepository.save(course);
    }

    @Override
    @Transactional
    public void deleteTeacher(Long teacherId) {
        Teacher teacher = getTeacherById(teacherId);
        teacher.getCourses().forEach(course -> course.setTeacher(null));
        teacherRepository.delete(teacher);
    }
}


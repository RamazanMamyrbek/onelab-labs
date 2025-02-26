package org.ramazanmamyrbek.services.impl;

import org.ramazanmamyrbek.entity.Course;
import org.ramazanmamyrbek.entity.Student;
import org.ramazanmamyrbek.entity.Teacher;
import org.ramazanmamyrbek.repository.CourseRepository;
import org.ramazanmamyrbek.repository.StudentRepository;
import org.ramazanmamyrbek.repository.TeacherRepository;
import org.ramazanmamyrbek.services.SchoolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SchoolServiceImpl implements SchoolService {
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final TeacherRepository teacherRepository;

    @Autowired
    public SchoolServiceImpl(StudentRepository studentRepository,
                         CourseRepository courseRepository,
                         TeacherRepository teacherRepository) {
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
        this.teacherRepository = teacherRepository;
    }

    @Override
    public void saveStudent(Student student) {
        studentRepository.save(student);
    }

    @Override
    public void saveTeacher(Teacher teacher) {
        teacherRepository.save(teacher);
    }

    @Override
    public void saveCourse(Course course) {
        courseRepository.save(course);
    }

    @Override
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    @Override
    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    @Override
    public List<Teacher> getAllTeachers() {
        return teacherRepository.findAll();
    }

    @Override
    public List<String> getStudentNamesByCourseId(Long courseId) {
        return courseRepository.findAll().stream()
                .filter(course -> course.getId().equals(courseId))
                .flatMap(course -> course.getStudents().stream())
                .map(Student::getName)
                .toList();
    }

    @Override
    public List<String> getCoursesByTeacherId(Long teacherId) {
        return teacherRepository.findAll().stream()
                .filter(teacher -> teacher.getId().equals(teacherId))
                .flatMap(teacher -> teacher.getCourses().stream())
                .map(Course::getCourseName)
                .toList();
    }
}

package org.ramazanmamyrbek.services.impl;

import org.ramazanmamyrbek.entity.Course;
import org.ramazanmamyrbek.entity.Student;
import org.ramazanmamyrbek.entity.Teacher;
import org.ramazanmamyrbek.repository.CourseRepository;
import org.ramazanmamyrbek.repository.StudentRepository;
import org.ramazanmamyrbek.repository.TeacherRepository;
import org.ramazanmamyrbek.services.SchoolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SchoolServiceImpl implements SchoolService {
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final TeacherRepository teacherRepository;
    private final JdbcTemplate jdbcTemplate;


    @Autowired
    public SchoolServiceImpl(StudentRepository studentRepository,
                         CourseRepository courseRepository,
                         TeacherRepository teacherRepository, JdbcTemplate jdbcTemplate) {
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
        this.teacherRepository = teacherRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    public void saveStudent(Student student) {
        studentRepository.save(student);
    }

    public void saveTeacher(Teacher teacher) {
        teacherRepository.save(teacher);
    }

    public void saveCourse(Course course) {
        courseRepository.save(course);
    }

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    public List<Teacher> getAllTeachers() {
        return teacherRepository.findAll();
    }

    public List<String> getStudentNamesByCourseId(Long courseId) {
        String sql = "SELECT s.name FROM student s " +
                "JOIN student_course sc ON s.id = sc.student_id " +
                "WHERE sc.course_id = ?";

        return jdbcTemplate.query(sql, new Object[]{courseId}, (rs, rowNum) -> rs.getString("name"));
    }

    public List<String> getCoursesByTeacherId(Long teacherId) {
        String sql = "SELECT c.course_name FROM course c " +
                "WHERE c.teacher_id = ?";

        return jdbcTemplate.query(sql, new Object[]{teacherId}, (rs, rowNum) -> rs.getString("course_name"));
    }
}
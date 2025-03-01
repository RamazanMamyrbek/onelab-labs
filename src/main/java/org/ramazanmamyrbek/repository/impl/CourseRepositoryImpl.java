package org.ramazanmamyrbek.repository.impl;

import org.ramazanmamyrbek.entity.Course;
import org.ramazanmamyrbek.entity.Teacher;
import org.ramazanmamyrbek.repository.CourseRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CourseRepositoryImpl implements CourseRepository {

    private final JdbcTemplate jdbcTemplate;

    public CourseRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void save(Course course) {
        String sql = "INSERT INTO course (course_name, teacher_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, course.getCourseName(), course.getTeacher().getId());
    }

    @Override
    public List<Course> findAll() {
        String sql = "SELECT * FROM course";
        return jdbcTemplate.query(sql, courseRowMapper);
    }

    private final RowMapper<Course> courseRowMapper = (rs, rowNum) -> new Course(
            rs.getLong("id"),
            rs.getString("course_name"),
            new Teacher(rs.getLong("teacher_id"), null, null), // Пока имя учителя не связываем
            null
    );
}

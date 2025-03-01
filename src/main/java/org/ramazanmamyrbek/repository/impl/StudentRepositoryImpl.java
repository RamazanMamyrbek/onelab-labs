package org.ramazanmamyrbek.repository.impl;

import org.ramazanmamyrbek.entity.Student;
import org.ramazanmamyrbek.repository.StudentRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class StudentRepositoryImpl implements StudentRepository {

    private final JdbcTemplate jdbcTemplate;

    public StudentRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void save(Student student) {
        String sql = "INSERT INTO student (name) VALUES (?)";
        jdbcTemplate.update(sql, student.getName());
    }

    @Override
    public List<Student> findAll() {
        String sql = "SELECT * FROM student";
        return jdbcTemplate.query(sql, studentRowMapper);
    }

    private final RowMapper<Student> studentRowMapper = (rs, rowNum) -> new Student(
            rs.getLong("id"),
            rs.getString("name"),
            null
    );
}
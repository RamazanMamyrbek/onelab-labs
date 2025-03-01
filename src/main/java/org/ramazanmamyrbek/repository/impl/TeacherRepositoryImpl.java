package org.ramazanmamyrbek.repository.impl;

import org.ramazanmamyrbek.entity.Teacher;
import org.ramazanmamyrbek.repository.TeacherRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TeacherRepositoryImpl implements TeacherRepository {

    private final JdbcTemplate jdbcTemplate;

    public TeacherRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void save(Teacher teacher) {
        String sql = "INSERT INTO teacher (name) VALUES (?)";
        jdbcTemplate.update(sql, teacher.getName());
    }

    @Override
    public List<Teacher> findAll() {
        String sql = "SELECT * FROM teacher";
        return jdbcTemplate.query(sql, teacherRowMapper);
    }

    private final RowMapper<Teacher> teacherRowMapper = (rs, rowNum) -> new Teacher(
            rs.getLong("id"),
            rs.getString("name"),
            null
    );
}
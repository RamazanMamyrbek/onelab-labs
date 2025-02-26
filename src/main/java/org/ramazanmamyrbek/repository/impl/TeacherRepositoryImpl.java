package org.ramazanmamyrbek.repository.impl;

import org.ramazanmamyrbek.entity.Teacher;
import org.ramazanmamyrbek.repository.TeacherRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class TeacherRepositoryImpl implements TeacherRepository {
    private final List<Teacher> teachers = new ArrayList<>();

    @Override
    public void save(Teacher teacher) {
        teachers.add(teacher);
    }

    @Override
    public List<Teacher> findAll() {
        return teachers;
    }
}

package org.ramazanmamyrbek.repository.impl;

import org.ramazanmamyrbek.entity.Student;
import org.ramazanmamyrbek.repository.StudentRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class StudentRepositoryImpl implements StudentRepository {
    private final List<Student> students = new ArrayList<>();

    @Override
    public void save(Student student) {
        students.add(student);
    }

    @Override
    public List<Student> findAll() {
        return students;
    }
}

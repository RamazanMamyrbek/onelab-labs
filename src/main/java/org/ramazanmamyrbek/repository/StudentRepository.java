package org.ramazanmamyrbek.repository;

import org.ramazanmamyrbek.entity.Student;

import java.util.List;

public interface StudentRepository {
    void save(Student student);
    List<Student> findAll();
}
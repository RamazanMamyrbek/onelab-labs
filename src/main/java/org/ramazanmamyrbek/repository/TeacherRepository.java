package org.ramazanmamyrbek.repository;

import org.ramazanmamyrbek.entity.Teacher;

import java.util.List;

public interface TeacherRepository {
    void save(Teacher teacher);
    List<Teacher> findAll();
}

package org.ramazanmamyrbek.repository;

import org.ramazanmamyrbek.entity.Course;

import java.util.List;

public interface CourseRepository {
    void save(Course course);
    List<Course> findAll();
}
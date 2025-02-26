package org.ramazanmamyrbek.repository.impl;

import org.ramazanmamyrbek.entity.Course;
import org.ramazanmamyrbek.repository.CourseRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class CourseRepositoryImpl implements CourseRepository {
    private final List<Course> courses = new ArrayList<>();

    @Override
    public void save(Course course) {
        courses.add(course);
    }

    @Override
    public List<Course> findAll() {
        return courses;
    }
}

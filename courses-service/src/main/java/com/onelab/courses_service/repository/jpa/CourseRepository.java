package com.onelab.courses_service.repository.jpa;

import com.onelab.courses_service.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findAllByTeacherId(Long teacherId);
}

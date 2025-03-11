package com.onelab.courses_service.repository.jpa;

import com.onelab.courses_service.entity.Course;
import com.onelab.courses_service.entity.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LessonRepository extends JpaRepository<Lesson, Long> {
    List<Lesson> findByCourse(Course course);
}

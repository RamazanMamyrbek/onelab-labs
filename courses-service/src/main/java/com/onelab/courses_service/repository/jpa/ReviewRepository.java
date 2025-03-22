package com.onelab.courses_service.repository.jpa;

import com.onelab.courses_service.entity.Course;
import com.onelab.courses_service.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findAllByCourse_Id(Long courseId);

    boolean existsByCourseAndUserId(Course course, Long userId);
}

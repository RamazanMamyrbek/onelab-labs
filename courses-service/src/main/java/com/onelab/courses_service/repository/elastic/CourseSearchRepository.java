package com.onelab.courses_service.repository.elastic;

import com.onelab.courses_service.entity.Course;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseSearchRepository extends ElasticsearchRepository<Course, Long> {
    List<Course> findByNameContainingIgnoreCase(String name);
}

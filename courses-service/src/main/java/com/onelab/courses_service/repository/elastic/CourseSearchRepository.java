package com.onelab.courses_service.repository.elastic;

import com.onelab.courses_service.entity.elastic.CourseIndex;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseSearchRepository extends ElasticsearchRepository<CourseIndex, Long> {
    Page<CourseIndex> findByNameContainingIgnoreCase(String name, Pageable pageRequest);
    List<CourseIndex> findByNameContainingIgnoreCase(String name);
}

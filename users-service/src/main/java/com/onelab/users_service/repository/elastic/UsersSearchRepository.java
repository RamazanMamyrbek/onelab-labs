package com.onelab.users_service.repository.elastic;

import com.onelab.users_service.entity.elastic.UsersIndex;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UsersSearchRepository extends ElasticsearchRepository<UsersIndex, Long> {
    List<UsersIndex> findAllByNameContainingIgnoreCase(String nameQuery);

    Page<UsersIndex> findAllByNameContainingIgnoreCase(String nameQuery, Pageable pageRequest);
}

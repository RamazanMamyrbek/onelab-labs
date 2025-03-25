package org.onelab.reviewcheckerservice.repository;

import org.onelab.reviewcheckerservice.entity.ForbiddenWord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ForbiddenWordRepository extends JpaRepository<ForbiddenWord, Long> {
    boolean existsByWordContainingIgnoreCase(String word);
}

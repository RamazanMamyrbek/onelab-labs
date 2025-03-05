package org.ramazanmamyrbek.repository;

import org.ramazanmamyrbek.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    boolean existsByName(String name);
}
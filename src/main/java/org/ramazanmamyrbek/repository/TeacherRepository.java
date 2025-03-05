package org.ramazanmamyrbek.repository;

import org.ramazanmamyrbek.entity.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Long> {

    boolean existsByName(String name);
}

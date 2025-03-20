package com.onelab.users_service.repository.jpa;

import com.onelab.users_service.entity.Users;
import org.onelab.common.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Users, Long> {
    List<Users> findAllByRole(Role role);

    Optional<Users> findByEmail(String email);

    boolean existsByEmail(String email);

    @Modifying
    @Query(value = "DELETE FROM user_courses WHERE user_id IN (SELECT id FROM users WHERE role = 'ROLE_STUDENT') AND course_id = :courseId",
    nativeQuery = true)
    void removeCourseFromStudents(Long courseId);
}

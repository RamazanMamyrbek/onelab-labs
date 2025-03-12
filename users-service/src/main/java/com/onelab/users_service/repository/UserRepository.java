package com.onelab.users_service.repository;

import com.onelab.users_service.entity.Users;
import org.onelab.common.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Users, Long> {
    List<Users> findAllByRole(Role role);

    Optional<Users> findByEmail(String email);

    boolean existsByEmail(String email);
}

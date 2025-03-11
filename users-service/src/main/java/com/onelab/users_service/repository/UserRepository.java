package com.onelab.users_service.repository;

import com.onelab.users_service.entity.Users;
import com.onelab.users_service.entity.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<Users, Long> {
    List<Users> findAllByRole(Role role);
}

package com.zorvyn.assignment.repositories;

import com.zorvyn.assignment.entities.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsersRepository extends JpaRepository<Users, Integer> {
    Optional<Users> findByEmailId(String emailId);
    boolean existsByEmailId(String emailId);
}

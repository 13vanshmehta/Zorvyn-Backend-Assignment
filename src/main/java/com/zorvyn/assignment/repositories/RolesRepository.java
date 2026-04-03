package com.zorvyn.assignment.repositories;

import com.zorvyn.assignment.entities.UserRoles;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RolesRepository extends JpaRepository<UserRoles, Integer> {
    Optional<UserRoles> findByRole(String role);
}
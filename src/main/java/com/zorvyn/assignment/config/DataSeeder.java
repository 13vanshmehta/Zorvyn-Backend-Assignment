package com.zorvyn.assignment.config;

import com.zorvyn.assignment.entities.UserRoles;
import com.zorvyn.assignment.entities.Users;
import com.zorvyn.assignment.enums.UserStatus;
import com.zorvyn.assignment.repositories.RolesRepository;
import com.zorvyn.assignment.repositories.UsersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final RolesRepository rolesRepository;
    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        // Seed system roles only if they don't exist yet
        UserRoles viewerRole = getOrCreateRole("VIEWER", "Can only view dashboard data");
        UserRoles analystRole = getOrCreateRole("ANALYST", "Can view records and access insights");
        UserRoles adminRole = getOrCreateRole("ADMIN", "Can create, update, and manage records and users");

        // Seed admin account only if it doesn't exist yet
        String adminEmail = "admin@zorvyn.com";
        if (usersRepository.findByEmailId(adminEmail).isEmpty()) {
            Users admin = Users.builder()
                    .firstName("Admin")
                    .lastName("Zorvyn")
                    .emailId(adminEmail)
                    .password(passwordEncoder.encode("Admin@123"))
                    .status(UserStatus.ACTIVE)
                    .build();

            admin.setUserRoles(Set.of(adminRole, analystRole, viewerRole));
            usersRepository.save(admin);
            log.info("Admin account created — {}", adminEmail);
        }
    }

    private UserRoles getOrCreateRole(String name, String description) {
        return rolesRepository.findByRole(name)
                .orElseGet(() -> rolesRepository.save(
                        UserRoles.builder().role(name).description(description).build()
                ));
    }
}

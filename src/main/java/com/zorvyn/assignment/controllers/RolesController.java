package com.zorvyn.assignment.controllers;

import com.zorvyn.assignment.dtos.RolesDTO;
import com.zorvyn.assignment.entities.UserRoles;
import com.zorvyn.assignment.responses.ApiResponse;
import com.zorvyn.assignment.services.RolesServiceInterface;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("${basePath}/roles")
@Tag(name = "Role Management", description = "CRUD operations for roles (Admin only)")
public class RolesController {
    private final RolesServiceInterface rolesService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a role", description = "Admin only — create a new role")
    public ResponseEntity<ApiResponse> createRole(@Valid @RequestBody RolesDTO dto) {
        UserRoles role = rolesService.createRole(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.builder()
                        .message("Role created successfully")
                        .statusCode(HttpStatus.CREATED)
                        .data(role)
                        .build());
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all roles", description = "Admin only — list all available roles")
    public ResponseEntity<ApiResponse> getAllRoles() {
        List<UserRoles> roles = rolesService.getAllRoles();
        return ResponseEntity.ok(ApiResponse.builder()
                .message("All roles retrieved")
                .statusCode(HttpStatus.OK)
                .data(roles)
                .build());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get role by ID", description = "Admin only — retrieve a specific role")
    public ResponseEntity<ApiResponse> getRoleById(@PathVariable int id) {
        UserRoles role = rolesService.getRoleById(id);
        return ResponseEntity.ok(ApiResponse.builder()
                .message("Role retrieved")
                .statusCode(HttpStatus.OK)
                .data(role)
                .build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete a role", description = "Admin only — delete a role by ID")
    public ResponseEntity<ApiResponse> deleteRole(@PathVariable int id) {
        rolesService.deleteRole(id);
        return ResponseEntity.ok(ApiResponse.builder()
                .message("Role deleted successfully")
                .statusCode(HttpStatus.OK)
                .data(null)
                .build());
    }
}

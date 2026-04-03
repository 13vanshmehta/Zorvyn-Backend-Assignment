package com.zorvyn.assignment.controllers;

import com.zorvyn.assignment.dtos.*;
import com.zorvyn.assignment.responses.ApiResponse;
import com.zorvyn.assignment.services.UsersServiceInterface;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("${basePath}/users")
@Tag(name = "User Management", description = "CRUD operations for users (role-restricted)")
public class UsersController {

    private final UsersServiceInterface usersService;

    // ── Current User ──

    @GetMapping("/me")
    @Operation(summary = "Get current user profile", description = "Returns the authenticated user's profile")
    public ResponseEntity<ApiResponse> getCurrentUser(Authentication authentication) {
        UsersDTO user = usersService.getCurrentUser(authentication.getName());
        return ResponseEntity.ok(ApiResponse.builder()
                .message("Current user retrieved")
                .statusCode(HttpStatus.OK)
                .data(user)
                .build());
    }

    // ── Admin-Only User Management ──

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all users", description = "Admin only — lists all registered users")
    public ResponseEntity<ApiResponse> getAllUsers() {
        List<UsersDTO> users = usersService.getAllUsers();
        return ResponseEntity.ok(ApiResponse.builder()
                .message("All users retrieved")
                .statusCode(HttpStatus.OK)
                .data(users)
                .build());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get user by ID", description = "Admin only — retrieve a specific user by ID")
    public ResponseEntity<ApiResponse> getUserById(@PathVariable int id) {
        UsersDTO user = usersService.getUserById(id);
        return ResponseEntity.ok(ApiResponse.builder()
                .message("User retrieved")
                .statusCode(HttpStatus.OK)
                .data(user)
                .build());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update user", description = "Admin only — update a user's profile information")
    public ResponseEntity<ApiResponse> updateUser(@PathVariable int id, @RequestBody UpdateUserDTO dto) {
        UsersDTO user = usersService.updateUser(id, dto);
        return ResponseEntity.ok(ApiResponse.builder()
                .message("User updated successfully")
                .statusCode(HttpStatus.OK)
                .data(user)
                .build());
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update user status", description = "Admin only — activate or deactivate a user account")
    public ResponseEntity<ApiResponse> updateUserStatus(
            @PathVariable int id,
            @Valid @RequestBody UpdateStatusDTO dto) {
        UsersDTO user = usersService.updateUserStatus(id, dto);
        return ResponseEntity.ok(ApiResponse.builder()
                .message("User status updated to " + dto.getStatus())
                .statusCode(HttpStatus.OK)
                .data(user)
                .build());
    }

    @PostMapping("/{id}/roles")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Assign role to user", description = "Admin only — add a role to a user")
    public ResponseEntity<ApiResponse> assignRole(
            @PathVariable int id,
            @Valid @RequestBody AssignRoleDTO dto) {
        UsersDTO user = usersService.assignRole(id, dto);
        return ResponseEntity.ok(ApiResponse.builder()
                .message("Role '" + dto.getRoleName() + "' assigned to user")
                .statusCode(HttpStatus.OK)
                .data(user)
                .build());
    }

    @DeleteMapping("/{id}/roles/{roleName}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Remove role from user", description = "Admin only — remove a role from a user")
    public ResponseEntity<ApiResponse> removeRole(
            @PathVariable int id,
            @PathVariable String roleName) {
        UsersDTO user = usersService.removeRole(id, roleName);
        return ResponseEntity.ok(ApiResponse.builder()
                .message("Role '" + roleName + "' removed from user")
                .statusCode(HttpStatus.OK)
                .data(user)
                .build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete user", description = "Admin only — permanently delete a user")
    public ResponseEntity<ApiResponse> deleteUser(@PathVariable int id) {
        usersService.deleteUser(id);
        return ResponseEntity.ok(ApiResponse.builder()
                .message("User deleted successfully")
                .statusCode(HttpStatus.OK)
                .data(null)
                .build());
    }
}

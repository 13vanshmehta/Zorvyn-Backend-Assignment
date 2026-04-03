package com.zorvyn.assignment.controllers;

import com.zorvyn.assignment.dtos.AuthResponseDTO;
import com.zorvyn.assignment.dtos.LoginRequestDTO;
import com.zorvyn.assignment.dtos.RegisterRequestDTO;
import com.zorvyn.assignment.responses.ApiResponse;
import com.zorvyn.assignment.services.AuthServiceInterface;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("${basePath}/auth")
@Tag(name = "Authentication", description = "Register and login endpoints")
public class AuthController {

    private final AuthServiceInterface authService;

    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Creates a new user with the default VIEWER role")
    public ResponseEntity<ApiResponse> register(@Valid @RequestBody RegisterRequestDTO request) {
        AuthResponseDTO result = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.builder()
                        .message("User registered successfully")
                        .statusCode(HttpStatus.CREATED)
                        .data(result)
                        .build());
    }

    @PostMapping("/login")
    @Operation(summary = "Login", description = "Authenticate with email and password to receive a JWT token")
    public ResponseEntity<ApiResponse> login(@Valid @RequestBody LoginRequestDTO request) {
        AuthResponseDTO result = authService.login(request);
        return ResponseEntity.ok(
                ApiResponse.builder()
                        .message("Login successful")
                        .statusCode(HttpStatus.OK)
                        .data(result)
                        .build());
    }
}

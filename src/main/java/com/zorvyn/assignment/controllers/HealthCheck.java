package com.zorvyn.assignment.controllers;

import com.zorvyn.assignment.responses.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${basePath}/health-check")
public class HealthCheck {

    @GetMapping("")
    public ResponseEntity<ApiResponse> health() {
        ApiResponse response = ApiResponse.builder()
                .message("Server is running up!")
                .statusCode(HttpStatus.OK)
                .data(null)
                .build();

        return ResponseEntity.ok(response);
    }
}

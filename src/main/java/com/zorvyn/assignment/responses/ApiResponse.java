package com.zorvyn.assignment.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ApiResponse {
    private String message;
    private HttpStatus statusCode;
    private Object data;
}

package com.zorvyn.assignment.services;

import com.zorvyn.assignment.dtos.AuthResponseDTO;
import com.zorvyn.assignment.dtos.LoginRequestDTO;
import com.zorvyn.assignment.dtos.RegisterRequestDTO;

public interface AuthServiceInterface {
    AuthResponseDTO register(RegisterRequestDTO request);
    AuthResponseDTO login(LoginRequestDTO request);
}

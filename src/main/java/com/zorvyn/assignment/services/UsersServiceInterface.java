package com.zorvyn.assignment.services;

import com.zorvyn.assignment.dtos.*;
import com.zorvyn.assignment.enums.UserStatus;

import java.util.List;

public interface UsersServiceInterface {
    List<UsersDTO> getAllUsers();
    UsersDTO getUserById(int userId);
    UsersDTO getCurrentUser(String email);
    UsersDTO updateUser(int userId, UpdateUserDTO dto);
    UsersDTO updateUserStatus(int userId, UpdateStatusDTO dto);
    UsersDTO assignRole(int userId, AssignRoleDTO dto);
    UsersDTO removeRole(int userId, String roleName);
    void deleteUser(int userId);
}

package com.zorvyn.assignment.services;

import com.zorvyn.assignment.dtos.RolesDTO;
import com.zorvyn.assignment.entities.UserRoles;
import com.zorvyn.assignment.exceptions.DuplicateResourceException;
import com.zorvyn.assignment.exceptions.ResourceNotFoundException;

import java.util.List;

public interface RolesServiceInterface {
    UserRoles createRole(RolesDTO dto);
    List<UserRoles> getAllRoles();
    UserRoles getRoleById(int roleId);
    void deleteRole(int roleId);
}
package com.zorvyn.assignment.services;

import com.zorvyn.assignment.dtos.RolesDTO;
import com.zorvyn.assignment.entities.UserRoles;
import com.zorvyn.assignment.exceptions.DuplicateResourceException;
import com.zorvyn.assignment.exceptions.ResourceNotFoundException;
import com.zorvyn.assignment.repositories.RolesRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class RolesService implements RolesServiceInterface {
    private final RolesRepository rolesRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public UserRoles createRole(RolesDTO dto) {
        rolesRepository.findByRole(dto.getRole().toUpperCase())
                .ifPresent(role -> {
                    throw new DuplicateResourceException("Role '" + dto.getRole() + "' already exists");
                });

        UserRoles role = UserRoles.builder()
                .role(dto.getRole().toUpperCase())
                .description(dto.getDescription())
                .build();

        return rolesRepository.save(role);
    }

    @Override
    public List<UserRoles> getAllRoles() {
        return rolesRepository.findAll();
    }

    @Override
    public UserRoles getRoleById(int roleId) {
        return rolesRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + roleId));
    }

    @Override
    @Transactional
    public void deleteRole(int roleId) {
        UserRoles role = rolesRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + roleId));
        rolesRepository.delete(role);
    }
}

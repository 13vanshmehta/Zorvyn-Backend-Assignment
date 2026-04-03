package com.zorvyn.assignment.services;

import com.zorvyn.assignment.dtos.*;
import com.zorvyn.assignment.entities.UserRoles;
import com.zorvyn.assignment.entities.Users;
import com.zorvyn.assignment.enums.UserStatus;
import com.zorvyn.assignment.exceptions.ResourceNotFoundException;
import com.zorvyn.assignment.repositories.RolesRepository;
import com.zorvyn.assignment.repositories.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class UsersService implements UsersServiceInterface {

    private final UsersRepository usersRepository;
    private final RolesRepository rolesRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<UsersDTO> getAllUsers() {
        return usersRepository.findAll()
                .stream()
                .map(user -> modelMapper.map(user, UsersDTO.class))
                .toList();
    }

    @Override
    public UsersDTO getUserById(int userId) {
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        return modelMapper.map(user, UsersDTO.class);
    }

    @Override
    public UsersDTO getCurrentUser(String email) {
        Users user = usersRepository.findByEmailId(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        return modelMapper.map(user, UsersDTO.class);
    }

    @Override
    @Transactional
    public UsersDTO updateUser(int userId, UpdateUserDTO dto) {
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        if (dto.getFirstName() != null && !dto.getFirstName().isBlank()) {
            user.setFirstName(dto.getFirstName());
        }
        if (dto.getLastName() != null && !dto.getLastName().isBlank()) {
            user.setLastName(dto.getLastName());
        }
        if (dto.getGender() != null && !dto.getGender().isBlank()) {
            user.setGender(dto.getGender());
        }

        Users saved = usersRepository.save(user);
        return modelMapper.map(saved, UsersDTO.class);
    }

    @Override
    @Transactional
    public UsersDTO updateUserStatus(int userId, UpdateStatusDTO dto) {
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        user.setStatus(dto.getStatus());
        Users saved = usersRepository.save(user);
        return modelMapper.map(saved, UsersDTO.class);
    }

    @Override
    @Transactional
    public UsersDTO assignRole(int userId, AssignRoleDTO dto) {
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        UserRoles role = rolesRepository.findByRole(dto.getRoleName().toUpperCase())
                .orElseThrow(() -> new ResourceNotFoundException("Role not found: " + dto.getRoleName()));

        user.getUserRoles().add(role);
        Users saved = usersRepository.save(user);
        return modelMapper.map(saved, UsersDTO.class);
    }

    @Override
    @Transactional
    public UsersDTO removeRole(int userId, String roleName) {
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        UserRoles role = rolesRepository.findByRole(roleName.toUpperCase())
                .orElseThrow(() -> new ResourceNotFoundException("Role not found: " + roleName));

        user.getUserRoles().remove(role);
        Users saved = usersRepository.save(user);
        return modelMapper.map(saved, UsersDTO.class);
    }

    @Override
    @Transactional
    public void deleteUser(int userId) {
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        usersRepository.delete(user);
    }
}

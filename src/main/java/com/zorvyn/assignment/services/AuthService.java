package com.zorvyn.assignment.services;

import com.zorvyn.assignment.dtos.AuthResponseDTO;
import com.zorvyn.assignment.dtos.LoginRequestDTO;
import com.zorvyn.assignment.dtos.RegisterRequestDTO;
import com.zorvyn.assignment.dtos.UsersDTO;
import com.zorvyn.assignment.entities.UserRoles;
import com.zorvyn.assignment.entities.Users;
import com.zorvyn.assignment.enums.UserStatus;
import com.zorvyn.assignment.exceptions.DuplicateResourceException;
import com.zorvyn.assignment.exceptions.ResourceNotFoundException;
import com.zorvyn.assignment.repositories.RolesRepository;
import com.zorvyn.assignment.repositories.UsersRepository;
import com.zorvyn.assignment.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class AuthService implements AuthServiceInterface {

    private final UsersRepository usersRepository;
    private final RolesRepository rolesRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public AuthResponseDTO register(RegisterRequestDTO request) {
        if (usersRepository.existsByEmailId(request.getEmailId())) {
            throw new DuplicateResourceException("Email '" + request.getEmailId() + "' is already registered");
        }

        // Default role is VIEWER
        UserRoles viewerRole = rolesRepository.findByRole("VIEWER")
                .orElseThrow(() -> new ResourceNotFoundException("Default role VIEWER not found. Seed roles first."));

        Users user = Users.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .emailId(request.getEmailId())
                .password(passwordEncoder.encode(request.getPassword()))
                .gender(request.getGender())
                .status(UserStatus.ACTIVE)
                .build();

        user.getUserRoles().add(viewerRole);
        Users savedUser = usersRepository.save(user);

        String token = jwtUtil.generateToken(savedUser);
        UsersDTO userDTO = modelMapper.map(savedUser, UsersDTO.class);

        return AuthResponseDTO.builder()
                .token(token)
                .tokenType("Bearer")
                .user(userDTO)
                .build();
    }

    @Override
    public AuthResponseDTO login(LoginRequestDTO request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmailId(),
                        request.getPassword()
                )
        );

        Users user = (Users) authentication.getPrincipal();
        String token = jwtUtil.generateToken(user);
        UsersDTO userDTO = modelMapper.map(user, UsersDTO.class);

        return AuthResponseDTO.builder()
                .token(token)
                .tokenType("Bearer")
                .user(userDTO)
                .build();
    }
}

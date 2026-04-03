package com.zorvyn.assignment.dtos;

import com.zorvyn.assignment.enums.UserStatus;
import lombok.*;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UsersDTO {
    private int id;
    private String firstName;
    private String lastName;
    private String emailId;
    private String gender;
    private UserStatus status;
    @Builder.Default
    private Set<RolesDTO> userRoles = new HashSet<>();
    private Instant createdAt;
    private Instant updatedAt;
}

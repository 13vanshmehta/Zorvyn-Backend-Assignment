package com.zorvyn.assignment.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssignRoleDTO {
    @NotBlank(message = "Role name is required")
    private String roleName;
}

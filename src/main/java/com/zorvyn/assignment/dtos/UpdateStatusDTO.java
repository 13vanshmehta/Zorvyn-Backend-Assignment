package com.zorvyn.assignment.dtos;

import com.zorvyn.assignment.enums.UserStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateStatusDTO {
    @NotNull(message = "Status is required (ACTIVE or INACTIVE)")
    private UserStatus status;
}

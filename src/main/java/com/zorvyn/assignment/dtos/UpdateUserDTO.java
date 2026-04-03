package com.zorvyn.assignment.dtos;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateUserDTO {
    private String firstName;
    private String lastName;
    private String gender;
}

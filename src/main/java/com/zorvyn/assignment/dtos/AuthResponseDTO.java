package com.zorvyn.assignment.dtos;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponseDTO {
    private String token;
    private String tokenType = "Bearer";
    private UsersDTO user;
}

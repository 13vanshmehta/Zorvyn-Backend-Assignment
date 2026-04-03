package com.zorvyn.assignment.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "roles")
public class UserRoles {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private int id;

    @Column(nullable = false, unique = true)
    private String role;

    private String description;

    @JsonIgnore
    @ManyToMany(mappedBy = "userRoles")
    @Builder.Default
    private Set<Users> users = new HashSet<>();
}

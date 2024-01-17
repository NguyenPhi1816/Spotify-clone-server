package com.khaphi.spotifycloneserver.request;

import com.khaphi.spotifycloneserver.enums.Gender;
import com.khaphi.spotifycloneserver.enums.Role;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private LocalDateTime createAt;
    private Gender gender;
    private Role role;
}

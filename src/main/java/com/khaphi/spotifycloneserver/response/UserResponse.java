package com.khaphi.spotifycloneserver.response;

import com.khaphi.spotifycloneserver.enums.Gender;
import com.khaphi.spotifycloneserver.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    private Integer id;
    private String firstName;
    private String lastName;
    private LocalDateTime createAt;
    private Gender gender;
    private Role role;
}

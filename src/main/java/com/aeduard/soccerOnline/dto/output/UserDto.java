package com.aeduard.soccerOnline.dto.output;

import com.aeduard.soccerOnline.model.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class UserDto {
    private String email;
    private UserRole role;
}
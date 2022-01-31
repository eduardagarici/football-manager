package com.aeduard.soccerOnline.representation.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Getter
@NoArgsConstructor
public class AuthRequest {
    @NotEmpty
    @Email(message = "invalid")
    private String email;

    @NotEmpty
    @Size(min = 8, message = "should contain at least 8 characters")
    private String password;
}

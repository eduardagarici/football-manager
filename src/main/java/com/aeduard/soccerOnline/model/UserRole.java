package com.aeduard.soccerOnline.model;


import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum UserRole {
    USER("USER"),
    ADMIN("ADMIN");

    private final String role;
}

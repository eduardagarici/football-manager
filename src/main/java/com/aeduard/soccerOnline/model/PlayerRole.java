package com.aeduard.soccerOnline.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum PlayerRole {
    GK("Goalkeeper"),
    DEF("Defender"),
    MID("Midfielder"),
    AT("Attacker");

    private final String description;
}

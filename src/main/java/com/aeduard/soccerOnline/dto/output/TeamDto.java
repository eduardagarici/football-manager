package com.aeduard.soccerOnline.dto.output;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
public class TeamDto {

    private Long id;
    private String name;
    private String country;
    private BigDecimal totalValue;
    private BigDecimal activeBudget;
    private String referenceCurrency;
    private Long ownerId;
    private List<PlayerDto> players;
}

package com.aeduard.soccerOnline.dto.output;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class PlayerDto {

    private Long id;
    private String firstName;
    private String lastName;
    private String country;
    private Integer age;
    private BigDecimal marketValue;
    private String currencyValue;
    private String playerRole;
    private Long teamId;

}

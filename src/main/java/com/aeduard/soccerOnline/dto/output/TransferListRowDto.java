package com.aeduard.soccerOnline.dto.output;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@AllArgsConstructor
@Data
public class TransferListRowDto {
    private Long playerId;
    private String firstName;
    private String lastName;
    private Integer age;
    private Long teamId;
    private BigDecimal transferPrice;
    private String transferCurrency;
    private LocalDateTime createdOn;
}

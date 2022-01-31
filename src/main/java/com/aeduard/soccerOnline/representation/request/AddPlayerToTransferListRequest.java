package com.aeduard.soccerOnline.representation.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

import static com.aeduard.soccerOnline.util.Constants.DEFAULT_CURRENCY;
import static com.aeduard.soccerOnline.util.Constants.ID_GE_THAN_0;
import static com.aeduard.soccerOnline.util.Constants.TRANSFER_PRICE_GE_THEN_0;

@Getter
@NoArgsConstructor
public class AddPlayerToTransferListRequest {
    @NotNull
    @Min(value = 0, message = ID_GE_THAN_0)
    private Long playerId;

    @NotNull
    @Min(value = 0, message = TRANSFER_PRICE_GE_THEN_0)
    private BigDecimal transferPrice;
    
    @Size(min = 3, max = 3)
    private String transferCurrency = DEFAULT_CURRENCY; //default value
}

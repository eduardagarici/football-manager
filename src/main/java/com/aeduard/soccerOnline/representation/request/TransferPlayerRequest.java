package com.aeduard.soccerOnline.representation.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import static com.aeduard.soccerOnline.util.Constants.ID_GE_THAN_0;

@NoArgsConstructor
@Getter
public class TransferPlayerRequest {

    @NotNull
    @Min(value = 0, message = ID_GE_THAN_0)
    private Long playerId;

    @NotNull
    @Min(value = 0, message = ID_GE_THAN_0)
    private Long nextTeamId;
}

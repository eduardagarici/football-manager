package com.aeduard.soccerOnline.dto.input;

import com.aeduard.soccerOnline.util.Constants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import static com.aeduard.soccerOnline.util.Constants.ID_GE_THAN_0;
import static com.aeduard.soccerOnline.util.Constants.ONLY_LETTERS_FIELD;
import static com.aeduard.soccerOnline.util.Constants.ONLY_LETTERS_REGXP;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PatchTeamDto {

    @Min(value = 0, message = ID_GE_THAN_0)
    private Long id;
    @Size(min = 3, max = 255, message = Constants.DEFAULT_SIZE)
    @Pattern(regexp = ONLY_LETTERS_REGXP, message = ONLY_LETTERS_FIELD)
    private String name;
    @Size(min = 3, max = 255, message = Constants.DEFAULT_SIZE)
    @Pattern(regexp = ONLY_LETTERS_REGXP, message = ONLY_LETTERS_FIELD)
    private String country;
}

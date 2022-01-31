package com.aeduard.soccerOnline.dto.input;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import static com.aeduard.soccerOnline.util.Constants.ID_GE_THAN_0;
import static com.aeduard.soccerOnline.util.Constants.ONLY_LETTERS_REGXP;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PatchPlayerDto {

    @Min(value = 0, message = ID_GE_THAN_0)
    private Long id;
    @Size(min = 3, max = 255)
    @Pattern(regexp = ONLY_LETTERS_REGXP)
    private String firstName;
    @Pattern(regexp = ONLY_LETTERS_REGXP)
    @Size(min = 3, max = 255)
    private String lastName;
    @Pattern(regexp = ONLY_LETTERS_REGXP)
    @Size(min = 3, max = 255)
    private String country;
}

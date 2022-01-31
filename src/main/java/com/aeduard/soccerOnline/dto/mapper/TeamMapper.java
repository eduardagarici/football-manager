package com.aeduard.soccerOnline.dto.mapper;

import com.aeduard.soccerOnline.dto.input.PatchTeamDto;
import com.aeduard.soccerOnline.model.Team;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")

public interface TeamMapper {

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateTeamFromPatchDto(PatchTeamDto dto, @MappingTarget Team entity);
}

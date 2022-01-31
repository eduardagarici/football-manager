package com.aeduard.soccerOnline.dto.mapper;

import com.aeduard.soccerOnline.dto.input.PatchPlayerDto;
import com.aeduard.soccerOnline.model.Player;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface PlayerMapper {

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updatePlayerFromPatchDto(PatchPlayerDto dto, @MappingTarget Player entity);
}
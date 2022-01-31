package com.aeduard.soccerOnline.controller;

import com.aeduard.soccerOnline.dto.input.PatchPlayerDto;
import com.aeduard.soccerOnline.dto.output.PlayerDto;
import com.aeduard.soccerOnline.exception.BusinessException;
import com.aeduard.soccerOnline.service.PlayerService;
import com.aeduard.soccerOnline.util.Paths;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@RestController
@RequestMapping(Paths.PLAYERS)
@AllArgsConstructor
@Slf4j
public class PlayerController {

    private final PlayerService playerService;

    @GetMapping(value = "/{id}")
    public ResponseEntity<PlayerDto> getPlayerById(@PathVariable("id") @Valid @Min(0) Long playerId){
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("getPlayerById() - user: {}, playerId: {}", userEmail, playerId);
        return ResponseEntity.ok().body(playerService.getPlayerById(userEmail, playerId));
    }

    @PatchMapping(value = "/{id}")
    public ResponseEntity<PlayerDto> patchPlayerById(@Min(0) @PathVariable("id") Long playerId, @Valid @RequestBody  PatchPlayerDto patchPlayerDto){
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("patchPlayerById() - user: {}, playerId: {}", userEmail, playerId);
        if(playerId != patchPlayerDto.getId()){
            log.error("patchPlayerById: Id in the url do not match the one in the body");
            throw new BusinessException(HttpStatus.BAD_REQUEST.toString(), "Invalid id in request body", HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok().body(playerService.updatePlayerById(userEmail, playerId, patchPlayerDto));
    }

}

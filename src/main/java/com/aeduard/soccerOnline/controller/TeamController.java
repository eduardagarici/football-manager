package com.aeduard.soccerOnline.controller;

import com.aeduard.soccerOnline.dto.input.PatchTeamDto;
import com.aeduard.soccerOnline.dto.output.TeamDto;
import com.aeduard.soccerOnline.exception.BusinessException;
import com.aeduard.soccerOnline.service.TeamService;
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
import java.security.Principal;

@RestController
@RequestMapping(Paths.TEAMS)
@AllArgsConstructor
@Slf4j
public class TeamController {

    private final TeamService teamService;

    @GetMapping
    public ResponseEntity<TeamDto> getUserTeam(Principal principal){
        return ResponseEntity.ok().body(teamService.getUserTeam(principal.getName()));
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<TeamDto> getTeamById(@PathVariable("id") @Min(0) Long teamId){
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("getTeamById() - userId : {}, teamId : {}", userEmail, teamId);
        return ResponseEntity.ok().body(teamService.getTeamById(userEmail, teamId));
    }


    @PatchMapping(value = "/{id}")
    public ResponseEntity<TeamDto> updateTeamById(@PathVariable("id") @Min(0) Long teamId, @Valid @RequestBody PatchTeamDto patchTeamDto){
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("updateTeamById() - userId : {}, teamId : {}", userEmail, teamId);
        if(teamId != patchTeamDto.getId()){
            log.error("updateTeamById: Id in the url do not match the one in the body");
            throw new BusinessException(HttpStatus.BAD_REQUEST.toString(), "Invalid id in request body", HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.status(HttpStatus.OK).body(teamService.updateTeamById(userEmail, teamId, patchTeamDto));
    }
}

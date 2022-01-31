package com.aeduard.soccerOnline.service;

import com.aeduard.soccerOnline.dto.input.PatchTeamDto;
import com.aeduard.soccerOnline.dto.mapper.TeamMapper;
import com.aeduard.soccerOnline.dto.output.TeamDto;
import com.aeduard.soccerOnline.exception.BusinessException;
import com.aeduard.soccerOnline.model.Team;
import com.aeduard.soccerOnline.model.User;
import com.aeduard.soccerOnline.model.UserRole;
import com.aeduard.soccerOnline.repository.TeamRepository;
import com.aeduard.soccerOnline.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
@Transactional
@AllArgsConstructor
@Slf4j
public class TeamService {

    private static final String TEAM_NOT_FOUND = "The team with id %d was not found";
    private static final String OPERATION_NOT_PERMITTED = "You do not have access to see/modify the team with id %s";


    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final TeamMapper teamMapper;

    public TeamDto getUserTeam(String email) {
        User user = userRepository.findByEmail(email).get();
        return user.getTeam().toDto();
    }

    public TeamDto getTeamById(String email, Long teamId) {
        User user = userRepository.findByEmail(email).get();
        return getTeamForUserById(user, teamId).toDto();
    }

    public TeamDto updateTeamById(String email, Long teamId, PatchTeamDto patchTeamDto) {

        User user = userRepository.findByEmail(email).get();
        Team team = getTeamForUserById(user, teamId);
        teamMapper.updateTeamFromPatchDto(patchTeamDto, team);
        team = teamRepository.save(team);
        return team.toDto();
    }

    public Team getTeamForUserById(User user, Long teamId) {
        Optional<Team> team = teamRepository.findById(teamId);
        if (!team.isPresent()) {
            throw new BusinessException(HttpStatus.NOT_FOUND.toString(), String.format(TEAM_NOT_FOUND, teamId), HttpStatus.NOT_FOUND);
        }
        if (user.getId() != teamId && !user.getRole().equals(UserRole.ADMIN)) {
            throw new BusinessException(HttpStatus.FORBIDDEN.toString(), String.format(OPERATION_NOT_PERMITTED, teamId), HttpStatus.FORBIDDEN);
        }
        return team.get();
    }

    public boolean isUserTeamOwnerOrAdmin(User user, Team team) {
        return user.getRole().equals(UserRole.ADMIN) || team.getId() == user.getId();
    }


}

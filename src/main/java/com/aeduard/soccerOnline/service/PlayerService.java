package com.aeduard.soccerOnline.service;

import com.aeduard.soccerOnline.dto.input.PatchPlayerDto;
import com.aeduard.soccerOnline.dto.mapper.PlayerMapper;
import com.aeduard.soccerOnline.dto.output.PlayerDto;
import com.aeduard.soccerOnline.exception.BusinessException;
import com.aeduard.soccerOnline.model.Player;
import com.aeduard.soccerOnline.model.Team;
import com.aeduard.soccerOnline.model.User;
import com.aeduard.soccerOnline.repository.PlayerRepository;
import com.aeduard.soccerOnline.repository.TeamRepository;
import com.aeduard.soccerOnline.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;

@Service
@Transactional
@AllArgsConstructor
@Slf4j
public class PlayerService {


    private static final String PLAYER_NOT_FOUND = "The player with id %d was not found";
    private static final String OPERATION_NOT_PERMITTED_READ = "You do not have access to see the player with id %s";
    private static final String OPERATION_NOT_PERMITTED_EDIT = "You do not have access to modify the player with id %s";
    private static final String NOT_ENOUGH_BUDGET = "Not enough budget to purchase the player";

    private final PlayerRepository playerRepository;
    private final TeamService teamService;
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final PlayerMapper playerMapper;


    public PlayerDto getPlayerById(String email, Long playerId) {
        User user = userRepository.findByEmail(email).get();
        Player player = getPlayerWithViewRight(user, playerId);
        return player.toDto();
    }

    public PlayerDto updatePlayerById(String email, Long playerId, PatchPlayerDto patchPlayerDto) {
        User user = userRepository.findByEmail(email).get();
        Player player = getPlayerWithEditRight(user, playerId);
        playerMapper.updatePlayerFromPatchDto(patchPlayerDto, player);
        player = playerRepository.save(player);
        return player.toDto();
    }

    public boolean userCanViewPlayerDetails(User user, Player player) {
        return teamService.isUserTeamOwnerOrAdmin(user, player.getTeam()) || player.isOnTransferList();
    }

    public boolean userCanEditPlayerDetails(User user, Player player) {
        return teamService.isUserTeamOwnerOrAdmin(user, player.getTeam());
    }

    public Player getPlayerFromDb(Long playerId) {
        Optional<Player> player = playerRepository.findById(playerId);
        if (!player.isPresent()) {
            throw new BusinessException(HttpStatus.NOT_FOUND.toString(), String.format(PLAYER_NOT_FOUND, playerId), HttpStatus.NOT_FOUND);
        }
        return player.get();
    }

    public Player getPlayerWithViewRight(User user, Long playerId) {
        Player player = getPlayerFromDb(playerId);
        if (!userCanViewPlayerDetails(user, player)) {
            throw new BusinessException(HttpStatus.FORBIDDEN.toString(), String.format(OPERATION_NOT_PERMITTED_READ, playerId), HttpStatus.FORBIDDEN);
        }
        return player;
    }

    public Player getPlayerWithEditRight(User user, Long playerId){
        Player player = getPlayerFromDb(playerId);
        if (!userCanEditPlayerDetails(user, player)) {
            throw new BusinessException(HttpStatus.FORBIDDEN.toString(), String.format(OPERATION_NOT_PERMITTED_EDIT, playerId), HttpStatus.FORBIDDEN);
        }
        return player;
    }

    public void updateTransferStateForPlayer(Long playerId) {
        Player player = getPlayerFromDb(playerId);
        player.setOnTransferList(!player.isOnTransferList()); //reverse the state
        log.info("Player {} onTransferList:{}", playerId, player.isOnTransferList());
        playerRepository.save(player);
    }

    public void transferPlayer(Player player, Team nextTeam, BigDecimal transferPrice, String transferCurrency) {

        Team currentTeam = player.getTeam();
        double randomMultiplyFactor = Math.random() * 0.91 + 1.10;

        if (nextTeam.getActiveBudget().compareTo(transferPrice) == -1) {
            log.error("Current budget:{} is lower then transfer price:{}", nextTeam.getActiveBudget(), transferPrice);
            throw new BusinessException(HttpStatus.CONFLICT.toString(), NOT_ENOUGH_BUDGET, HttpStatus.CONFLICT);
        }

        if (transferCurrency.equals(currentTeam.getReferenceCurrency())) {

            currentTeam.setActiveBudget(currentTeam.getActiveBudget().add(transferPrice));
            currentTeam.setTotalValue(currentTeam.getTotalValue().subtract(player.getMarketValue()));

            player.setMarketValue(player.getMarketValue().multiply(BigDecimal.valueOf(randomMultiplyFactor)));
            player.setOnTransferList(false);

            nextTeam.setActiveBudget(nextTeam.getActiveBudget().subtract(transferPrice));
            nextTeam.setTotalValue(nextTeam.getTotalValue().add(player.getMarketValue()));


            player.setTeam(nextTeam);
            playerRepository.save(player);
            teamRepository.saveAll(Arrays.asList(currentTeam, nextTeam));
        }
    }
}

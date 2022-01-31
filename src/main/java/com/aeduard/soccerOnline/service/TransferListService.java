package com.aeduard.soccerOnline.service;


import com.aeduard.soccerOnline.dto.output.TransferListRowDto;
import com.aeduard.soccerOnline.exception.BusinessException;
import com.aeduard.soccerOnline.model.Player;
import com.aeduard.soccerOnline.model.Team;
import com.aeduard.soccerOnline.model.TransferListRow;
import com.aeduard.soccerOnline.model.User;
import com.aeduard.soccerOnline.repository.TransferListRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
@Slf4j
public class TransferListService {

    private static final String CANNOT_DELETE_USER_NOT_ON_TRANSFER_LIST = "Delete failed. User with id {} is not on the transfer list.";
    private static final String CANNOT_TRANSFER_USER_NOT_ON_TRANSFER_LIST = "Transfer failed. User with id {} is not on the transfer list.";
    private static final String UNKNOWN_EXCEPTION = "Unkown exception.";
    private static final String RECORD_NOT_FOUND = "The record with id %d was not found";

    private  final TransferListRepository transferListRepository;
    private  final PlayerService playerService;
    private  final UserService userService;
    private  final TeamService teamService;

    public List<TransferListRowDto> getAllTransferListRecords(Integer pageNumber, Integer pageSize, String sortBy) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy));
        Page<TransferListRow> pagedResult = transferListRepository.findAll(pageable);
        return pagedResult.getContent().stream().map(TransferListRow::toDto).collect(Collectors.toList());
    }

    public TransferListRowDto getTransferListRecordById(Long transferListRecordId){
        Optional<TransferListRow> record = transferListRepository.findById(transferListRecordId);
        if(!record.isPresent()){
            throw new BusinessException(HttpStatus.NOT_FOUND.toString(), String.format(RECORD_NOT_FOUND, transferListRecordId), HttpStatus.NOT_FOUND);
        }
        return record.get().toDto();

    }

    public TransferListRowDto addPlayerToTransferList(String userEmail, Long playerId, BigDecimal transferPrice, String transferCurrency) {
        User user = userService.findByEmail(userEmail).get();
        Player player = playerService.getPlayerWithViewRight(user, playerId);
        playerService.updateTransferStateForPlayer(playerId);

        TransferListRow transferListRow = new TransferListRow();
        transferListRow.setPlayer(player);
        transferListRow.setTransferPrice(transferPrice);
        transferListRow.setTransferCurrency(transferCurrency);
        transferListRow = transferListRepository.save(transferListRow);

        return transferListRow.toDto();
    }

    public void removePlayerFromTransferListByOwner(String userEmail, Long playerId) {
        User user = userService.findByEmail(userEmail).get();
        Player player = playerService.getPlayerWithEditRight(user, playerId);
        if (!player.isOnTransferList()) {
            throw new BusinessException(HttpStatus.BAD_REQUEST.toString(), String.format(CANNOT_DELETE_USER_NOT_ON_TRANSFER_LIST, playerId), HttpStatus.BAD_REQUEST);
        }
        playerService.updateTransferStateForPlayer(playerId);
        transferListRepository.deleteById(playerId);
    }

    /**
     *
     * @param userEmail - user email of the buyer
     * @param playerId - player id
     */

    public void transferPlayerToOtherTeam(String userEmail, Long playerId, Long nextTeamId) {
        User user = userService.findByEmail(userEmail).get();
        Player player = playerService.getPlayerFromDb(playerId);
        Team team = teamService.getTeamForUserById(user, nextTeamId);
        if (!player.isOnTransferList()) {
            throw new BusinessException(HttpStatus.BAD_REQUEST.toString(), String.format(CANNOT_TRANSFER_USER_NOT_ON_TRANSFER_LIST, playerId), HttpStatus.BAD_REQUEST);
        }
        Optional<TransferListRow> transferListRow = transferListRepository.findById(playerId);
        if(!transferListRow.isPresent()){
            log.error("Player with id {} is on transfer list but not such record was found", playerId);
            throw new BusinessException(HttpStatus.INTERNAL_SERVER_ERROR.toString(), UNKNOWN_EXCEPTION, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        transferListRepository.delete(transferListRow.get());
        playerService.transferPlayer(player, team, transferListRow.get().getTransferPrice(),  transferListRow.get().getTransferCurrency());

        log.info("transfer of player with id {} to team with id {} was succesfully", playerId, nextTeamId);
    }
}

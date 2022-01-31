package com.aeduard.soccerOnline.controller;

import com.aeduard.soccerOnline.dto.output.TransferListRowDto;
import com.aeduard.soccerOnline.representation.request.AddPlayerToTransferListRequest;
import com.aeduard.soccerOnline.representation.request.TransferPlayerRequest;
import com.aeduard.soccerOnline.service.TransferListService;
import com.aeduard.soccerOnline.util.Paths;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import java.util.List;

import static com.aeduard.soccerOnline.util.Constants.ONLY_LETTERS_REGXP;

@RestController
@RequestMapping(Paths.TRANSFER_LIST)
@AllArgsConstructor
@Slf4j
public class TransferListController {

    private final TransferListService transferListService;

    @GetMapping
    public ResponseEntity<List<TransferListRowDto>> getAllTransferListRecords(@RequestParam(defaultValue = "0") @Min(0) Integer pageNo,
                                                                              @RequestParam(defaultValue = "10") @Min(1) Integer pageSize,
                                                                              @RequestParam(defaultValue = "id") @Pattern(regexp = ONLY_LETTERS_REGXP,
                                                                                      message = "Sort parameter should contain only letters") String sortBy) {
        log.info("getAllTransferListRecords()");
        List<TransferListRowDto> records = transferListService.getAllTransferListRecords(pageNo, pageSize, sortBy);
        return ResponseEntity.status(records.isEmpty() ? HttpStatus.NO_CONTENT : HttpStatus.OK).body(records);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<TransferListRowDto> getTransferListRecordById(@PathVariable("id") @Min(0) @Valid Long id) {
        log.info("getTransferListRecordById() - playerId {}", id);
        return ResponseEntity.ok().body(transferListService.getTransferListRecordById(id));
    }

    @PostMapping
    public ResponseEntity addPlayerToTransferList(@Valid @RequestBody AddPlayerToTransferListRequest addPlayerToTransferListRequest) {
        log.info("addPlayerToTransferList() - playerId {}", addPlayerToTransferListRequest.getPlayerId());
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.status(HttpStatus.CREATED).body(transferListService.addPlayerToTransferList(userEmail, addPlayerToTransferListRequest.getPlayerId(), addPlayerToTransferListRequest.getTransferPrice(), addPlayerToTransferListRequest.getTransferCurrency()));
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity removePlayerFromTransferList(@Valid @PathVariable("id") @Min(0) Long playerId) {
        log.info("removePlayerFromTransferList() - playerId {}", playerId);
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        transferListService.removePlayerFromTransferListByOwner(userEmail, playerId);
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/transfer")
    public ResponseEntity transferPlayer(@Valid @RequestBody TransferPlayerRequest transferPlayerRequest){
        log.info("transferPlayer() -  playerId {} to teamId {}", transferPlayerRequest.getPlayerId(), transferPlayerRequest.getNextTeamId());
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        transferListService.transferPlayerToOtherTeam(userEmail, transferPlayerRequest.getPlayerId(), transferPlayerRequest.getNextTeamId());
        return ResponseEntity.ok().build();
    }

}

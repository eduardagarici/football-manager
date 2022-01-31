package com.aeduard.soccerOnline.service;


import com.aeduard.soccerOnline.dto.output.TransferListRowDto;
import com.aeduard.soccerOnline.exception.BusinessException;
import com.aeduard.soccerOnline.model.Player;
import com.aeduard.soccerOnline.model.PlayerRole;
import com.aeduard.soccerOnline.model.Team;
import com.aeduard.soccerOnline.model.TransferListRow;
import com.aeduard.soccerOnline.model.User;
import com.aeduard.soccerOnline.repository.TransferListRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.AdditionalAnswers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@RunWith(SpringRunner.class)
public class TransferListServiceTest {

    @InjectMocks
    private TransferListService transferListService;

    @Mock
    private TransferListRepository transferListRepository;

    @Mock
    private UserService userService;

    @Mock
    private PlayerService playerService;

    private List<TransferListRow> transferListRowList;

    private List<Player> players;

    private Team team;

    private Team team2;

    private User user = new User();

    private Player player1 = new Player();
    private Player player2 = new Player();

    private static Long ROW_ID1 = 1l;
    private static Long ROW_ID2 = 2l;
    private static BigDecimal DEFAULT_TRANSFER_PRICE = BigDecimal.valueOf(10000l);
    private static String DEFAULT_CURRENCY = "USD";
    private static int DEFAULT_PAGE_NUMBER = 0;
    private static int DEFAULT_PAGE_SIZE = 10;
    private static int DEFAULT_AGE = 20;
    private static String DEFAULT_CONTRY = "country";
    private static String DEFAULT_SORT_BY = "id";
    private static String NAME = "Name";
    private static boolean ON_TRANSFER_LIST = false;
    private static final String RECORD_NOT_FOUND = "The record with id %d was not found";
    private static final String CANNOT_DELETE_USER_NOT_ON_TRANSFER_LIST = "Delete failed. User with id {} is not on the transfer list.";
    private static final String USER_EMAIL = "user@email.com";


    @Before
    public void setUp() {

        team = new Team();
        team2  = new Team();
        team.setId(ROW_ID1);
        team2.setId(ROW_ID2);
        team2.setActiveBudget(DEFAULT_TRANSFER_PRICE);
        team2.setReferenceCurrency(DEFAULT_CURRENCY);
        player1 = new Player(ROW_ID1, NAME, NAME, DEFAULT_CONTRY, DEFAULT_AGE, DEFAULT_TRANSFER_PRICE, DEFAULT_CURRENCY, PlayerRole.AT, ON_TRANSFER_LIST, team);
        player2 = new Player(ROW_ID2, NAME, NAME, DEFAULT_CONTRY, DEFAULT_AGE, DEFAULT_TRANSFER_PRICE, DEFAULT_CURRENCY, PlayerRole.AT, ON_TRANSFER_LIST, team);
        players = Arrays.asList(player1, player2);

        TransferListRow row1 = new TransferListRow(ROW_ID1, DEFAULT_TRANSFER_PRICE, DEFAULT_CURRENCY, player1, LocalDateTime.now());
        TransferListRow row2 = new TransferListRow(ROW_ID2, DEFAULT_TRANSFER_PRICE, DEFAULT_CURRENCY, player2, LocalDateTime.now());
        transferListRowList = Arrays.asList(row1, row2);
    }


    @Test
    public void getAllTransferListRecordsShouldReturnTheExistingData() {
        Page<TransferListRow> pagedResult = new PageImpl(transferListRowList);
        Mockito.when(transferListRepository.findAll(Mockito.any(Pageable.class))).thenReturn(pagedResult);

        List<TransferListRowDto> response = transferListService.getAllTransferListRecords(DEFAULT_PAGE_NUMBER, DEFAULT_PAGE_SIZE, DEFAULT_SORT_BY);
        assertThat(response.size()).isEqualTo(transferListRowList.size());
        response.forEach(dto -> {
            assertThat(dto.getTransferPrice()).isEqualTo(DEFAULT_TRANSFER_PRICE);
            assertThat(dto.getTransferCurrency()).isEqualTo(DEFAULT_CURRENCY);
        });

        assertThat(response.get(0).getPlayerId()).isEqualTo(ROW_ID1);
        assertThat(response.get(1).getPlayerId()).isEqualTo(ROW_ID2);

    }

    @Test
    public void getRecordByIdShouldFindTheExistingEntry(){
        Mockito.when(transferListRepository.findById(transferListRowList.get(0).getId())).thenReturn(Optional.of(transferListRowList.get(0)));
        TransferListRowDto dto = transferListService.getTransferListRecordById(transferListRowList.get(0).getId());

        assertThat(dto.getPlayerId()).isEqualTo(ROW_ID1);
        assertThat(dto.getTransferPrice()).isEqualTo(DEFAULT_TRANSFER_PRICE);
        assertThat(dto.getTransferCurrency()).isEqualTo(DEFAULT_CURRENCY);
    }

    @Test
    public void getRecordByIdShouldThrowNotFoundException(){
        Mockito.when(transferListRepository.findById(transferListRowList.get(0).getId())).thenReturn(Optional.empty());
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            transferListService.getTransferListRecordById(transferListRowList.get(0).getId());
        });

        assertThat(exception.getErrorCode()).isEqualTo(HttpStatus.NOT_FOUND.toString());
        assertThat(exception.getErrorMessage()).isEqualTo(String.format(RECORD_NOT_FOUND, transferListRowList.get(0).getId()));
    }

    @Test
    public void testSuccessfullyAddPlayerToTransferList(){

        Mockito.when(userService.findByEmail(USER_EMAIL)).thenReturn(Optional.of(user));
        Mockito.when(playerService.getPlayerWithViewRight(user, player1.getId())).thenReturn(player1);
        Mockito.when(transferListRepository.save(Mockito.any())).then(AdditionalAnswers.returnsFirstArg());

        TransferListRowDto result = transferListService.addPlayerToTransferList(USER_EMAIL, ROW_ID1, DEFAULT_TRANSFER_PRICE, DEFAULT_CURRENCY);
        Mockito.verify(playerService, Mockito.times(1)).updateTransferStateForPlayer(player1.getId());
        assertThat(result.getTransferPrice()).isEqualTo(DEFAULT_TRANSFER_PRICE);
    }

    @Test
    public void testSuccessfullyRemovePlayerFromTransferList(){
        Mockito.when(userService.findByEmail(USER_EMAIL)).thenReturn(Optional.of(user));
        Mockito.when(playerService.getPlayerWithEditRight(user, player1.getId())).thenReturn(player1);
        Mockito.doNothing().when(transferListRepository).deleteById(player1.getId());
        player1.setOnTransferList(true);
        transferListService.removePlayerFromTransferListByOwner(USER_EMAIL, player1.getId());
        Mockito.verify(playerService, Mockito.times(1)).updateTransferStateForPlayer(player1.getId());
        Mockito.verify(transferListRepository, Mockito.times(1)).deleteById(player1.getId());
    }

    @Test
    public void testRemovePlayerFromTransferListShouldFailIfPlayerNotOnTransferList(){
        Mockito.when(userService.findByEmail(USER_EMAIL)).thenReturn(Optional.of(user));
        Mockito.when(playerService.getPlayerWithEditRight(user, player1.getId())).thenReturn(player1);
        Mockito.doNothing().when(transferListRepository).deleteById(player1.getId());
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            transferListService.removePlayerFromTransferListByOwner(USER_EMAIL, player1.getId());
        });

        assertThat(exception.getErrorCode()).isEqualTo(HttpStatus.BAD_REQUEST.toString());
        assertThat(exception.getErrorMessage()).isEqualTo(String.format(CANNOT_DELETE_USER_NOT_ON_TRANSFER_LIST, player1.getId()));
    }
}


package com.aeduard.soccerOnline.service;

import com.aeduard.soccerOnline.exception.BusinessException;
import com.aeduard.soccerOnline.model.Player;
import com.aeduard.soccerOnline.model.PlayerRole;
import com.aeduard.soccerOnline.model.Team;
import com.aeduard.soccerOnline.model.User;
import com.aeduard.soccerOnline.repository.PlayerRepository;
import com.aeduard.soccerOnline.repository.TeamRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;

@RunWith(SpringRunner.class)
public class PlayerServiceTest {

    @InjectMocks
    private PlayerService playerService;

    @Mock
    private UserService userService;

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private PlayerRepository playerRepository;

    private Player player1 = new Player();

    private Team team1;

    private Team team2;

    private User user = new User();


    private static Long ROW_ID1 = 1l;
    private static Long ROW_ID2 = 2l;
    private static BigDecimal DEFAULT_TRANSFER_PRICE = BigDecimal.valueOf(10000l);
    private static String DEFAULT_CURRENCY = "USD";
    private static int DEFAULT_AGE = 20;
    private static String DEFAULT_CONTRY = "country";
    private static String DEFAULT_SORT_BY = "id";
    private static String NAME = "Name";
    private static boolean ON_TRANSFER_LIST = false;
    private static final String NOT_ENOUGH_BUDGET = "Not enough budget to purchase the player";
    @Before
    public void setUp(){
        team1 = new Team();
        team1.setId(ROW_ID1);
        team1.setActiveBudget(DEFAULT_TRANSFER_PRICE);
        team1.setTotalValue(DEFAULT_TRANSFER_PRICE);
        team1.setReferenceCurrency(DEFAULT_CURRENCY);

        team2  = new Team();
        team2.setId(ROW_ID2);
        team2.setActiveBudget(DEFAULT_TRANSFER_PRICE);
        team2.setTotalValue(DEFAULT_TRANSFER_PRICE);
        team2.setReferenceCurrency(DEFAULT_CURRENCY);

        player1 = new Player(ROW_ID1, NAME, NAME, DEFAULT_CONTRY, DEFAULT_AGE, DEFAULT_TRANSFER_PRICE, DEFAULT_CURRENCY, PlayerRole.AT, ON_TRANSFER_LIST, team1);
    }

    @Test
    public void testTransferPlayerSuccesfully(){

        playerService.transferPlayer(player1, team2, DEFAULT_TRANSFER_PRICE, DEFAULT_CURRENCY);
        Mockito.verify(playerRepository, times(1)).save(player1);
        Mockito.verify(teamRepository, times(1)).saveAll(Arrays.asList(team1, team2));

        assertThat(team1.getActiveBudget()).isEqualTo(DEFAULT_TRANSFER_PRICE.multiply(BigDecimal.valueOf(2l)));
        assertThat(team1.getTotalValue()).isEqualTo(BigDecimal.ZERO);

        assertThat(team2.getActiveBudget()).isEqualTo(BigDecimal.ZERO);
        assertThat(team2.getTotalValue()).isGreaterThan(DEFAULT_TRANSFER_PRICE);
    }

    @Test
    public void transferShouldFailIfNotEnoughBudget(){
        team2.setActiveBudget(DEFAULT_TRANSFER_PRICE.subtract(BigDecimal.ONE));

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            playerService.transferPlayer(player1, team2, DEFAULT_TRANSFER_PRICE, DEFAULT_CURRENCY);
        });

        assertThat(exception.getErrorMessage()).isEqualTo(NOT_ENOUGH_BUDGET);
        assertThat(exception.getErrorCode()).isEqualTo(HttpStatus.CONFLICT.toString());
    }
}

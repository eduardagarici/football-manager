package com.aeduard.soccerOnline.model;

import com.aeduard.soccerOnline.dto.output.PlayerDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "players")
@Data
@ToString(exclude = "team")
@EqualsAndHashCode(exclude = "team")
@NoArgsConstructor
@AllArgsConstructor
public class Player {

    private static Double INITIAL_VALUE = 1000000.0;
    private static String INITIAL_CURRENCY = "USD";
    private static String DEFAULT = "Default";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name", nullable = false)
    private String firstName = DEFAULT;

    @Column(name = "last_name", nullable = false)
    private String lastName = DEFAULT;

    @Column(name = "country", nullable = false)
    private String country = DEFAULT;

    @Column(name = "age", nullable = false)
    private Integer age = (int) (Math.random() * 23 +  18); // to obtain a positive integer from interval [18, 40];

    @Column(name = "market_value", nullable = false)
    private BigDecimal marketValue = BigDecimal.valueOf(INITIAL_VALUE);

    @Column(name = "currency_value", nullable = false)
    private String currencyValue = INITIAL_CURRENCY;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private PlayerRole playerRole;

    @Column(name = "is_on_transfer_list", nullable = false)
    @Getter
    private boolean onTransferList = false;

    @ManyToOne(fetch = FetchType.LAZY)
    private Team team;

    public Player(PlayerRole role) {
        this.playerRole = role;
    }

    public PlayerDto toDto(){
        return new PlayerDto(id, firstName, lastName, country, age, marketValue, currencyValue, playerRole.getDescription(), team.getId());
    }

}

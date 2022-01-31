package com.aeduard.soccerOnline.model;


import com.aeduard.soccerOnline.dto.output.TeamDto;
import lombok.Data;
import lombok.ToString;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.MapsId;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "teams")
@Data
@ToString(exclude = "players")
public class Team {

    private static double INITIAL_BUDGET = 5000000;
    private static String DEFAULT_CURRENCY = "USD";
    private static String DEFAULT_NAME = "default name";
    private static String DEFAULT_COUNTRY = "default country";

    @Id
    private Long id;

    @Column(name = "name", nullable = false)
    private String name = DEFAULT_NAME;

    @Column(name = "country", nullable = false)
    private String country = DEFAULT_COUNTRY;

    @Column(name = "totalValue", nullable = false)
    private BigDecimal totalValue;

    @Column(name = "active_budget", nullable = false)
    private BigDecimal activeBudget;

    @Column(name = "reference_currency", nullable = false)
    private String referenceCurrency;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    private User user;

    @OneToMany(
            mappedBy = "team",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )

    private List<Player> players = new ArrayList<>();

    public void addPlayer(Player player) {
        players.add(player);
        player.setTeam(this);
    }

    public void removePlayer(Player player) {
        players.remove(player);
        player.setTeam(null);
    }

    @PrePersist
    public void prePersist() {
        for (int i = 0; i < 3; ++i) {
            addPlayer(new Player(PlayerRole.GK));
        }
        for (int i = 0; i < 6; ++i) {
            addPlayer(new Player(PlayerRole.DEF));
        }
        for (int i = 0; i < 6; ++i) {
            addPlayer(new Player(PlayerRole.MID));
        }
        for (int i = 0; i < 5; ++i) {
            addPlayer(new Player(PlayerRole.AT));
        }
        this.totalValue = players.stream().map(Player::getMarketValue).reduce(BigDecimal.ZERO, BigDecimal::add);
        this.activeBudget = BigDecimal.valueOf(INITIAL_BUDGET);
        this.referenceCurrency = DEFAULT_CURRENCY;
    }

    public TeamDto toDto(){
        return new TeamDto(id, name, country, totalValue, activeBudget, referenceCurrency, user.getId(), players.stream().map(Player::toDto).collect(Collectors.toList()));
    }
}

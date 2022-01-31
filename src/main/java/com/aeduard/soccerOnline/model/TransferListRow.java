package com.aeduard.soccerOnline.model;


import com.aeduard.soccerOnline.dto.output.TransferListRowDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transfer_list")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransferListRow {
    @Id
    private Long id;

    @Column(name = "transfer_price", nullable = false)
    private BigDecimal transferPrice;

    @Column(name = "transfer_currency", nullable = false)
    private String transferCurrency;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    private Player player;

    @Column(name = "created_on", nullable = false)
    private LocalDateTime createdOn;

    @PrePersist
    public void prePersist() {
        this.createdOn = LocalDateTime.now();
    }

    public TransferListRowDto toDto() {
        return new TransferListRowDto(id, player.getFirstName(), player.getLastName(), player.getAge(), player.getTeam().getId(), transferPrice, transferCurrency, createdOn);
    }
}

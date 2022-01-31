package com.aeduard.soccerOnline.repository;

import com.aeduard.soccerOnline.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayerRepository extends JpaRepository<Player, Long> {
}

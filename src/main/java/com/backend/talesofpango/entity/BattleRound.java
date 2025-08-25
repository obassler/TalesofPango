package com.backend.talesofpango.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "battle_rounds")
@Data
public class BattleRound {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int roundNumber;

    @ManyToOne
    private Battle battle;

    @ManyToOne
    private Character attacker;

    @ManyToOne
    private Character defender;

    @Enumerated(EnumType.STRING)
    private ActionType action;

    private int damageDealt;
    private String result;
}

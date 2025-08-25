package com.backend.talesofpango.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "battles")
@Data
public class Battle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Character player;

    @ManyToOne
    private Character enemy;

    @ManyToOne
    private Character winner;

    private LocalDateTime startedAt;
    private LocalDateTime endedAt;

    @OneToMany(mappedBy = "battle", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BattleRound> rounds;
}

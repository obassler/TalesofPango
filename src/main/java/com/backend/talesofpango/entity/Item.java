package com.backend.talesofpango.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "items")
@Data
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    private ItemType type;

    private int hpBonus;
    private int dmgBonus;
    private double critRateBonus;
    private double critDamageBonus;
    private int agilityBonus;
    private int toughnessBonus;

    private String imageKey;
}

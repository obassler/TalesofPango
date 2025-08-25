package com.backend.talesofpango.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "character_items")
@Data
public class CharacterItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "character_id")
    private Character character;

    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;

    private boolean equipped;
}

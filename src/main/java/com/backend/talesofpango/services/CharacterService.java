package com.backend.talesofpango.services;

import com.backend.talesofpango.entity.Character;

public interface CharacterService {
    Character getCharacter(Long id);
    Character equipItem(Long characterId, Long itemId);
}
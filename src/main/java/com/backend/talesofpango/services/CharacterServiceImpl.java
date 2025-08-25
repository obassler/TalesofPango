package com.backend.talesofpango.services;

import com.backend.talesofpango.entity.Character;
import com.backend.talesofpango.entity.CharacterItem;
import com.backend.talesofpango.entity.Item;
import com.backend.talesofpango.repositories.CharacterRepository;
import com.backend.talesofpango.repositories.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class CharacterServiceImpl implements CharacterService {

    private final CharacterRepository characterRepository;
    private final ItemRepository itemRepository;

    @Override
    public Character getCharacter(Long id) {
        return characterRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Character not found!"));
    }

    @Override
    @Transactional
    public Character equipItem(Long characterId, Long itemId) {
        Character character = getCharacter(characterId);
        Item newItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found with id: " + itemId));

        ItemType newItemType = newItem.getType();

        List<CharacterItem> itemsToRemove = character.getItems().stream()
                .filter(ci -> ci.getItem().getType() == newItemType && ci.isEquipped())
                .toList();

        for (CharacterItem oldCharacterItem : itemsToRemove) {
            Item oldItem = oldCharacterItem.getItem();

            character.setHp(character.getHp() - oldItem.getHpBonus());
            character.setDamage(character.getDamage() - oldItem.getDmgBonus());
            character.setCritRate(character.getCritRate() - oldItem.getCritRateBonus());
            character.setCritDamage(character.getCritDamage() - oldItem.getCritDamageBonus());
            character.setAgility(character.getAgility() - oldItem.getAgilityBonus());
            character.setToughness(character.getToughness() - oldItem.getToughnessBonus());

            character.getItems().remove(oldCharacterItem);
        }

        character.setHp(character.getHp() + newItem.getHpBonus());
        character.setDamage(character.getDamage() + newItem.getDmgBonus());
        character.setCritRate(character.getCritRate() + newItem.getCritRateBonus());
        character.setCritDamage(character.getCritDamage() + newItem.getCritDamageBonus());
        character.setAgility(character.getAgility() + newItem.getAgilityBonus());
        character.setToughness(character.getToughness() + newItem.getToughnessBonus());

        CharacterItem newCharacterItem = new CharacterItem();
        newCharacterItem.setCharacter(character);
        newCharacterItem.setItem(newItem);
        newCharacterItem.setEquipped(true);
        character.getItems().add(newCharacterItem);

        return characterRepository.save(character);
    }
}
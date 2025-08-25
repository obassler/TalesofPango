package com.backend.talesofpango.repositories;

import com.backend.talesofpango.entity.CharacterItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CharacterItemRepository extends JpaRepository<CharacterItem, Long> {
    List<CharacterItem> findByCharacterId(Long characterId);
}
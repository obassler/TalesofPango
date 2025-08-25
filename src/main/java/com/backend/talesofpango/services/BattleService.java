package com.backend.talesofpango.services;

import com.backend.talesofpango.entity.ActionType;
import com.backend.talesofpango.entity.Battle;
import com.backend.talesofpango.entity.BattleRound;

public interface BattleService {
    Battle startBattle(Long playerId, Long enemyId);
    BattleRound executeTurn(Long battleId, ActionType playerAction);
}
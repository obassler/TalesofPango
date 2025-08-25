package com.backend.talesofpango.services;

import com.backend.talesofpango.entity.*;
import com.backend.talesofpango.repositories.BattleRepository;
import com.backend.talesofpango.repositories.CharacterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class BattleServiceImpl implements BattleService {

    private final BattleRepository battleRepository;
    private final CharacterRepository characterRepository;

    @Override
    @Transactional
    public Battle startBattle(Long playerId, Long enemyId) {
        Character player = characterRepository.findById(playerId).orElseThrow();
        Character enemy = characterRepository.findById(enemyId).orElseThrow();

        Battle battle = new Battle();
        battle.setPlayer(player);
        battle.setEnemy(enemy);
        battle.setStartedAt(LocalDateTime.now());

        return battleRepository.save(battle);
    }

    @Override
    @Transactional
    public BattleRound executeTurn(Long battleId, ActionType playerAction) {
        Battle battle = battleRepository.findById(battleId)
                .orElseThrow(() -> new RuntimeException("Battle not found!"));

        if (battle.getWinner() != null) {
            throw new IllegalStateException("This battle has already concluded.");
        }

        Character player = battle.getPlayer();
        Character enemy = battle.getEnemy();

        ActionType enemyAction = determineEnemyAction();

        int playerDamageDealt = 0;
        int enemyDamageDealt = 0;
        StringBuilder resultDescription = new StringBuilder();

        if (playerAction == ActionType.ATTACK) {
            if (enemyAction == ActionType.ATTACK) {
                playerDamageDealt = calculateDamage(player, enemy);
                enemyDamageDealt = calculateDamage(enemy, player);
                resultDescription.append(String.format("%s and %s attack each other! ", player.getName(), enemy.getName()));
            } else if (enemyAction == ActionType.BLOCK) {
                playerDamageDealt = calculateDamage(player, enemy);
                resultDescription.append(String.format("%s attacks, but %s's block has no effect! ", player.getName(), enemy.getName()));
            } else {
                if (isAttackSuccessful(player, enemy)) {
                    playerDamageDealt = calculateDamage(player, enemy);
                    resultDescription.append(String.format("%s attacks and hits the dodging %s! ", player.getName(), enemy.getName()));
                } else {
                    resultDescription.append(String.format("%s attacks, but %s dodges successfully! ", player.getName(), enemy.getName()));
                }
            }
        }
        else if (playerAction == ActionType.BLOCK) {
            if (enemyAction == ActionType.ATTACK) {
                resultDescription.append(String.format("%s prepares to attack, but %s blocks, nullifying the turn! ", enemy.getName(), player.getName()));
            } else {
                resultDescription.append("Both combatants take a defensive stance. Nothing happens. ");
            }
        }
        else {
            if (enemyAction == ActionType.ATTACK) {
                if (isAttackSuccessful(enemy, player)) {
                    enemyDamageDealt = calculateDamage(enemy, player);
                    resultDescription.append(String.format("%s tries to dodge, but %s lands a hit! ", player.getName(), enemy.getName()));
                } else {
                    resultDescription.append(String.format("%s successfully dodges %s's attack! ", player.getName(), enemy.getName()));
                }
            } else {
                resultDescription.append("Both combatants wait for an opening. Nothing happens. ");
            }
        }

        int newPlayerHp = Math.max(0, player.getHp() - enemyDamageDealt);
        int newEnemyHp = Math.max(0, enemy.getHp() - playerDamageDealt);
        player.setHp(newPlayerHp);
        enemy.setHp(newEnemyHp);

        resultDescription.append(String.format("%s dealt %d damage. %s dealt %d damage.", player.getName(), playerDamageDealt, enemy.getName(), enemyDamageDealt));

        BattleRound round = new BattleRound();
        round.setBattle(battle);
        round.setRoundNumber(battle.getRounds().size() + 1);
        round.setAction(playerAction);
        round.setDamageDealt(playerDamageDealt);
        round.setResult(resultDescription.toString());
        battle.getRounds().add(round);

        if (newPlayerHp <= 0) {
            battle.setWinner(enemy);
            battle.setEndedAt(LocalDateTime.now());
        } else if (newEnemyHp <= 0) {
            battle.setWinner(player);
            battle.setEndedAt(LocalDateTime.now());
        }

        characterRepository.save(player);
        characterRepository.save(enemy);
        battleRepository.save(battle);

        return round;
    }

// --- Helper Methods ---

    /**
     * A simple AI to randomly choose an action for the enemy.
     */
    private ActionType determineEnemyAction() {
        return ActionType.values()[new Random().nextInt(ActionType.values().length)];
    }

    /**
     * Calculates the damage an attacker deals, including critical hits.
     * Toughness of the defender reduces incoming damage.
     */
    private int calculateDamage(Character attacker, Character defender) {
        double baseDamage = attacker.getDamage();
        // Check for a critical hit
        if (Math.random() < attacker.getCritRate()) {
            baseDamage *= attacker.getCritDamage(); // Apply critical damage multiplier
        }
        // Defender's toughness reduces damage. Ensure damage isn't negative.
        int finalDamage = (int) Math.max(0, baseDamage - defender.getToughness());
        return finalDamage;
    }

    /**
     * Determines if an attack hits a dodging opponent.
     * Success is based on the defender's agility.
     */
    private boolean isAttackSuccessful(Character attacker, Character defender) {
        // Each point of agility gives a 0.5% chance to dodge. Capped at 75%.
        double dodgeChance = Math.min(defender.getAgility() * 0.005, 0.75);
        return Math.random() > dodgeChance;
    }
}
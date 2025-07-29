package com.milite.battle.abilities;

import com.milite.battle.BattleUnit;
import com.milite.battle.BattleContext;

public interface SpecialAbility {
	void onAttack(BattleUnit attacker, BattleUnit target, BattleContext context);
	void onDefensePerHit(BattleUnit defender, BattleUnit attacker, int damage, BattleContext context);
	void onDefensePerTurn(BattleUnit defender, BattleUnit attacker, int totalDamage, BattleContext context);
	void onTurnStart(BattleUnit unit, BattleContext context);
	void onTurnEnd(BattleUnit unit, BattleContext context);
	String getName();
}

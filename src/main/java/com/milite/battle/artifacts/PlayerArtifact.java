package com.milite.battle.artifacts;

import com.milite.battle.BattleUnit;
import com.milite.battle.BattleContext;

public interface PlayerArtifact {
	void onPlayerAttack(BattleUnit attacker, BattleUnit target, BattleContext context);

	void onPlayerHit(BattleUnit attacker, BattleUnit target, int damageDealt, BattleContext context);

	void onPlayerDefensePerHit(BattleUnit defender, BattleUnit attacker, int damage, BattleContext context);

	void onPlayerDefensePerTurn(BattleUnit defender, BattleUnit attacker, int damage, BattleContext context);

	void onPlayerTurnStart(BattleUnit unit, BattleContext context);

	void onPlayerTurnEnd(BattleUnit unit, BattleContext context);

	String getArtifactName();

	String getArtifactDescription();

	/*default String getArtifactRarity() {
		return "Common";
	} 아마 아티팩트에 등급을 넣지 않을 것으로 생각됨*/

	default boolean isActive() {
		return true;
	}
}

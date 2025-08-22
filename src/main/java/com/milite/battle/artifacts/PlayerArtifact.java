package com.milite.battle.artifacts;

import com.milite.battle.BattleUnit;
import com.milite.battle.BattleContext;

public interface PlayerArtifact {
	/*
	 * 플레이어 아티팩트에 대한 인터페이스 아래의 메서드는 전투의 어느 시점에서 발동되는지에 대해 정하는 메서드 (각 메서드의 시점은 각 메서드의
	 * 주석 확인)
	 * 
	 * 단, 모든 행동에 대해서는 BattleContext를 이용하여 진행할 것
	 */

	void onPlayerAttack(BattleUnit attacker, BattleUnit target, BattleContext context);
// 공격 시도 시, 발동하는 옵션

	void onPlayerHit(BattleUnit attacker, BattleUnit target, int damageDealt, BattleContext context);
// 공격 명중 시, 발동하는 옵션

	void onPlayerDefensePerHit(BattleUnit defender, BattleUnit attacker, int damage, BattleContext context);
// 매 피격마다 발동하는 옵션

	void onPlayerDefensePerTurn(BattleUnit defender, BattleUnit attacker, int damage, BattleContext context);
// 피격할 때, 발동하는 옵션

	void onPlayerTurnStart(BattleUnit unit, BattleContext context);
// 턴 시작 시, 발동하는 옵션

	void onPlayerTurnEnd(BattleUnit unit, BattleContext context);
// 턴 종료 시, 발동하는 옵션

	String getArtifactName();

	String getArtifactDescription();

	default boolean isActive() {
		return true;
	}
}

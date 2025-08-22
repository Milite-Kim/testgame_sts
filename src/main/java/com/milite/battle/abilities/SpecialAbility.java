package com.milite.battle.abilities;

import com.milite.battle.BattleUnit;
import com.milite.battle.BattleContext;

public interface SpecialAbility {
	/*
	 * 몬스터의 특수능력 인터페이스
	 * 아래의 메서드는 전투의 어느 시점에서 발동되는지에 대해 정하는 메서드 (각 메서드의 시점은 각 메서드의 주석 확인)
	 * 
	 * 단, 모든 행동에 대해서는 BattleContext를 이용하여 진행할 것
	*/
	
	void onAttack(BattleUnit attacker, BattleUnit target, BattleContext context);
	/* 공격 시도 시, 명중 여부와 관계 없이 발동하는 옵션 */
	
	void onHit(BattleUnit attacker, BattleUnit target, int damageDealt, BattleContext context);
	/* 명중 시, 발동하는 옵션 */
	
	void onDefensePerHit(BattleUnit defender, BattleUnit attacker, int damage, BattleContext context);
	/* 피격 시, 매 피격마다 발동하는 옵션 */
	
	void onDefensePerTurn(BattleUnit defender, BattleUnit attacker, int totalDamage, BattleContext context);
	/* 피격 시, 해당 턴에 한 번만 발동하는 옵션 */
	
	void onTurnStart(BattleUnit unit, BattleContext context);
	/* 턴 시작 시 발동하는 옵션 */
	void onTurnEnd(BattleUnit unit, BattleContext context);
	/* 턴 종료 시 발동하는 옵션 */
	
	String getName();
}

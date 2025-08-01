package com.milite.battle.abilities;

import com.milite.battle.BattleContext;
import com.milite.battle.BattleUnit;
import com.milite.util.KoreanUtil;

public class RecoveryAbility implements SpecialAbility {
	private static final int RECOVERY_AMOUNT = 4;

	@Override
	public void onAttack(BattleUnit attacker, BattleUnit target, BattleContext context) {

	}

	@Override
	public void onHit(BattleUnit attacker, BattleUnit target,int damageDealt, BattleContext context) {

	}

	@Override
	public void onDefensePerHit(BattleUnit defender, BattleUnit attacker, int damage, BattleContext context) {

	}

	@Override
	public void onDefensePerTurn(BattleUnit defender, BattleUnit attacker, int totalDamage, BattleContext context) {

	}

	@Override
	public void onTurnStart(BattleUnit unit, BattleContext context) {
		context.healUnit(unit, RECOVERY_AMOUNT);
		context.addLogEntry(unit.getName(), "special_ability", unit.getName()
				+ KoreanUtil.getJosa(unit.getName(), "이 ", "가 ") + "꽃풀을 먹어 " + RECOVERY_AMOUNT + "만큼 회복했습니다.");
	}

	@Override
	public void onTurnEnd(BattleUnit unit, BattleContext context) {

	}

	@Override
	public String getName() {
		return "Recovery";
	}

	public int getRecoveryAmount() {
		return RECOVERY_AMOUNT;
	}
}
package com.milite.battle.abilities;

import com.milite.battle.BattleContext;
import com.milite.battle.BattleUnit;
import com.milite.constants.BattleConstants;
import com.milite.util.KoreanUtil;

public class RecoveryAbility implements SpecialAbility {
	/* 각 메서드의 전반적인 내용은 SpecialAbility 파일의 주석을 우선 확인 
	 * 매 턴 시작 시, 회복함
	 * */
	@Override
	public void onAttack(BattleUnit attacker, BattleUnit target, BattleContext context) {

	}

	@Override
	public void onHit(BattleUnit attacker, BattleUnit target, int damageDealt, BattleContext context) {

	}

	@Override
	public void onDefensePerHit(BattleUnit defender, BattleUnit attacker, int damage, BattleContext context) {

	}

	@Override
	public void onDefensePerTurn(BattleUnit defender, BattleUnit attacker, int totalDamage, BattleContext context) {

	}

	@Override
	public void onTurnStart(BattleUnit unit, BattleContext context) {
		// 턴 시작 시, 정해진 수치만큼 회복 진행
		context.healUnit(unit, BattleConstants.getRecoveryAmount());
		context.addLogEntry(unit.getName(), "special_ability",
				unit.getName() + KoreanUtil.getJosa(unit.getName(), "이 ", "가 ") + "꽃풀을 먹어 "
						+ BattleConstants.getRecoveryAmount() + "만큼 회복했습니다.");
	}

	@Override
	public void onTurnEnd(BattleUnit unit, BattleContext context) {

	}

	@Override
	public String getName() {
		return "Recovery";
	}

	public int getRecoveryAmount() {
		return BattleConstants.getRecoveryAmount();
	}
}
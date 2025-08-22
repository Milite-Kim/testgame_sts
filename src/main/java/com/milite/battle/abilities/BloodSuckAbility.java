package com.milite.battle.abilities;

import com.milite.battle.BattleContext;
import com.milite.battle.BattleUnit;
import com.milite.constants.BattleConstants;
import com.milite.util.KoreanUtil;

public class BloodSuckAbility implements SpecialAbility {
	/* 각 메서드의 전반적인 내용은 SpecialAbility 파일의 주석을 우선 확인 
	 * 
	 * 흡혈, 가한 피해량의 50%만큼 회복함 */
	@Override
	public void onAttack(BattleUnit attacker, BattleUnit target, BattleContext context) {

	}

	@Override
	public void onHit(BattleUnit attacker, BattleUnit target, int damageDealt, BattleContext context) {
		// 피해를 입히지 못한 경우 회복 없음
		if (damageDealt <= 0) {
			return;
		}

		// 회복량은 가한 데미지의 50%
		int healAmount = (int) Math.ceil(damageDealt * BattleConstants.getBloodSuckRatio());

		// 회복량이 0이 넘을 경우, 최대 체력을 고려하여 실제 회복량을 계산
		if (healAmount > 0) {
			int actualHealed = context.healUnit(attacker, healAmount);
			context.addLogEntry(attacker.getName(), "blood_suck", attacker.getName()
					+ KoreanUtil.getJosa(attacker.getName(), "이 ", "가 ") + "흡혈로 " + actualHealed + "만큼 체력을 회복했습니다.");
		}
	}

	@Override
	public void onDefensePerHit(BattleUnit defender, BattleUnit attacker, int damage, BattleContext context) {

	}

	@Override
	public void onDefensePerTurn(BattleUnit defender, BattleUnit attacker, int totalDamage, BattleContext context) {

	}

	@Override
	public void onTurnStart(BattleUnit unit, BattleContext context) {

	}

	@Override
	public void onTurnEnd(BattleUnit unit, BattleContext context) {

	}

	@Override
	public String getName() {
		return "BloodSuck";
	}
}

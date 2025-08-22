package com.milite.battle.abilities;

import com.milite.battle.BattleContext;
import com.milite.battle.BattleMonsterUnit;
import com.milite.battle.BattleUnit;
import com.milite.util.KoreanUtil;

public class ImmunAbility implements SpecialAbility {
	/* 각 메서드의 전반적인 내용은 SpecialAbility 파일의 주석을 우선 확인 
	 * 상태이상 면역, 일차적으로 상태이상 부여 시, 이 특수 능력을 보유하고 있다면 제외하도록 코딩이 되어있음
	 * 이 파일의 코드는 그럼에도 공격을 받았을 때 상태이상이 있다면 그걸 없애는 안전장치용 코딩
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
		if (defender.getStatusEffects() != null && !defender.getStatusEffects().isEmpty()) {
			defender.getStatusEffects().clear();
			context.addLogEntry(defender.getName(), "immune",
					defender.getName() + KoreanUtil.getJosa(defender.getName(), "의 ", "의 ") + "면역력으로 상태이상이 정화되었습니다.");
		}
	}

	@Override
	public void onTurnStart(BattleUnit unit, BattleContext context) {

	}

	@Override
	public void onTurnEnd(BattleUnit unit, BattleContext context) {

	}

	@Override
	public String getName() {
		return "Immun";
	}

	// 해당 몬스터가 면역 능력이 있는지 반환
	public static boolean isImmun(BattleUnit unit) {
		if (unit instanceof BattleMonsterUnit) {
			BattleMonsterUnit monster = (BattleMonsterUnit) unit;
			return "Immun".equals(monster.getSpecial());
		}
		return false;
	}
}
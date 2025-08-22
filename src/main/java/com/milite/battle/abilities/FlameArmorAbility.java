package com.milite.battle.abilities;

import com.milite.battle.BattleContext;
import com.milite.battle.BattleUnit;
import com.milite.constants.BattleConstants;
import com.milite.util.KoreanUtil;

public class FlameArmorAbility implements SpecialAbility {
	/* 각 메서드의 전반적인 내용은 SpecialAbility 파일의 주석을 우선 확인 
	 * 공격 받았다면 적에게 반사 피해를 입힘(턴 당 1회 한정)
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
		// 공격자가 존재하고, 살아있다면 발동
		if (attacker != null && attacker.isAlive()) {
			// 공격자에게 반사 피해 적용
			context.addReflectDamage(attacker, BattleConstants.getFlameArmorReflect());
			context.addLogEntry(defender.getName(), "flame_armor", defender.getName() + "의 불꽃 갑옷이 " + attacker.getName()
					+ KoreanUtil.getJosa(attacker.getName(), "을 ", "를 ") + "태웠습니다!");
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
		return "FlameArmor";
	}
}

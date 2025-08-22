package com.milite.battle.abilities;

import com.milite.battle.BattleContext;
import com.milite.battle.BattleMonsterUnit;
import com.milite.battle.BattleUnit;
import com.milite.constants.BattleConstants;
import com.milite.util.KoreanUtil;

public class ThreeChanceAbility implements SpecialAbility {
	/* 각 메서드의 전반적인 내용은 SpecialAbility 파일의 주석을 우선 확인 
	 * 3의 배수 턴마다 공격력 증가 버프 보유
	 * */
	@Override
	public void onAttack(BattleUnit attacker, BattleUnit target, BattleContext context) {
		// 특수 능력 발동 시, 특수 로그 남기기
		if (isThreeMultipleTurn(context.getCurrentTurn())) {
			context.addLogEntry(attacker.getName(), "three_chance",
					attacker.getName() + KoreanUtil.getJosa(attacker.getName(), "이 ", "가 ") + "거대한 무언가를 떨어트립니다.");
		}
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
		// 3의 배수 턴일 때, 공격력 증가 버프 부여
		if (isThreeMultipleTurn(context.getCurrentTurn())) {
			context.addLogEntry(unit.getName(), "power_up",
					unit.getName() + KoreanUtil.getJosa(unit.getName(), "의 ", "의 ") + "공격력이 상승합니다!");
		}
	}

	@Override
	public void onTurnEnd(BattleUnit unit, BattleContext context) {

	}

	@Override
	public String getName() {
		return "ThreeChance";
	}

	// 공격력 배율 반환
	public static double getDamageMultiplier(BattleUnit unit, int currentTurn) {
		if (unit instanceof BattleMonsterUnit) {
			BattleMonsterUnit monster = (BattleMonsterUnit) unit;
			if ("ThreeChance".equals(monster.getSpecial()) && isThreeMultipleTurn(currentTurn)) {
				return BattleConstants.getThreeChanceMultiplier();
			}
		}
		return 1.0;
	}

	// 3의 배수 턴인지 반환
	public static boolean isThreeMultipleTurn(int turn) {
		return turn > 0 && turn % 3 == 0;
	}
}

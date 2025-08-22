package com.milite.battle.abilities;

import com.milite.battle.BattleContext;
import com.milite.battle.BattleMonsterUnit;
import com.milite.battle.BattleUnit;
import com.milite.constants.BattleConstants;
import com.milite.util.KoreanUtil;

public class ModeSwitchAbility implements SpecialAbility {
	/* 각 메서드의 전반적인 내용은 SpecialAbility 파일의 주석을 우선 확인 
	 * 공격 모드와 방어 모드를 전환하며, 가하는 데미지와 받는 데미지에 변동이 생김
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
		// 몬스터가 아니면 사용 하지 않음
		if (!(unit instanceof BattleMonsterUnit)) {
			return;
		}

		BattleMonsterUnit monster = (BattleMonsterUnit) unit;
		int currentTurn = context.getCurrentTurn();
		int formCount = getFormCount(monster);

		boolean isHungryMode = shouldUseOffensiveStance(formCount, currentTurn);

		String modeName = isHungryMode ? "포식 모드" : "휴식 모드";
		String description = isHungryMode ? "공격적으로 변합니다" : "방어적으로 변합니다";

		context.addLogEntry(unit.getName(), "mode_switch", unit.getName()
				+ KoreanUtil.getJosa(unit.getName(), "이 ", "가 ") + modeName + "로 전환했습니다. " + description);
	}

	@Override
	public void onTurnEnd(BattleUnit unit, BattleContext context) {

	}

	@Override
	public String getName() {
		return "ModeSwitch";
	}

	private static int getFormCount(BattleMonsterUnit monster) {
		return monster.getFormCount();
	}

	// 홀수 턴에 무슨 모드로 할지 선택
	public static boolean shouldUseOffensiveStance(int formCount, int currentTurn) {
		boolean isOddTurn = currentTurn % 2 == 1;

		if (formCount == 0) {
			return isOddTurn;
		} else {
			return !isOddTurn;
		}
	}

	// 해당 모드에서 데미지 배율 반환
	public static double getAttackMultiplier(BattleUnit unit, int currentTurn) {
		if (!(unit instanceof BattleMonsterUnit)) {
			return 1.0;
		}

		BattleMonsterUnit monster = (BattleMonsterUnit) unit;
		if (!"ModeSwitch".equals(monster.getSpecial())) {
			return 1.0;
		}

		int formCount = getFormCount(monster);
		boolean isOffensive = shouldUseOffensiveStance(formCount, currentTurn);

		return isOffensive ? BattleConstants.getModeSwitchOffenseAtk() : BattleConstants.getModeSwitchDefenseAtk();
	}

	// 해당 모드에서 받는 피해 배율 반환
	public static double getDefenseMulitplier(BattleUnit unit, int currentTurn) {
		if (!(unit instanceof BattleMonsterUnit)) {
			return 1.0;
		}

		BattleMonsterUnit monster = (BattleMonsterUnit) unit;
		if (!"ModeSwitch".equals(monster.getSpecial())) {
			return 1.0;
		}

		int formCount = getFormCount(monster);
		boolean isOffensive = shouldUseOffensiveStance(formCount, currentTurn);

		return isOffensive ? BattleConstants.getModeSwitchOffenseDef() : BattleConstants.getModeSwitchDefenseDef();
	}
}

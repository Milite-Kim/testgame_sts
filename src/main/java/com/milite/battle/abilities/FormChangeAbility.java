package com.milite.battle.abilities;

import com.milite.battle.BattleContext;
import com.milite.battle.BattleMonsterUnit;
import com.milite.battle.BattleUnit;
import com.milite.constants.BattleConstants;
import com.milite.util.KoreanUtil;

public class FormChangeAbility implements SpecialAbility {
	/* 각 메서드의 전반적인 내용은 SpecialAbility 파일의 주석을 우선 확인 
	 * 
	 * 매 턴, 공격 모드와 수비 모드가 전환됨*/
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
		// 유닛이 몬스터가 아닌 경우 발동하지 않음
		if (!(unit instanceof BattleMonsterUnit)) {
			return;
		}

		BattleMonsterUnit monster = (BattleMonsterUnit) unit;
		// 현재 턴을 확인하고, 해당 턴에 따라 공격 모드인지 수비 모드인지 정하고 적용
		int currentTurn = context.getCurrentTurn();
		int formCount = getFormCount(monster);

		boolean isOffensiveStance = shouldUseOffensiveStance(formCount, currentTurn);

		String stanceName = isOffensiveStance ? "공격 태세" : "방어 태세";
		String description = isOffensiveStance ? "공격력이 증가하지만 받는 피해도 증가합니다." : "받는 피해가 감소하지만 공격력도 감소합니다.";
		
		context.addLogEntry(unit.getName(), "form_change", 
	            unit.getName() + KoreanUtil.getJosa(unit.getName(), "이 ", "가 ") + 
	            stanceName + "로 변환했습니다. " + description);
	}

	@Override
	public void onTurnEnd(BattleUnit unit, BattleContext context) {

	}

	@Override
	public String getName() {
		return "FormChange";
	}
	
	private static int getFormCount(BattleMonsterUnit monster) {
		// Todo 추후에 수정해야함
		// static으로 해야하는가? 혹은 special에 따라 BattleMonsterUnit에 변수가 추가되는 식으로 해야하는가?
		return monster.getFormCount();
	}
	
	// 공격 모드 턴인지 아닌지 확인
	public static boolean shouldUseOffensiveStance(int formCount, int currentTurn) {
		boolean isOddTurn = currentTurn %2 == 1;
		
		if(formCount == 0) {
			return isOddTurn;
		}else {
			return !isOddTurn;
		}
	}
	
	// 현재 상태에 따른 데미지 배율 반환
	public static double getAttackMultiplier(BattleUnit unit, int currentTurn) {
		// 플레이어는 1배 반환
		if(!(unit instanceof BattleMonsterUnit)) {
			return 1.0;
		}
		
		// 이 특수 능력이 없다면 1배 반환
		BattleMonsterUnit monster = (BattleMonsterUnit) unit;
		if(!"FormChange".equals(monster.getSpecial())) {
			return 1.0;
		}
		
		int formCount = getFormCount(monster);
		boolean isOffensive = shouldUseOffensiveStance(formCount, currentTurn);
		
		// 공격 모드면 공격 모드 배율 반환, 방어 모드면 방어 모드 배율 반환
		return isOffensive ? BattleConstants.getFormChangeOffenseAtk() : BattleConstants.getFormChangeDefenseAtk();
	}
	
	// 현재 상태에 따른 받는 피해 감소 배율 반환. 세부 내용은 위의 getAttackMultiplier 와 동일
	public static double getDefenseMultiplier(BattleUnit unit, int currentTurn) {
		if(!(unit instanceof BattleMonsterUnit)) {
			return 1.0;
		}
		
		BattleMonsterUnit monster = (BattleMonsterUnit) unit;
		if(!"FormChange".equals(monster.getSpecial())) {
			return 1.0;
		}
		
		int formCount = getFormCount(monster);
		boolean isOffensive = shouldUseOffensiveStance(formCount, currentTurn);
		
		return isOffensive ? BattleConstants.getFormChangeOffenseDef() : BattleConstants.getFormChangeDefenseDef();
	}
}

package com.milite.battle.artifacts;

import com.milite.battle.BattleContext;
import com.milite.battle.BattleUnit;
import com.milite.constants.BattleConstants;

public class MagicianAmuletArtifact implements PlayerArtifact{
	/* 전반적인 내용은 PlayerArtifact의 주석 확인
	 * 아티팩트의 내용은 아래의 내용 확인
	 * 실제 배율을 반환 받아서 적용하는 건 BattleSession에 있음
	 * */
	private static final String ARTIFACT_NAME = "마법사의 부적";
	private static final String ARTIFACT_DESCRIPTION = "약세 상성 공격 시, 배율 10% 증가";
	
	
	@Override
	public void onPlayerAttack(BattleUnit attacker, BattleUnit target, BattleContext context) {

	}

	@Override
	public void onPlayerHit(BattleUnit attacker, BattleUnit target, int damageDealt, BattleContext context) {

	}

	@Override
	public void onPlayerDefensePerHit(BattleUnit defender, BattleUnit attacker, int damage, BattleContext context) {

	}

	@Override
	public void onPlayerDefensePerTurn(BattleUnit defender, BattleUnit attacker, int totalDamage,
			BattleContext context) {

	}

	@Override
	public void onPlayerTurnStart(BattleUnit unit, BattleContext context) {

	}

	@Override
	public void onPlayerTurnEnd(BattleUnit unit, BattleContext context) {

	}

	@Override
	public String getArtifactName() {
		return ARTIFACT_NAME;
	}

	@Override
	public String getArtifactDescription() {
		return ARTIFACT_DESCRIPTION;
	}
	
	// 현재가 약세 공격인가?
	public boolean hasElementDisadvantage(double baseMultiplier) {
		return baseMultiplier <1.0;
	}
	
	// 반환해야할 보정 배율 0.1
	public double getElementDisadvantageBonus() {
		return BattleConstants.getMagicianAmuletBonus();
	}
	
	public String getEffectDescription(double baseMultiplier) {
		if (hasElementDisadvantage(baseMultiplier)) {
			double finalMultiplier = baseMultiplier + getElementDisadvantageBonus();
			return String.format("마법사의 부적 효과 : %.1f배 -> %.1f배", baseMultiplier, finalMultiplier);
		}
		return "약세 상성이 아니기에 마법사의 부적 효과가 적용되지 않음";
	}
}

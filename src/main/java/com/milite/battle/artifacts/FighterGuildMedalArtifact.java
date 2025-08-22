package com.milite.battle.artifacts;

import com.milite.battle.BattleContext;
import com.milite.battle.BattleUnit;
import com.milite.constants.BattleConstants;

public class FighterGuildMedalArtifact implements PlayerArtifact {
	/* 전반적인 내용은 PlayerArtifact의 주석 확인
	 * 아티팩트의 내용은 아래의 내용 확인
	 * */
	private static final String ARTIFACT_NAME = "파이터 길드 메달";
	private static final String ARTIFACT_DESCRIPTION = "무속성 스킬 카드의 데미지 2 증가";
	private static final String TARGET_ELEMENT = "None";
	
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
	
	// 스킬의 속성이 아티팩트 적용 속성인지 확인
	public boolean isTargetElement(String skillElement) {
		return TARGET_ELEMENT.equals(skillElement);
	}
	
	public int getDamageBonus() {
		return BattleConstants.getFighterGuildMedalBonus();
	}
	
	// 조건이 맞다면 추가 데미지 수치 반환
	public int calculateDamageBonus(String skillElement) {
		if(isTargetElement(skillElement)) {
			return getDamageBonus();
		}
		return 0;
	}
	
	public String getEffectDescription(String skillElement) {
		if (isTargetElement(skillElement)) {
			return String.format("파이터 길드 메달 효과: %s 속성 스킬에 +%d 데미지", TARGET_ELEMENT, BattleConstants.getFighterGuildMedalBonus());
		}
		return "파이터 길드 메달 효과 없음 (무속성 스킬이 아님)";
	}
}

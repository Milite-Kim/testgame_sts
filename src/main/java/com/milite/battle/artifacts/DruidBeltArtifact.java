package com.milite.battle.artifacts;

import com.milite.battle.BattleContext;
import com.milite.battle.BattleUnit;
import com.milite.constants.BattleConstants;

public class DruidBeltArtifact implements PlayerArtifact {
	/* 전반적인 내용은 PlayerArtifact의 주석 확인
	 * 아티팩트의 내용은 아래의 내용 확인
	 * */
	private static final String ARTIFACT_NAME = "드루이드의 벨트";
	private static final String ARTIFACT_DESCRIPTION = "풀속성 스킬 카드의 데미지 2 증가";
	private static final String TARGET_ELEMENT = "Grass";
	
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
	
	// 아티팩트 조건 속성인지 확인
	public boolean isTargetElement(String skillElement) {
		return TARGET_ELEMENT.equals(skillElement);
	}
	
	public int getDamageBonus() {
		return BattleConstants.getDruidBeltBonus();
	}
	// 조건 성립 시, 추가 데미지 반환
	public int calculateDamageBonus(String skillElement) {
		if(isTargetElement(skillElement)) {
			return getDamageBonus();
		}
		return 0;
	}
}

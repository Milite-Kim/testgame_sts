package com.milite.battle.artifacts;

import com.milite.battle.BattleContext;
import com.milite.battle.BattleUnit;
import com.milite.constants.BattleConstants;

public class GrayCloakArtifact implements PlayerArtifact {
	/* 전반적인 내용은 PlayerArtifact의 주석 확인
	 * 아티팩트의 내용은 아래의 내용 확인
	 * 실제 구현은 BattleSession의 isAttacked() 메서드에 포함되어있음
	 * */
	private static final String ARTIFACT_NAME = "회색 망토";
	private static final String ARTIFACT_DESCRIPTION = "회피율 5% 증가";

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

	public int getDodgeBonus() {
		return BattleConstants.getGrayCloakDodgeBonus();
	}
}

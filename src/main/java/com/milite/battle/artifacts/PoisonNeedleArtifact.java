package com.milite.battle.artifacts;

import com.milite.battle.BattleContext;
import com.milite.battle.BattleUnit;
import com.milite.constants.BattleConstants;

public class PoisonNeedleArtifact implements PlayerArtifact {
	/* 전반적인 내용은 PlayerArtifact의 주석 확인
	 * 아티팩트의 내용은 아래의 내용 확인
	 * 실질적인 처리는 BattleContext의 상태이상 처리 부분에서 담당하고 있음
	 * */
	private static final String ARTIFACT_NAME = "바늘 달린 독 장치";
	private static final String ARTIFACT_DESCRIPTION = "중독 상태이상 적용 시, 기존에 적용된 중독 스택의 절반에 해당하는 피해를 가함";

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

	public double getPoisonNeedleRatio() {
		return BattleConstants.getPoisonNeedleRatio();
	}

	// 현재 걸린 독 스택을 입력 받아서 그에 맞은 스택 절반 수치를 반환
	public int calculateStackDamage(int existingPoisonTurns) {
		if (existingPoisonTurns <= 0) {
			return 0;
		}

		double additionalDamage = existingPoisonTurns * getPoisonNeedleRatio();
		return (int) Math.ceil(additionalDamage);
	}

	public String getEffectDescription() {
		return String.format("바늘 달린 독 장치 효과: 중독 적용 시 기존 스택의 %.0f%%만큼 추가 피해", getPoisonNeedleRatio() * 100);
	}
}

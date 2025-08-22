package com.milite.battle.artifacts;

import com.milite.battle.BattleContext;
import com.milite.battle.BattleUnit;
import com.milite.util.KoreanUtil;

public class OverloadCrystalArtifact implements PlayerArtifact {
	/* 전반적인 내용은 PlayerArtifact의 주석 확인
	 * 아티팩트의 내용은 아래의 내용 확인
	 * 확정 명중 보정은 BattleSession의 isAttacked() 메서드에 처리되어있음
	 * */
	private static final String ARTIFACT_NAME = "과부하 결정";
	private static final String ARTIFACT_DESCRIPTION = "첫 턴에 하는 공격이 확정 명중";

	private boolean isUsed = false;

	@Override
	public void onPlayerAttack(BattleUnit attacker, BattleUnit target, BattleContext context) {
		// 공격 시, 이 옵션이 사용 가능한지 확인 후 사용 가능해지면 로그 남기기
		if (!isUsed) {
			isUsed = true;

			context.addLogEntry(attacker.getName(), "overload_crystal_effect", attacker.getName()
					+ KoreanUtil.getJosa(attacker.getName(), "의 ", "의 ") + ARTIFACT_NAME + "이 불안정하게 빛을 발합니다.");
		}
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

	public boolean canUse() {
		return !isUsed;
	}

	public void useEffect() {
		this.isUsed = true;
	}

	public boolean hasUsedEffect() {
		return isUsed;
	}

	public String getStatusDescription() {
		if (isUsed) {
			return "과부하 결정: 효과 사용됨";
		} else {
			return "과부하 결정: 다음 공격 확정 명중 준비됨";
		}
	}
}

package com.milite.battle.artifacts;

import com.milite.battle.BattleContext;
import com.milite.battle.BattleUnit;
import com.milite.util.KoreanUtil;

public class SlipperyLeatherArtifact implements PlayerArtifact {
	/* 전반적인 내용은 PlayerArtifact의 주석 확인
	 * 아티팩트의 내용은 아래의 내용 확인
	 * */
	private static final String ARTIFACT_NAME = "미끄러운 가죽 보호대";
	private static final String ARTIFACT_DESCRIPTION = "전투마다 처음 받는 피해를 무효화함";

	private boolean isUsed = false;

	@Override
	public void onPlayerAttack(BattleUnit attacker, BattleUnit target, BattleContext context) {

	}

	@Override
	public void onPlayerHit(BattleUnit attacker, BattleUnit target, int damageDealt, BattleContext context) {

	}

	@Override
	public void onPlayerDefensePerHit(BattleUnit defender, BattleUnit attacker, int damage, BattleContext context) {
		// 아직 이 아티팩트가 발동하지 않았고, 실제로 피해를 받은 경우
		if (!isUsed && damage > 0) {
			isUsed = true; // 발동을 하였다고 상태를 변경한 뒤 로그 남김

			context.addLogEntry(defender.getName(), "slippery_leather_effect", defender.getName() + "의 공격이 "
					+ KoreanUtil.getJosa(defender.getName(), "의 ", "의 ") + ARTIFACT_NAME + "로 인해 미끄러졌습니다.");
		}
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
			return "미끄러운 가죽 보호대: 효과 사용됨";
		} else {
			return "미끄러운 가죽 보호대: 다음 피해 무효화 준비됨";
		}
	}
}

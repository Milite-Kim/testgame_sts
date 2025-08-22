package com.milite.battle.artifacts;

import com.milite.battle.BattleContext;
import com.milite.battle.BattleUnit;
import com.milite.constants.BattleConstants;
import com.milite.util.KoreanUtil;

public class BlackCoralArtifact implements PlayerArtifact {
	/* 전반적인 내용은 PlayerArtifact의 주석 확인
	 * 공격을 시도했을 때, 3을 회복함
	 * */
	
	private static final String ARTIFACT_NAME = "검은 산호";
	private static final String ARTIFACT_DESCRIPTION = "공격 시 3 회복";

	@Override
	public void onPlayerAttack(BattleUnit attacker, BattleUnit target, BattleContext context) {
		// 회복량을 BattleConstants에서 끌어옴
		int healAmount = getHealAmount();
		// 실제로 회복량이 몇인지 확인
		int actualHealed = context.healUnit(attacker, healAmount);

		// 실제로 회복이 되었다면 로그 남기기 
		if (actualHealed > 0) {
			context.addLogEntry(attacker.getName(), "artifact_heal", attacker.getName()
					+ KoreanUtil.getJosa(attacker.getName(), "이 ", "가 ") + "검은 산호 효과로 " + actualHealed + "만큼 회복했습니다.");
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

	public int getHealAmount() {
		return BattleConstants.getBlackCoralHealAmount();
	}
}

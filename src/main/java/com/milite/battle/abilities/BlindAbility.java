package com.milite.battle.abilities;

import com.milite.battle.BattleContext;
import com.milite.battle.BattleUnit;
import com.milite.constants.BattleConstants;

import com.milite.util.KoreanUtil;

public class BlindAbility implements SpecialAbility {
	/* 각 메서드의 전반적인 내용은 SpecialAbility 파일의 주석을 우선 확인
	 * 
	 *  명중률이 1턴 간 50% 감소하는 효과 
	 *  이 클래스는 실명을 확률에 따라 부여할지 말지에 대해서 결정하는 클래스
	 *  실질적인 명중률 감소 메커니즘은 BattleSession 클래스의 isAttacked() 메서드 확인
	 *  */
	@Override
	public void onAttack(BattleUnit attacker, BattleUnit target, BattleContext context) {

	}

	@Override
	public void onHit(BattleUnit attacker, BattleUnit target, int damageDealt, BattleContext context) {
		// 데미지를 받지 않은 경우 발동하지 않음
		if (damageDealt <= 0) {
			return;
		}

		// 몬스터가 타겟인 경우 발동하지 않음
		if (!target.getUnitType().equals("Player")) {
			return;
		}

		//확률 계산을 위한 랜덤 다이스
		int roll = (int) (Math.random() * 100) + 1;
		if (roll <= BattleConstants.getBlindChance()) {
			// 플레이어가 이미 실명 상태인지 판별(실명 지속시간은 1턴이기에 현재로써는 거의 무의미한 코드)
			boolean wasAlreadyBlind = isBlind(target);

			// 플레이어에게 실명 부여 
			context.addStatusEffect(target, BattleConstants.STATUS_BLIND, BattleConstants.getBlindTurn());

			if (wasAlreadyBlind) {
				context.addLogEntry(attacker.getName(), "blind_refresh",
						attacker.getName() + "의 공격으로 " + target.getName() + "의 실명이 갱신되었습니다!");
			} else {
				context.addLogEntry(attacker.getName(), BattleConstants.STATUS_BLIND, attacker.getName() + "의 공격으로 "
						+ target.getName() + KoreanUtil.getJosa(target.getName(), "이 ", "가 ") + "실명 상태에 걸렸습니다!");
			}
		}
	}

	@Override
	public void onDefensePerHit(BattleUnit defender, BattleUnit attacker, int damage, BattleContext context) {

	}

	@Override
	public void onDefensePerTurn(BattleUnit defender, BattleUnit attacker, int totalDamage, BattleContext context) {

	}

	@Override
	public void onTurnStart(BattleUnit unit, BattleContext context) {

	}

	@Override
	public void onTurnEnd(BattleUnit unit, BattleContext context) {

	}

	@Override
	public String getName() {
		return "Blind";
	}

	public static boolean isBlind(BattleUnit unit) {
		// 이미 실명 상태이상을 보유 중인지 확인
		if (unit.getStatusEffects() != null) {
			return unit.getStatusEffects().getOrDefault(BattleConstants.STATUS_BLIND, 0) > 0;
		}
		return false;
	}
}

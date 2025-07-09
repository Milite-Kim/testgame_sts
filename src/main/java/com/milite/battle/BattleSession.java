package com.milite.battle;

import lombok.*;
import java.util.*;

import com.milite.dto.BattleResultDto;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BattleSession {
	private int sessionId;
	private BattleUnit player;
	private BattleUnit enemy;
	private boolean isFinished;
	private int turnCount;
	private List<String> battleLog = new ArrayList<>();

	public BattleResultDto perfornTurn(BattleUnit attacker, BattleUnit defender) {
		attacker.setActionGauge(attacker.getActionGauge() - 20);
		
		boolean isHit = isAttacked(attacker.getAcc(), defender.getEva());
		boolean isCritical = false;
		int damage = 0;
		int newHp = defender.getHp();
		
		if (isHit) {
			int atkValue = attacker.getAtk();
			if (isCritical(attacker.getCri())) {
				isCritical = true;
				atkValue = (int) Math.floor(atkValue * 1.5);
			}
			
			atkValue = calcAtk(atkValue, null);
			damage = calcDmg(atkValue, defender.getDef());
			newHp = Math.max(newHp - damage, 0);
			
			defender.setHp(newHp);
			if(newHp == 0) {
				defender.setAlive(false);
				isFinished = true;
			}
		}
		
		String msg = isHit ? ( isCritical ? "치명타!" : "")+ attacker.getName() + ">" + defender.getName() + ":"+ damage + "의 피해를 입혔습니다" : "빗나감!";
		battleLog.add(attacker.getName() + " -> " + defender.getName() + ":" + msg);
		
		return new BattleResultDto(msg,damage,newHp,isCritical, isHit, newHp == 0);
	}
	
	public void increaseGauges() {
		player.setActionGauge(player.getActionGauge() + randomGaugeGain(player.getAgi()));
		enemy.setActionGauge(enemy.getActionGauge() + randomGaugeGain(enemy.getAgi()));
	}

	private int randomGaugeGain(int agi) {
		int min = agi / 2;
		int max = agi;
		return (int) (Math.random() * (max - min + 1)) + min;
	}
	
	public BattleUnit getNextAttacker() {
		if(player.isAlive() && player.getActionGauge() >= 20) {
			return player;
		}
		if(enemy.isAlive() && enemy.getActionGauge() >=20) {
			return enemy;
		}	
		return null;
	}
	
	private boolean isAttacked(int acc, int eva) {
		int dodgeChance = Math.max(0, eva - acc);
		int roll = (int) (Math.random() * 100) + 1;
		return roll > dodgeChance;
	}

	private boolean isCritical(int cri) {
		int roll = (int) (Math.random() * 100) + 1;
		return roll <= cri;
	}

	private int calcAtk(int atk, Integer sta) {
		if (sta == null) {
			sta = 0;
		}

		double range_percent = 0.1 * (1 - (sta / 100.0));
		double atk_range = (Math.random() * 2 * range_percent) - range_percent;

		double calc = atk * (1 + atk_range);

		return (int) Math.round(calc);
	}

	private int calcDmg(int atk, int def) {
		double dmg = atk * (1 - ((double) def / (def + 100)));
		return (int) Math.round(dmg);
	}
	
}
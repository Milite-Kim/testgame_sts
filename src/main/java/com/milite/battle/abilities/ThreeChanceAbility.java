package com.milite.battle.abilities;

import com.milite.battle.BattleContext;
import com.milite.battle.BattleUnit;

public class ThreeChanceAbility implements SpecialAbility{
	@Override
	public void onAttack(BattleUnit attacker, BattleUnit target, BattleContext context) {

	}
	
	@Override
	public void onDefense(BattleUnit defender, BattleUnit attacker, int damage, BattleContext context) {
		
	}
	
	@Override
	public void onTurnStart(BattleUnit unit, BattleContext context) {
		
	}
	
	@Override
	public void onTurnEnd(BattleUnit unit, BattleContext context) {
		
	}
	
	@Override
	public String getName() {
		return "ThreeChance";
	}
}

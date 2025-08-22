package com.milite.battle.abilities;

import java.util.*;

public class SpecialAbilityFactory {
	/* 
	 * 특수 능력을 생성해서 연결하기 위한 부분
	 * 특수능력 이름과 해당 클래스를 연결하는 클래스
	 * */
	private static final Map<String, SpecialAbility> abilities = new HashMap<>();

	static {
		abilities.put("Swift", new SwiftAbility());
		abilities.put("DoubleAttack", new DoubleAttackAbility());
		abilities.put("Recovery", new RecoveryAbility());
		abilities.put("BraveBite", new BraveBiteAbility());
		abilities.put("FormChange", new FormChangeAbility());
		abilities.put("ThreeChance", new ThreeChanceAbility());
		abilities.put("FlameArmor", new FlameArmorAbility());
		abilities.put("ModeSwitch", new ModeSwitchAbility());
		abilities.put("ThreeStack", new ThreeStackAbility());
		abilities.put("Immun", new ImmunAbility());
		abilities.put("BloodSuck", new BloodSuckAbility());
		abilities.put("Summon", new SummonAbility());
		abilities.put("Blind", new BlindAbility());
	}

	public static SpecialAbility getAbility(String specialName) {
		return abilities.get(specialName);
	}
}

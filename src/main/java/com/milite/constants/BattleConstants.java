package com.milite.constants;

import java.util.Map;

public class BattleConstants {
	public static final String STATUS_BURN = "Burn"; // 화상
	public static final String STATUS_POISON = "Poison"; // 중독
	public static final String STATUS_FREEZE = "Freeze"; // 빙결
	public static final String STATUS_STUN = "Stun"; // 기절
	public static final String STATUS_BLIND = "Blind";

	public static final int BURN_DAMAGE = 3; // 이 부분은 화상 데미지 결정되면 그 때 수정

	public static final int SUMMON_MASTER_ID = 51;
	public static final int SERVANT_MONSTER_ID = 52;

	public static final int BASE_DODGE_ROLL = 15;
	public static final int DODGE_MULTIPLIER = 2;

	public static final int BLIND_DODGE_BONUS = 50;

	public static final Map<String, Map<String, Double>> ELEMENT_EFFECTIVENESS = Map.of("Fire",
			Map.of("Grass", 1.2, "Water", 0.8, "Fire", 1.0, "None", 1.0), "Water",
			Map.of("Fire", 1.2, "Grass", 0.8, "Water", 1.0, "None", 1.0), "Grass",
			Map.of("Water", 1.2, "Fire", 0.8, "Grass", 1.0, "None", 1.0), "None",
			Map.of("Fire", 1.0, "Water", 1.0, "Grass", 1.0, "None", 1.0));
}

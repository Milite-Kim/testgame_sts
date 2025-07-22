package com.milite.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SkillDto {
	String skill_ID;
	String skill_Type;
	String element;
	int min_damage;
	int max_damage;
	int hit_time;
	String target;
	
	Integer statusEffectRate;
	Integer statusEffectTurn;
	
	int image_ID;
}

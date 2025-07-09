package com.milite.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BattleResultDto {
	private String resultMessage;
	private int damageDealt;
	private int targetRemainingHp;
	private boolean isCritical;
	private boolean isHit;
	private boolean isDefeated;
}

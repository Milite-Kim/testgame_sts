package com.milite.battle;

import com.milite.dto.ComStatusDto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BattleUnit {
	private Integer id;
	private String name;
	private int hp;
	private int atk;
	private int def;
	private int Acc;
	private int Eva;
	private int cri;
	private int agi;
	private int actionGauge;
	private boolean isAlive;
	private boolean isPlayer;

	public static BattleUnit fromDto(ComStatusDto dto, boolean isPlayer) {
		return new BattleUnit(dto.getId(), dto.getName(), dto.getCurr_hp(), dto.getAtk(), dto.getDef(), dto.getAcc(),
				dto.getEva(), dto.getCri(), dto.getAgi(), 0, true, isPlayer);
	}

}

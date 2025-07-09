package com.milite.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaseStatDto {
	private String race;

	private Integer hp;
	private Integer atk;
	private Integer def;
	private Integer cri;
	private Integer acc;
	private Integer eva;
	private Integer agi;
	
	private Double hpLvCoef;
	private Double atkLvCoef;
	private Double defLvCoef;
	private Double criLvCoef;
	private Double accLvCoef;
	private Double evaLvCoef;
	private Double agiLvCoef;
	
	private Double hpSPCoef;
	private Double atkSPCoef;
	private Double defSPCoef;
	private Double criSPCoef;
	private Double accSPCoef;
	private Double evaSPCoef;
	private Double agiSPCoef;
}

package com.milite.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ComStatusDto {
	private Integer id;
	private String name;
	private String race;
	private String type;

	private Integer max_hp;
	private Integer curr_hp;
	private Integer atk;
	private Integer def;
	private Integer cri;
	private Integer acc;
	private Integer eva;
	private Integer agi;

	private Integer gold;
	private Integer level;
	private Integer curr_exp;
	private Integer max_exp;
}
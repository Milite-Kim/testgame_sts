package com.milite.battle;

import com.milite.dto.MonsterDto;
import java.util.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BattleMonsterUnit implements BattleUnit {
	private Integer ID;
	private String name;
	private String element;
	private int hp;
	private int min_atk;
	private int max_atk;
	private int luck;
	private boolean isAlive;
	private String special;
	private Map<String, Integer> statusEffects = new HashMap<>();
	
	public BattleMonsterUnit(MonsterDto dto) {
		this.ID = dto.getMonsterID();
		this.name = dto.getName();
		this.element = dto.getElement();
		this.hp = calcHP(dto);
		this.min_atk = dto.getMin_atk();
		this.max_atk = dto.getMax_atk();
		this.luck = dto.getLuck();
		this.isAlive = true;
		this.special = dto.getSpecial();
	}

	private int calcHP(MonsterDto dto) {
		int hp = (int) (Math.random() * (dto.getMax_hp() - dto.getMin_hp())) + dto.getMin_hp();
		return hp;
	}

	/* private int calcATK(MonsterDto dto) {
		int atk = (int) (Math.random() * (dto.getMax_atk() - dto.getMin_atk())) + dto.getMin_atk();
		return atk;
	} */ //현재 사용 안하는 함수
	
    @Override
    public String getName() {
        return this.name;
    }
    
    @Override
    public int getHp() {
        return this.hp;
    }
    
    @Override
    public boolean isAlive() {
        return this.isAlive;
    }
    
    @Override
    public String getUnitType() {
        return "Monster";
    }
    
    @Override
    public boolean hasSwift() {
        return "Swift".equals(this.special);
    }
    
    @Override
    public String getSpecial() {
        return this.special;
    }
    
    @Override
    public Map<String, Integer> getStatusEffects() {
        return this.statusEffects;
    }
    
    @Override
    public void setStatusEffects(Map<String, Integer> statusEffects) {
        this.statusEffects = statusEffects;
    }
}

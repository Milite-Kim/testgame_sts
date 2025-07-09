package com.milite.service;

import com.milite.dto.BattleResultDto;

public interface BattleService {
	public BattleResultDto battle(Integer PlayerId, Integer EnemyId);
}

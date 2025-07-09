package com.milite.service;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.milite.battle.BattleSession;
import com.milite.battle.BattleUnit;
import com.milite.dto.BattleResultDto;
import com.milite.dto.ComStatusDto;
import com.milite.mapper.ComStatusMapper;

import lombok.Setter;
import lombok.extern.log4j.Log4j;

@Log4j
@Service
public class BattleServiceImpl implements BattleService {

	@Setter(onMethod_ = @Autowired)
	private ComStatusMapper comStatusMapper;

	@Override
	public BattleResultDto battle(Integer playerId, Integer enemyId) {
		if (playerId == null || enemyId == null) {
			return new BattleResultDto("전투 대상 정보 없음", 0, 0, false, false, false);
		}

		ComStatusDto playerDto;
		ComStatusDto enemyDto;
		try {
			playerDto = getBattleInfo(playerId);
			enemyDto = getBattleInfo(enemyId);
			if (playerDto == null || enemyDto == null) {
				return new BattleResultDto("전투 대상 정보 없음", 0, 0, false, false, false);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new BattleResultDto("전투 대상 정보 없음", 0, 0, false, false, false);
		}

		BattleUnit player = unitMemory.getOrDefault(playerId, BattleUnit.fromDto(playerDto, true));
		BattleUnit enemy = unitMemory.getOrDefault(enemyId, BattleUnit.fromDto(enemyDto, false));

		BattleSession session = new BattleSession(999, player, enemy, false, 0, new ArrayList<>());
		session.increaseGauges();

		BattleUnit attacker = session.getNextAttacker();

		if (attacker == null) {

			unitMemory.put(playerId, player);
			unitMemory.put(enemyId, enemy);

			String msg = "아직 행동할 수 있는 유닛 없음. 게이지 누적됨\n" + player.getName() + ": " + player.getActionGauge() + " / "
					+ enemy.getName() + ": " + enemy.getActionGauge();
			return new BattleResultDto(msg, 0, 0, false, false, false);
		}

		BattleUnit defender = (attacker == player) ? enemy : player;
		BattleResultDto result = session.perfornTurn(attacker, defender);

		ComStatusDto updateDto = new ComStatusDto();
		updateDto.setId(defender.getId());
		updateDto.setCurr_hp(defender.getHp());
		comStatusMapper.updateStatus(updateDto);

		if (result.isDefeated() && !defender.isPlayer()) {
			// 몬스터 쓰러짐 처리
			// 전투 보상 처리
		} else if (result.isDefeated() && defender.isPlayer()) {
			comStatusMapper.resetChar(defender.getId());
			// 패배 처리
		}

		if (player.isAlive()) {
			unitMemory.put(playerId, player);
		} else {
			unitMemory.remove(playerId);
		}

		if (enemy.isAlive()) {
			unitMemory.put(enemyId, enemy);
		} else {
			unitMemory.remove(enemyId);
		}
		return result;
	}

	private ComStatusDto getBattleInfo(Integer Id) {
		ComStatusDto dto = comStatusMapper.getInfo(Id);
		return dto;
	}

	private final Map<Integer, BattleUnit> unitMemory = new HashMap<>();
}

package com.milite.service;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.milite.battle.*;
import com.milite.dto.BattleResultDto;
import com.milite.dto.MonsterDto;
import com.milite.dto.PlayerDto;
import com.milite.dto.SkillDto;
import com.milite.mapper.CharacterStatusMapper;
import com.milite.mapper.MonsterMapper;
import com.milite.util.CommonUtil;
import com.milite.util.KoreanUtil;

import lombok.Setter;
import lombok.extern.log4j.Log4j;

@Log4j
@Service
public class BattleServiceImpl implements BattleService {

	@Setter(onMethod_ = @Autowired)
	private MonsterMapper monsterMapper;

	@Setter(onMethod_ = @Autowired)
	private CharacterStatusMapper characterStatusMapper;

	private final Map<String, PlayerDto> PlayerMemory = new HashMap<>();
	private final Map<String, BattleMonsterUnit> MonsterMemory = new HashMap<>();
	private final Map<String, BattleSession> SessionMemory = new HashMap<>();

	private static final String STATUS_BURN = "Burn"; // 화상
	private static final String STATUS_POISON = "Poison"; // 중독
	private static final String STATUS_FREEZE = "Freeze"; // 빙결
	private static final String STATUS_STUN = "Stun"; // 기절

	private static final int BURN_DAMAGE = 3; // 이 부분은 화상 데미지 결정되면 그 때 수정

	public BattleResultDto processNextAction(String PlayerID, SkillDto playerSkill, Integer targetIndex) {

		BattleSession session = SessionMemory.get(PlayerID);
		if (session == null) {
			return createErrorResult("전투 세션을 찾을 수 없습니다.");
		}
		// 배틑 세션 확인

		BattleResultDto battleStatus = checkBattleEndCondition(session);
		if (battleStatus != null) {
			return battleStatus;
		}
		// 배틀 종료 확인

		BattleUnit currentUnit = getCurrentActionUnit(session);
		if (currentUnit == null) {
			return createErrorResult("액션 정보를 확인할 수 없습니다.");
		}
		// 액션 정보 확인

		List<BattleLogEntry> allActionLogs = new ArrayList<>();
		BattleResultDto actionResult;
		// 액션 정보 처리

		if (currentUnit.getUnitType().equals("Player")) {// 플레이어의 액션 처리
			actionResult = processPlayerActionInternal(session, currentUnit, playerSkill, targetIndex, allActionLogs);
		} else {// 그게 아니라면 몬스터의 액션 처리
			actionResult = processMonsterActionInternal(session, currentUnit, allActionLogs);
		}

		if (currentUnit.getUnitType().equals("Player") && !actionResult.isDefeated()) {// 몬스터들의 액션 연속 처리
			List<BattleResultDto> monsterActionResults = processAllMonsterAction(session, allActionLogs);

			for (BattleResultDto monsterResult : monsterActionResults) {
				if (monsterResult.isDefeated()) {
					actionResult = monsterResult;
					break;
				}
			}
		}

		if (!actionResult.isDefeated()) {// 전투가 끝났는지 확인
			BattleResultDto endCheck = checkBattleEndCondition(session);
			if (endCheck != null) {
				actionResult = endCheck;
			}
		}

		session.getBattleLog().addAll(allActionLogs);// 해당 로그들을 세션에 저장

		return createActionResult(actionResult, session, allActionLogs);
	}

	@Override
	public BattleResultDto battle(String PlayerID) {
		// 플레이어 정보 관련
		if (PlayerID == null) {
			return createErrorResult("플레이어 정보가 없습니다.");
		}

		PlayerDto Player = getPlayerInfo(PlayerID); // 플레이어 정보 불러오기
		if (Player == null) {
			return createErrorResult("플레이어 정보가 없습니다.");
		}

		// 몬스터 생성 및 배열
		ArrayList<MonsterDto> EnemyData = generateEnemies(Player.getWhereSession(), Player.getWhereStage());
		ArrayList<BattleMonsterUnit> enemy = new ArrayList<>();
		for (MonsterDto dto : EnemyData) {
			enemy.add(new BattleMonsterUnit(dto));
		}

		// 액션 순서 정하기
		List<BattleUnit> actionOrder = createActionOrder(Player, enemy);

		// 정보를 세션에 저장하기
		int sessionID = generateSessionID();
		BattleSession session = new BattleSession(sessionID, Player, enemy, false, 1, new ArrayList<>(), actionOrder,
				0);

		// 메모리에 유닛들 정보 저장
		saveToMemory(PlayerID, Player, enemy, session);

		log.info("=== 전투 시작 ===");
		logActionOrder(actionOrder);

		return new BattleResultDto("전투 시작", 0, 0, false, false, new ArrayList<>());
	}

	public Map<String, Object> getBattleStatus(String PlayerID) {
		BattleSession session = SessionMemory.get(PlayerID);
		if (session == null) {
			return Map.of("error", "전투 세션을 찾을 수 없음");
		}

		BattleUnit currentUnit = getCurrentActionUnit(session);
		Map<String, Object> status = new HashMap<>();

		status.put("isFinished", session.isFinished());
		status.put("currentTurn", session.getCurrentTurn());
		status.put("currentActionIndex", session.getCurrentActionIndex());
		status.put("needsPlayerInput", currentUnit != null && currentUnit.getUnitType().equals("Player"));
		status.put("currentUnit", currentUnit != null ? currentUnit.getName() : null);
		status.put("unitType", currentUnit != null ? currentUnit.getUnitType() : null);

		status.put("playerHp", session.getPlayer().getHp());
		status.put("playerMaxHp", session.getPlayer().getMax_hp());
		status.put("aliveMonsters", session.getEnemy().stream().filter(BattleUnit::isAlive).map(BattleUnit::getName)
				.collect(Collectors.toList()));

		return status;
	}

	private BattleResultDto processPlayerActionInternal(BattleSession session, BattleUnit player, SkillDto skill,
			Integer targetIndex, List<BattleLogEntry> allLogs) {
		List<BattleLogEntry> actionLogs = new ArrayList<>();

		processStatusEffectsAtActionStart(player, actionLogs, session.getCurrentTurn()); // 행동 전의 상태 이상 처리

		if (isUnitDisabled(player)) {
			String statusName = getDisableStatusName(player);
			log.info("플레이어가 " + statusName + " 상태로 행동 불가");

			decreaseDisableStatusTurns(player);
			actionLogs.add(new BattleLogEntry(player.getName(), "disabled", player.getName()
					+ KoreanUtil.getJosa(player.getName(), "이 ", "가 ") + statusName + " 상태로 행동할 수 없습니다.",
					session.getCurrentTurn()));

			moveToNextAction(session);
			allLogs.addAll(actionLogs);
			return new BattleResultDto(statusName + " 상태로 행동 불가", 0, 0, false, false, actionLogs);
		}

		if (skill == null) {
			actionLogs.add(
					new BattleLogEntry(player.getName(), "skip", "스킬 정보가 없어서 액션을 넘깁니다.", session.getCurrentTurn()));
			moveToNextAction(session);
			allLogs.addAll(actionLogs);
			return new BattleResultDto("스킬 정보 없음으로 액션 스킵", 0, 0, false, false, actionLogs);
		}

		List<BattleUnit> allUnits = createAllUnitsList(session);
		BattleResultDto attackResult = session.battleTurn(player, allUnits, targetIndex, skill);

		applySkillStatusEffects(skill, allUnits, targetIndex, actionLogs, session.getCurrentTurn());

		addActionInfoToLogs(attackResult, session.getCurrentTurn());
		actionLogs.addAll(attackResult.getBattleLog());
		allLogs.addAll(actionLogs);

		moveToNextAction(session);

		return new BattleResultDto(attackResult.getMessage(), attackResult.getDamage(), attackResult.getNewHp(),
				attackResult.isHit(), attackResult.isDefeated(), actionLogs);
	}

	private BattleResultDto processMonsterActionInternal(BattleSession session, BattleUnit monster,
			List<BattleLogEntry> allLogs) {
		List<BattleLogEntry> actionLogs = new ArrayList<>();

		if (!monster.isAlive()) {
			actionLogs.add(new BattleLogEntry(monster.getName(), "skip",
					monster.getName() + KoreanUtil.getJosa(monster.getName(), "은 ", "는 ") + "이미 사망 상태입니다",
					session.getCurrentTurn()));

			moveToNextAction(session);
			allLogs.addAll(actionLogs);
			return new BattleResultDto("사망한 몬스터 액션 스킵", 0, 0, false, false, actionLogs);
		}

		processStatusEffectsAtActionStart(monster, actionLogs, session.getCurrentTurn());

		if (isUnitDisabled(monster)) {
			String statusName = getDisableStatusName(monster);
			decreaseDisableStatusTurns(monster);
			actionLogs.add(new BattleLogEntry(monster.getName(), "disabled",
					monster.getName() + KoreanUtil.getJosa(monster.getName(), "이 ", "가 ") + statusName + " 상태로 행동 불가",
					session.getCurrentTurn()));

			moveToNextAction(session);
			allLogs.addAll(actionLogs);
			return new BattleResultDto(statusName + " 상태로 행동 불가", 0, 0, false, false, actionLogs);
		}

		List<BattleUnit> allUnits = createAllUnitsList(session);
		BattleResultDto attackResult = session.battleTurn(monster, allUnits);

		addActionInfoToLogs(attackResult, session.getCurrentTurn());
		actionLogs.addAll(attackResult.getBattleLog());
		allLogs.addAll(actionLogs);

		moveToNextAction(session);

		return new BattleResultDto(attackResult.getMessage(), attackResult.getDamage(), attackResult.getNewHp(),
				attackResult.isHit(), attackResult.isDefeated(), actionLogs);
	}

	private List<BattleResultDto> processAllMonsterAction(BattleSession session, List<BattleLogEntry> allLogs) {
		List<BattleResultDto> results = new ArrayList<>();

		while (true) {
			BattleUnit currentUnit = getCurrentActionUnit(session);

			if (currentUnit == null || currentUnit.getUnitType().equals("Player")) {
				break;
			}

			BattleResultDto monsterResult = processMonsterActionInternal(session, currentUnit, allLogs);
			results.add(monsterResult);

			if (monsterResult.isDefeated()) {
				break;
			}

			if (results.size() > 10) {
				log.warn("몬스터 액션 처리 중 무한 루프 의심");
				break;
			}
		}

		return results;
	}

	private BattleResultDto checkBattleEndCondition(BattleSession session) {
		boolean playerAlive = session.getPlayer().isAlive();
		boolean anyMonsterAlive = session.getEnemy().stream().anyMatch(BattleUnit::isAlive);

		if (!playerAlive) {
			session.setFinished(true);
			log.info("=== 플레이어 패배 ===");
			return new BattleResultDto("전투 패배", 0, 0, false, true, session.getBattleLog());
		} else if (!anyMonsterAlive) {
			session.setFinished(true);
			log.info("=== 플레이어 승리 ===");
			return new BattleResultDto("전투 승리", 0, 0, false, true, session.getBattleLog());
		}

		return null;
	}

	private BattleResultDto createActionResult(BattleResultDto baseResult, BattleSession session,
			List<BattleLogEntry> logs) {
		BattleUnit nextUnit = getCurrentActionUnit(session);
		boolean needsPlayerInput = nextUnit != null && nextUnit.getUnitType().equals("Player") && !session.isFinished();

		Map<String, Object> additionalInfo = new HashMap<>();
		additionalInfo.put("needsPlayerInput", needsPlayerInput);
		additionalInfo.put("nextUnitName", nextUnit != null ? nextUnit.getName() : null);
		additionalInfo.put("currentTurn", session.getCurrentTurn());

		return new BattleResultDto(baseResult.getMessage(), baseResult.getDamage(), baseResult.getNewHp(),
				baseResult.isHit(), baseResult.isDefeated(), logs);
	}

	private BattleResultDto createErrorResult(String message) {// 오류 발생 시 메시지 생성하는 기능
		return new BattleResultDto(message, 0, 0, false, false, new ArrayList<>());
	}

	private void saveToMemory(String PlayerID, PlayerDto player, ArrayList<BattleMonsterUnit> enemy,
			BattleSession session) {

		PlayerMemory.put(PlayerID, player);
		for (int i = 0; i < enemy.size(); i++) {
			MonsterMemory.put(PlayerID + "_enemy" + i, enemy.get(i));
		}
		SessionMemory.put(PlayerID, session);
	}

	private ArrayList<MonsterDto> generateEnemies(String session, int WhereStage) {
		return Enemy(session, WhereStage);
	}

	// 상태이상 처리용
	private void processStatusEffectsAtActionStart(BattleUnit unit, List<BattleLogEntry> logs, int currentTurn) {
		Map<String, Integer> statusMap = unit.getStatusEffects();
		if (statusMap == null || statusMap.isEmpty()) {
			return;
		}

		if (statusMap.containsKey(STATUS_BURN) && statusMap.get(STATUS_BURN) > 0) {
			applyDamageToUnit(unit, BURN_DAMAGE);
			logs.add(new BattleLogEntry(unit.getName(), "burn_damage",
					unit.getName() + KoreanUtil.getJosa(unit.getName(), "이 ", "가 ") + BURN_DAMAGE + "의 피해를 받았습니다.",
					currentTurn));
			decreaseStatusTurns(unit, STATUS_BURN);
		}

		if (statusMap.containsKey(STATUS_POISON) && statusMap.get(STATUS_POISON) > 0) {
			int poisonDamage = statusMap.get(STATUS_POISON);
			applyDamageToUnit(unit, poisonDamage);
			logs.add(new BattleLogEntry(unit.getName(), "poison_damage",
					unit.getName() + KoreanUtil.getJosa(unit.getName(), "이 ", "가 ") + poisonDamage + "의 피해를 받았습니다.",
					currentTurn));
			decreaseStatusTurns(unit, STATUS_POISON);
		}
	}

	private void applySkillStatusEffects(SkillDto skill, List<BattleUnit> allUnits, Integer targetIndex,
			List<BattleLogEntry> logs, int currentTurn) {
		String element = skill.getElement();
		if (element == null) {
			return;
		}

		Integer effectRate = skill.getStatusEffectRate();
		Integer effectTurns = skill.getStatusEffectTurn();

		if (effectRate == null || effectRate <= 0 || effectTurns == null || effectTurns <= 0) {
			return; // 상태이상 없음
		}

		// 확률 판정
		int roll = (int) (Math.random() * 100) + 1;
		if (roll > effectRate) { // 실패 시 하단의 상태이상 처리 전부 무시
			log.debug("상태이상 확률 실패: " + roll + " > " + effectRate);
			return;
		}

		// 속성으로 상태이상 결정
		String statusType = null;

		switch (element) {
		case "Fire":
			statusType = STATUS_BURN;
			break;
		case "Water":
			statusType = STATUS_FREEZE;
			break;
		case "Grass":
			statusType = STATUS_POISON;
			break;
		case "None":
			statusType = STATUS_STUN;
			break;
		}

		if (statusType != null) {
			log.info(statusType + " (확률: " + effectRate + "%, 지속: " + effectTurns + "턴)");
			applyStatusEffectToTargets(skill.getTarget(), allUnits, targetIndex, statusType, effectTurns, logs,
					currentTurn);
		}
	}

	private void applyStatusEffectToTargets(String targetType, List<BattleUnit> allUnits, Integer targetIndex,
			String statusType, int statusTurns, List<BattleLogEntry> logs, int currentTurn) {
		List<BattleUnit> targets = new ArrayList<>();

		switch (targetType) {
		case "Pick":
			if (targetIndex != null) {
				List<BattleUnit> validTargets = new ArrayList<>();
				for (BattleUnit unit : allUnits) {
					if (!unit.getUnitType().equals("Player") && unit.isAlive()) {
						validTargets.add(unit);
					}
				}
				if (targetIndex < validTargets.size()) {
					targets.add(validTargets.get(targetIndex));
				}
			}
			break;
		case "All":
			for (BattleUnit unit : allUnits) {
				if (!unit.getUnitType().equals("Player") && unit.isAlive()) {
					targets.add(unit);
				}
			}
			break;
		case "Random":
			List<BattleUnit> aliveEnemies = new ArrayList<>();
			for (BattleUnit unit : allUnits) {
				if (!unit.getUnitType().equals("Player") && unit.isAlive()) {
					aliveEnemies.add(unit);
				}
			}
			if (!aliveEnemies.isEmpty()) {
				int randomIndex = CommonUtil.Dice(aliveEnemies.size());
				targets.add(aliveEnemies.get(randomIndex));
			}
			break;
		}

		for (BattleUnit target : targets) {
			applyStatusEffect(target, statusType, statusTurns, logs, currentTurn);
		}
	}

	private void applyStatusEffect(BattleUnit target, String statusType, int newTurns, List<BattleLogEntry> logs,
			int currentTurn) {
		Map<String, Integer> statusMap = target.getStatusEffects();
		if (statusMap == null) {
			statusMap = new HashMap<>();
			target.setStatusEffects(statusMap);
		}

		int currentTurns = statusMap.getOrDefault(statusType, 0);
		String logMessage;
		boolean applied = false;

		switch (statusType) {
		case STATUS_BURN:
			if (newTurns > currentTurns) { // 더 긴 쪽으로 결정
				statusMap.put(statusType, newTurns);
				logMessage = target.getName() + KoreanUtil.getJosa(target.getName(), "이 ", "가 ")
						+ "화상 상태에 걸렸습니다. (남은 시간 : " + newTurns + " 턴)";
				applied = true;
			} else {
				return;
			}
			break;
		case STATUS_POISON:
			int totalPoisonTurns = currentTurns + newTurns; // 턴 수 합산
			statusMap.put(statusType, totalPoisonTurns);
			logMessage = target.getName() + KoreanUtil.getJosa(target.getName(), "이 ", "가 ") + "중독 상태에 걸렸습니다. (남은 시간 : "
					+ totalPoisonTurns + " 턴)";
			applied = true;
			break;
		case STATUS_FREEZE:
		case STATUS_STUN:
			if (newTurns > currentTurns) { // 더 긴 쪽으로 결정
				statusMap.put(statusType, newTurns);
				String statusName = statusType.equals(STATUS_FREEZE) ? "빙결" : "기절";
				logMessage = target.getName() + KoreanUtil.getJosa(target.getName(), "이 ", "가 ") + statusName
						+ " 상태에 걸렸습니다. (남은 시간 : " + newTurns + " 턴)";
				applied = true;
			} else {
				return;
			}
			break;
		default:
			return;
		}
		if (applied) {
			logs.add(new BattleLogEntry(target.getName(), "Status_Applied", logMessage, currentTurn));
			log.info(logMessage);
		}else {
			log.debug(logMessage);
		}
	}

	private boolean isUnitDisabled(BattleUnit unit) { // 유닛이 행동 불가인가?
		Map<String, Integer> statusMap = unit.getStatusEffects();
		if (statusMap == null) {
			return false;
		}
		return (statusMap.getOrDefault(STATUS_FREEZE, 0) > 0 || statusMap.getOrDefault(STATUS_STUN, 0) > 0);
	}

	private String getDisableStatusName(BattleUnit unit) { // 행동 불가가 된 이유는?
		Map<String, Integer> statusMap = unit.getStatusEffects();
		if (statusMap == null) {
			return "";
		}
		if (statusMap.getOrDefault(STATUS_FREEZE, 0) > 0) {
			return "빙결";
		}
		if (statusMap.getOrDefault(STATUS_STUN, 0) > 0) {
			return "기절";
		}
		return "";
	}

	private void decreaseDisableStatusTurns(BattleUnit unit) { // 행동 불가 시 해당 상태 턴 수 줄이기 위함
		decreaseStatusTurns(unit, STATUS_FREEZE);
		decreaseStatusTurns(unit, STATUS_STUN);
	}

	private void decreaseStatusTurns(BattleUnit unit, String statusType) { // 상태 이상 턴 수 줄이고, 삭제하는 역할
		Map<String, Integer> statusMap = unit.getStatusEffects();
		if (statusMap == null) {
			return;
		}

		int currentTurns = statusMap.getOrDefault(statusType, 0);
		if (currentTurns > 0) {
			currentTurns--;
			if (currentTurns <= 0) {
				statusMap.remove(statusType);
			} else {
				statusMap.put(statusType, currentTurns);
			}
		}
	}

	private void applyDamageToUnit(BattleUnit unit, int damage) {
		if (unit.getUnitType().equals("Player")) {
			PlayerDto player = (PlayerDto) unit;
			player.setCurr_hp(Math.max(player.getCurr_hp() - damage, 0));
		} else if (unit.getUnitType().equals("Monster")) {
			BattleMonsterUnit monster = (BattleMonsterUnit) unit;
			monster.setHp(Math.max(monster.getHp() - damage, 0));
			if (monster.getHp() <= 0) {
				monster.setAlive(false);
			}
		}
	}

	private List<BattleUnit> createActionOrder(PlayerDto player, ArrayList<BattleMonsterUnit> enemy) {
		List<BattleUnit> allUnits = new ArrayList<>();
		allUnits.add(player);
		allUnits.addAll(enemy);

		allUnits.sort((a, b) -> {
			int initiativeA = a.getInitiative();
			int initiativeB = b.getInitiative();

			if (initiativeA != initiativeB) {
				return Integer.compare(initiativeB, initiativeA);
			}

			if (a.getUnitType().equals("Player"))
				return -1;
			if (b.getUnitType().equals("Player"))
				return 1;

			return 0;
		});

		return allUnits;
	}

	private BattleUnit getCurrentActionUnit(BattleSession session) {
		List<BattleUnit> actionOrder = session.getActionOrder();
		int currentActionIndex = session.getCurrentActionIndex();

		if (actionOrder.isEmpty() || currentActionIndex >= actionOrder.size()) {
			return null;
		}
		return actionOrder.get(currentActionIndex);
	}

	private List<BattleUnit> createAllUnitsList(BattleSession session) {
		List<BattleUnit> allUnits = new ArrayList<>();
		allUnits.add(session.getPlayer());
		allUnits.addAll(session.getEnemy());
		return allUnits;
	}

	private void moveToNextAction(BattleSession session) {
		int nextIndex = session.getCurrentActionIndex() + 1;

		if (nextIndex >= session.getActionOrder().size()) {
			nextIndex = 0;
			session.setCurrentTurn(session.getCurrentTurn() + 1);
			log.info("새로운 턴 시작 : " + session.getCurrentTurn() + " 턴");
		}

		session.setCurrentActionIndex(nextIndex);
	}

	private void addActionInfoToLogs(BattleResultDto result, int currentTurn) {
		if (result.getBattleLog() != null) {
			result.getBattleLog().forEach(log -> {
				log.setTurnNumber(currentTurn);
			});
		}
	}

	private void logActionOrder(List<BattleUnit> actionOrder) {
		log.info("=== 액션 순서 ===");
		for (int i = 0; i < actionOrder.size(); i++) {
			BattleUnit unit = actionOrder.get(i);
			log.info((i + 1) + ". " + unit.getName() + " (" + unit.getUnitType() + ", Initiative: "
					+ unit.getInitiative() + ")");
		}
	}

	// 임의의 세션 넘버 생성 함수
	private int generateSessionID() {
		return (int) (Math.random() * 10000);
	}

	// 적 생성 메서드
	private ArrayList<MonsterDto> Enemy(String session, int WhereStage) {
		ArrayList<MonsterDto> Enemy = new ArrayList<MonsterDto>();
		String type = null;

		if (WhereStage == 5) {
			type = "Middleboss";
		} else if (WhereStage == 10) {
			type = "boss";
		} else {
			type = "common";
		}

		List<MonsterDto> dto = monsterMapper.MonsterList(session, type);

		if (WhereStage == 1 || WhereStage == 2 || WhereStage == 3) {
			int n = CommonUtil.Dice(4);
			Enemy.add(dto.get(n));
		} else if (WhereStage == 4 || WhereStage == 6 || WhereStage == 7) {
			for (int a = 1; a <= CommonUtil.Dice(2); a++) {
				int n = CommonUtil.Dice(4);
				Enemy.add(dto.get(n));
			}
		} else if (WhereStage == 8 || WhereStage == 9) {
			for (int a = 1; a <= 2; a++) {
				int n = CommonUtil.Dice(4);
				Enemy.add(dto.get(n));
			}
		} else if (WhereStage == 5 || WhereStage == 10) {
			Enemy.add(dto.get(0));
		}
		return Enemy;
	}

	// 플레이어 정보 조회 메서드
	private PlayerDto getPlayerInfo(String PlayerID) {
		PlayerDto dto = characterStatusMapper.getPlayerInfo(PlayerID);
		return dto;
	}

}

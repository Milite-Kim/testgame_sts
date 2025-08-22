package com.milite.battle;

import java.util.*;

import com.milite.battle.abilities.FormChangeAbility;
import com.milite.battle.abilities.ModeSwitchAbility;
import com.milite.battle.artifacts.*;
import com.milite.constants.BattleConstants;
import com.milite.dto.PlayerDto;
import com.milite.util.KoreanUtil;

import lombok.Data;
import lombok.extern.log4j.Log4j;

@Log4j
@Data
public class BattleContext {

	/*
	 * BattleContext : 전투 행동에 대한 정의 및 실행에 대한 파일
	 * 
	 * 【핵심 개념】 전투 중 발생하는 모든 "게임 용어"를 "실제 동작"으로 변환하는 행동 사전
	 *  "유닛을 힐한다" = 현재체력 증가 (최대체력 초과 불가) 
	 *  "데미지를 준다" = 방어력 적용 후 체력 감소 (최소 1 피해)
	 *  "상태이상을 건다" = 지연 큐에 등록하여 나중에 일괄 처리
	 * 
	 * 【주요 책임】
	 *  1. 게임 규칙 강제: 체력 제한, 피해 최소값, 상태이상 중복 등
	 *  2. 지연 액션 관리: 반사 데미지, 상태이상 등 턴 종료 후 처리할 일들
	 *  3. 로그 생성: 모든 행동에 대한 상세 기록
	 *  4. 상태이상 통합 관리: 적용/효과/해제를 일관되게 처리
	 * 
	 * 【다른 클래스와의 관계】
	 *  - BattleSession: 전투 규칙 담당, Context에게 "행동 실행" 요청
	 *  - BattleServiceImpl: 시스템 관리 담당, Session을 통해 Context 간접 사용
	 *  - 특수능력/아티팩트: Context를 통해서만 게임 상태 변경 가능
	 * 
	 * 【설계 원칙
	 *  - 모든 게임 상태 변경은 반드시 이 클래스를 거쳐야 함
	 *  - 직접적인 HP/상태 조작 금지, 규칙이 적용된 메서드만 제공
	 *  - 지연 액션으로 복잡한 연쇄 반응을 안전하게 처리
	 */

	private BattleSession session;
	private int currentTurn;
	private List<BattleLogEntry> logs = new ArrayList<>();
	private List<DelayedAction> delayedActions = new ArrayList<>();

	public BattleContext(BattleSession session, int currentTurn) {
		this.session = session;
		this.currentTurn = currentTurn;
		this.logs = new ArrayList<>();
		this.delayedActions = new ArrayList<>();
	}

	// 지연 액션 리스트에 반사 데미지를 등록해둠
	public void addReflectDamage(BattleUnit target, int damage) {
		delayedActions.add(new ReflectDamageAction(target, damage));
		log.debug(target.getName() + "에게 " + damage + "의 반사 피해가 예약되었습니다.");
	}

	// 지연 액션에 상태이상 처리 등록해둠(아래의 메서드로 이관함)
	public void addStatusEffect(BattleUnit target, String statusType, int turns) {
		addStatusEffect(target, statusType, turns, null);
	}
	
	// 지연 액션에 상태이상 처리 등록해둠
	public void addStatusEffect(BattleUnit target, String statusType, int turns, BattleUnit caster) {
		delayedActions.add(new StatusEffectAction(target, statusType, turns, caster));
		log.debug(target.getName() + "에게 " + statusType + " 상태이상(" + turns + " 턴)이 예약되었습니다.");
	}

	// 몬스터 소환 특수 능력 예약
	public void addMonsterSummon(String monsterID, int count) {
		delayedActions.add(new SummonAction(monsterID, count));
		log.debug("몬스터 소환이 예약되었습니다 : " + monsterID + " x" + count);
	}

	// 유닛의 체력 회복에 대한 처리(최대 체력을 넘지 않고 실제로 회복된 양을 반환함)
	public int healUnit(BattleUnit unit, int amount) {
		if (unit.getUnitType().equals("Player")) {
			PlayerDto player = (PlayerDto) unit;
			int currentHp = player.getCurr_hp();
			int maxHp = player.getMax_hp();
			int newHp = Math.min(currentHp + amount, maxHp);
			int actualHealed = newHp - currentHp;

			player.setCurr_hp(newHp);

			if (actualHealed > 0) {
				addLogEntry(
						unit.getName() + KoreanUtil.getJosa(unit.getName(), "이 ", "가 ") + actualHealed + "만큼 회복하였습니다");
				log.info(unit.getName() + " 회복 : " + actualHealed + " ( HP : " + currentHp + " -> " + newHp + ")");
			}

			return actualHealed;
		} else if (unit.getUnitType().equals("Monster")) {
			BattleMonsterUnit monster = (BattleMonsterUnit) unit;
			int currentHp = monster.getHp();
			int maxHp = monster.getMax_hp();
			int newHp = Math.min(currentHp + amount, maxHp);
			int actualHealed = newHp - currentHp;

			monster.setHp(newHp);

			if (actualHealed > 0) {
				addLogEntry(
						unit.getName() + KoreanUtil.getJosa(unit.getName(), "이 ", "가 ") + actualHealed + "만큼 회복하였습니다");
				log.info(unit.getName() + " 회복 : " + actualHealed + " ( HP : " + currentHp + " -> " + newHp + ")");
			}
			return actualHealed;
		}
		return 0;
	}

	// 유닛에게 피해를 적용(피해 감소, 아티팩트 효과, 부활에 대한 체크 적용)
	public void damageUnit(BattleUnit unit, int damage) {
		int finalDamage = damage;

		if (unit.getUnitType().equals("Player") && damage > 0) {
			PlayerDto player = (PlayerDto) unit;
			for (PlayerArtifact artifact : player.getArtifacts()) {
				if (artifact instanceof SlipperyLeatherArtifact) {
					SlipperyLeatherArtifact leather = (SlipperyLeatherArtifact) artifact;
					if (leather.canUse()) {
						leather.useEffect();
						finalDamage = 0;

						addLogEntry(unit.getName() + "의 미끄러운 가죽 보호대가 " + damage + " 피해를 완전히 무효화했습니다!");
						break;
					}
				}
			}
		}

		if (unit.getUnitType().equals("Monster")) {
			finalDamage = applyDefenseReduction(unit, damage);
		}

		if (unit.getUnitType().equals("Player")) {
			PlayerDto player = (PlayerDto) unit;
			int currentHp = player.getCurr_hp();
			int newHp = Math.max(currentHp - finalDamage, 0);

			if (newHp <= 0 && currentHp > 0) {
				boolean revived = checkAndExecuteRevival(player);
				if (revived) {
					return;
				}
			}

			player.setCurr_hp(newHp);

			addLogEntry(unit.getName() + KoreanUtil.getJosa(unit.getName(), "이 ", "가 ") + finalDamage + "의 피해를 받았습니다");
			log.info(unit.getName() + " 피해: " + finalDamage + " (HP: " + currentHp + " → " + newHp + ")");
		} else if (unit.getUnitType().equals("Monster")) {
			BattleMonsterUnit monster = (BattleMonsterUnit) unit;
			int currentHp = monster.getHp();
			int newHp = Math.max(currentHp - finalDamage, 0);
			monster.setHp(newHp);

			if (newHp <= 0) {
				monster.setAlive(false);
				addLogEntry(unit.getName() + KoreanUtil.getJosa(unit.getName(), "이 ", "가 ") + finalDamage
						+ "의 피해를 입고 쓰러졌습니다.");
				log.info(unit.getName() + " 사망 : " + finalDamage + " 피해");
			} else {
				addLogEntry(unit.getName() + KoreanUtil.getJosa(unit.getName(), "이 ", "가 ") + finalDamage
						+ "의 피해를 받았습니다. (HP: " + currentHp + " → " + newHp + ")");
				log.info(unit.getName() + " 피해: " + finalDamage + " (HP: " + currentHp + " → " + newHp + ")");
			}
		}
	}

	// 부활 메커니즘 처리
	private boolean checkAndExecuteRevival(PlayerDto player) {
		for (PlayerArtifact artifact : player.getArtifacts()) {
			if (artifact instanceof PhoenixFeatherArtifact) {
				PhoenixFeatherArtifact feather = (PhoenixFeatherArtifact) artifact;
				if (feather.canRevive()) {
					return feather.executeRevival(player, this);
				}
			}
		}
		return false;
	}

	// 몬스터가 가지는 받는 피해 감소 적용
	private int applyDefenseReduction(BattleUnit unit, int damage) {
		if (!(unit instanceof BattleMonsterUnit)) {
			return damage;
		}

		BattleMonsterUnit monster = (BattleMonsterUnit) unit;
		double defenseMultiplier = 1.0;

		if ("FormChange".equals(monster.getSpecial())) {
			defenseMultiplier = FormChangeAbility.getDefenseMultiplier(monster, getCurrentTurn());
		} else if ("ModeSwitch".equals(monster.getSpecial())) {
			defenseMultiplier = ModeSwitchAbility.getDefenseMulitplier(monster, getCurrentTurn());
		}

		int finalDamage = (int) Math.round(damage / defenseMultiplier);
		return Math.max(finalDamage, 1);
	}

	// 전투 로그에 시스템 메시지 추가하기
	public void addLogEntry(String message) {
		BattleLogEntry logEntry = new BattleLogEntry("System", "special", message, currentTurn);
		logs.add(logEntry);
	}

	// 전투 로그에 액션 내용을 포함한 시스템 메세지 추가하기
	public void addLogEntry(String actorName, String actionType, String message) {
		BattleLogEntry logEntry = new BattleLogEntry(actorName, actionType, message, currentTurn);
		logs.add(logEntry);
	}

	// 지연 액션 리스트 실행
	public void executeDelayedActions() {
		if (delayedActions.isEmpty()) {
			return;
		}

		log.info("지연된 액션 " + delayedActions.size() + " 개 실행");

		List<DelayedAction> actionsToExecute = new ArrayList<>(delayedActions);
		delayedActions.clear();

		for (DelayedAction action : actionsToExecute) {
			try {
				action.execute(this);
			} catch (Exception e) {
				log.error("지연된 액션 실행 중 오류 발생 : " + e.getMessage(), e);
				addLogEntry("특수능력 실행 중 오류 발생");
			}
		}

		if (!delayedActions.isEmpty()) {
			executeDelayedActions();
		}

		log.info("지연된 액션 실행 완료");
	}

	// 배틀 유닛을 하나의 BattleUnit 이라는 자료형으로 묶기
	public List<BattleUnit> getAllUnits() {
		List<BattleUnit> allUnits = new ArrayList<>();
		allUnits.add(session.getPlayer());
		allUnits.addAll(session.getEnemy());
		return allUnits;
	}

	// 현재 전투에서 살아있는 적들의 목록 반환하기
	public List<BattleUnit> getAliveEnemies() {
		return session.getEnemy().stream().filter(BattleUnit::isAlive).collect(java.util.stream.Collectors.toList());
	}

	// 플레이어 캐릭터가 살아있는지 반환
	public boolean isPlayerAlive() {
		return session.getPlayer().isAlive();
	}

	// 모든 몬스터가 사망했는지 확인
	public boolean areAllEnemiesDead() {
		return session.getEnemy().stream().noneMatch(BattleUnit::isAlive);
	}

	// 전투에서 세부 내용을 로그에 추가하기
	public void addDetailedLog(String actorName, String actionType, String message) {
		String detailedMessage = String.format("[턴 %d] %s", currentTurn, message);
		BattleLogEntry logEntry = new BattleLogEntry(actorName, actionType, detailedMessage, currentTurn);
		logs.add(logEntry);
		log.debug("상세 로그 추가 : " + detailedMessage);
	}

	// 로그 불러오기
	public List<BattleLogEntry> getLogs() {
		return new ArrayList<>(logs);
	}

	// 세션 반환하기
	public BattleSession getSession() {
		return session;
	}

	// 현재 턴 수 반환하기
	public int getCurrentTurn() {
		return currentTurn;
	}

	// 지연 액션 리스트가 남아있는지 확인하기
	public boolean hasDelayedActions() {
		return !delayedActions.isEmpty();
	}

	// 현재 지연 액션 리스트가 얼마나 남아있는지 확인하기
	public int getDelayedActionCount() {
		return delayedActions.size();
	}

	// 상태 이상 관련 처리 진행하기(피해, 남은 턴 수 감소, 해제 등등)
	public void processAllStatusEffects(BattleUnit unit) {
		Map<String, Integer> statusMap = unit.getStatusEffects();
		if (statusMap == null || statusMap.isEmpty()) {
			return;
		}

		if (statusMap.containsKey(BattleConstants.STATUS_BURN) && statusMap.get(BattleConstants.STATUS_BURN) > 0) {
			int burnDamage = calculateBurnDamage(unit);
			applyStatusDamage(unit, BattleConstants.STATUS_BURN, burnDamage);
			decreaseStatusTurns(unit, BattleConstants.STATUS_BURN);
		}

		if (statusMap.containsKey(BattleConstants.STATUS_POISON) && statusMap.get(BattleConstants.STATUS_POISON) > 0) {
			int poisonDamage = statusMap.get(BattleConstants.STATUS_POISON);
			applyStatusDamage(unit, BattleConstants.STATUS_POISON, poisonDamage);
			decreaseStatusTurns(unit, BattleConstants.STATUS_POISON);
		}

		decreaseStatusTurns(unit, BattleConstants.STATUS_BLIND);
		decreaseStatusTurns(unit, BattleConstants.STATUS_FREEZE);
		decreaseStatusTurns(unit, BattleConstants.STATUS_STUN);
	}

	// 상태이상의 턴 수를 1턴 줄이는 메서드
	public void decreaseStatusTurns(BattleUnit unit, String statusType) {
		Map<String, Integer> statusMap = unit.getStatusEffects();
		if (statusMap == null) {
			return;
		}

		int currentTurns = statusMap.getOrDefault(statusType, 0);
		if (currentTurns > 0) {
			currentTurns--;
			if (currentTurns <= 0) {
				statusMap.remove(statusType);
				addLogEntry(unit.getName(), "status_clear", unit.getName()
						+ KoreanUtil.getJosa(unit.getName(), "의 ", "의 ") + getStatusName(statusType) + "이 해제되었습니다.");
			} else {
				statusMap.put(statusType, currentTurns);
			}
		}
	}

	// 상태 이상의 데미지가 0보다 크다면 데미지를 입히고, 로그 남기기
	public void applyStatusDamage(BattleUnit unit, String statusType, int damage) {
		if (damage > 0) {
			damageUnit(unit, damage);
			addLogEntry(unit.getName(), statusType + "_damage",
					unit.getName() + KoreanUtil.getJosa(unit.getName(), "이 ", "가 ") + getStatusName(statusType) + "으로 "
							+ damage + "의 피해를 받았습니다.");
		}
	}

	// 아티팩트에 따른 최종적으로 입힐 화상 데미지 계산
	public int calculateBurnDamage(BattleUnit unit) {
		int baseBurnDamage = BattleConstants.getBurnDamage();

		if (unit.getUnitType().equals("Player")) {
			PlayerDto player = (PlayerDto) unit;

			for (PlayerArtifact artifact : player.getArtifacts()) {
				if (artifact instanceof DryWoodArtifact) {
					DryWoodArtifact dryWood = (DryWoodArtifact) artifact;
					baseBurnDamage += dryWood.getBurnDamageBonus();
				}
			}
		}

		return baseBurnDamage;
	}

	//스테이터스 이름 반환
	public String getStatusName(String statusType) {
		switch (statusType) {
		case BattleConstants.STATUS_BURN:
			return "화상";
		case BattleConstants.STATUS_POISON:
			return "중독";
		case BattleConstants.STATUS_FREEZE:
			return "빙결";
		case BattleConstants.STATUS_STUN:
			return "기절";
		case BattleConstants.STATUS_BLIND:
			return "실명";
		default:
			return statusType;
		}
	}

	// 중독 관련 아티팩트가 있을 경우 해당 내용 적용
	void applyPoisonArtifactEffects(BattleUnit caster, BattleUnit target) {
		if (caster == null || !caster.getUnitType().equals("Player")) {
			return;
		}

		PlayerDto player = (PlayerDto) caster;
		Map<String, Integer> targetStatusEffects = target.getStatusEffects();

		if (targetStatusEffects == null) {
			return;
		}

		int existingPoisonTurns = targetStatusEffects.getOrDefault(BattleConstants.STATUS_POISON, 0);

		for (PlayerArtifact artifact : player.getArtifacts()) {
			if (artifact instanceof PoisonNeedleArtifact) {
				PoisonNeedleArtifact needleArtifact = (PoisonNeedleArtifact) artifact;

				if (existingPoisonTurns > 0) {
					int additionalDamage = needleArtifact.calculateStackDamage(existingPoisonTurns);

					if (additionalDamage > 0) {
						damageUnit(target, additionalDamage);
						addLogEntry(caster.getName(), "poison_needle_effect",
								caster.getName() + KoreanUtil.getJosa(caster.getName(), "의 ", "의 ") + "바늘 달린 독 장치가 "
										+ target.getName() + "에게 추가로 " + additionalDamage + "의 중독 피해를 가했습니다! (기존 "
										+ existingPoisonTurns + " 스택)");
					}
				}
				break; // 하나만 적용
			}
		}
	}
}

// 지연 액션에 대한 내용
interface DelayedAction {
	void execute(BattleContext context);
}

class ReflectDamageAction implements DelayedAction {
	private final BattleUnit target;
	private final int damage;

	public ReflectDamageAction(BattleUnit target, int damage) {
		this.target = target;
		this.damage = damage;
	}

	@Override
	public void execute(BattleContext context) {
		if (!target.isAlive()) {
			context.addLogEntry("반사 피해 대상이 사망하여 취소되었습니다.");
			return;
		}

		context.damageUnit(target, damage);
		context.addLogEntry("System", "reflect_damage",
				target.getName() + KoreanUtil.getJosa(target.getName(), "이 ", "가 ") + damage + "의 반사 피해를 받았습니다.");
	}
}

// 상태이상에 대한 내용
class StatusEffectAction implements DelayedAction {
	private final BattleUnit target;
	private final String statusType;
	private final int turns;
	private final BattleUnit caster;

	public StatusEffectAction(BattleUnit target, String statusType, int turns) {
		this(target, statusType, turns, null);
	}

	public StatusEffectAction(BattleUnit target, String statusType, int turns, BattleUnit caster) {
		this.target = target;
		this.statusType = statusType;
		this.turns = turns;
		this.caster = caster;
	}

	@Override
	public void execute(BattleContext context) {
		if (!target.isAlive()) {
			context.addLogEntry("상태이상 대상이 이미 사망하였기에 취소되었습니다.");
			return;
		}

		Map<String, Integer> statusEffects = target.getStatusEffects();
		if (statusEffects == null) {
			statusEffects = new HashMap<>();
			target.setStatusEffects(statusEffects);
		}

		if (BattleConstants.STATUS_POISON.equals(statusType)) {
			context.applyPoisonArtifactEffects(caster, target);
		}

		int currentTurns = statusEffects.getOrDefault(statusType, 0);
		int newTurns = Math.max(currentTurns, turns);

		statusEffects.put(statusType, newTurns);

		if (currentTurns > 0) {
			context.addLogEntry("System", "status_refresh",
					target.getName() + "의 " + statusType + " 상태 지속시간이 " + newTurns + "턴으로 갱신되었습니다.");
		} else {
			context.addLogEntry("System", "status_effect",
					target.getName() + KoreanUtil.getJosa(target.getName(), "이 ", "가 ") + statusType + " 상태에 걸렸습니다. ("
							+ newTurns + "턴)");
		}
	}
}

class SummonAction implements DelayedAction {
	private final String monsterID;
	private final int count;

	public SummonAction(String monsterID, int count) {
		this.monsterID = monsterID;
		this.count = count;
	}

	@Override
	public void execute(BattleContext context) {
		// todo 로직 구현해야함
		context.addLogEntry("System", "summon", "몬스터 소환 시도 : " + monsterID + " x" + count + " (아직 미구현)");
	}
}
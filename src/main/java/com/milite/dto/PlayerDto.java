package com.milite.dto;

import lombok.*;

import com.milite.battle.BattleContext;
import com.milite.battle.BattleUnit;
import com.milite.battle.artifacts.PlayerArtifact;

import java.util.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlayerDto implements BattleUnit {
	String PlayerID;
	String Using_Character;
	int curr_hp;
	int max_hp;
	int atk;
	int luck;
	String WhereSession;
	int WhereStage;
	Map<String, Integer> statusEffects = new HashMap<>();
	// private booleanSwiftSkill = false;

	private List<PlayerArtifact> artifacts = new ArrayList<>();

	@Override
	public String getName() {
		return this.Using_Character;
	}

	@Override
	public int getHp() {
		return this.curr_hp;
	}

	@Override
	public boolean isAlive() {
		return this.curr_hp > 0;
	}

	@Override
	public String getUnitType() {
		return "Player";
	}

	@Override
	public boolean hasPlayerPriority() {
		return true;
	}

	@Override
	public Map<String, Integer> getStatusEffects() {
		return this.statusEffects;
	}

	@Override
	public void setStatusEffects(Map<String, Integer> statusEffects) {
		this.statusEffects = statusEffects;
	}

	public void executeArtifactsOnAttack(BattleUnit target, BattleContext context) {
		for (PlayerArtifact artifact : artifacts) {
			if (artifact != null) {
				artifact.onPlayerAttack(this, target, context);
			}
		}
	}

	public void executeArtifactsOnHit(BattleUnit target, int damageDealt, BattleContext context) {
		for (PlayerArtifact artifact : artifacts) {
			if (artifact != null) {
				artifact.onPlayerHit(this, target, damageDealt, context);
			}
		}
	}

	public void executeArtifactsOnDefensePerHit(BattleUnit attacker, int damage, BattleContext context) {
		for (PlayerArtifact artifact : artifacts) {
			if (artifact != null) {
				artifact.onPlayerDefensePerHit(this, attacker, damage, context);
			}
		}
	}

	public void executeArtifactsOnDefensePerTurn(BattleUnit attacker, int damage, BattleContext context) {
		for (PlayerArtifact artifact : artifacts) {
			if (artifact != null) {
				artifact.onPlayerDefensePerTurn(this, attacker, damage, context);
			}
		}
	}

	public void executeArtifactsOnTurnStart(BattleContext context) {
		for (PlayerArtifact artifact : artifacts) {
			if (artifact != null) {
				artifact.onPlayerTurnStart(this, context);
			}
		}
	}

	public void executeArtifactsOnTurnEnd(BattleContext context) {
		for (PlayerArtifact artifact : artifacts) {
			if (artifact != null) {
				artifact.onPlayerTurnEnd(this, context);
			}
		}
	}

	public void addArtifact(PlayerArtifact artifact) {
		if(artifact != null) {// 해당 아티팩트가 이미 보유 중인지는 전 단계에서 검사하는 걸로 결정
			artifacts.add(artifact);
		}
	}
	
	public void removeArtifact(PlayerArtifact artifact) {
		artifacts.remove(artifact);
	}
	
	public List<PlayerArtifact> getArtifacts(){
		return new ArrayList<>(artifacts);
	}
	
	public boolean hasArtifact(String artifactName) {
		return artifacts.stream().anyMatch(artifact -> artifact.getArtifactName().equals(artifactName));
	}
	
	public int getArtifactCount() {
		return artifacts.size();
	}
	
}

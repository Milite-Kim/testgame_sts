package com.milite.battle.artifacts;

import java.util.*;

public class PlayerArtifactFactory {
	private static final Map<String, PlayerArtifact> artifacts = new HashMap<>();

	static {
		artifacts.put("ElementStone", new ElementStoneArtifact());
		
		artifacts.put("FighterGuildMedal", new FighterGuildMedalArtifact());
		artifacts.put("BurningLavaStone", new BurningLavaStoneArtifact());
		artifacts.put("BlueTrident", new BlueTridentArtifact());
		artifacts.put("DruidBelt", new DruidBeltArtifact());
	}
	
	public static PlayerArtifact getArtifact(String artifactName) {
		return artifacts.get(artifactName);
	}
}

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

		artifacts.put("DryWood", new DryWoodArtifact());
		artifacts.put("PoisonNeedle", new PoisonNeedleArtifact());

		artifacts.put("BlurryLens", new BlurryLensArtifact());
		artifacts.put("GrayCloak", new GrayCloakArtifact());

		artifacts.put("SeaHeart", new SeaHeartArtifact());
		artifacts.put("DeapsPearl", new DeepsPearlArtifact());
		artifacts.put("BlackCoral", new BlackCoralArtifact());
	}

	public static PlayerArtifact getArtifact(String artifactName) {
		return artifacts.get(artifactName);
	}
}

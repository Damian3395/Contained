package com.contained.game;

import com.contained.game.util.MiniGameUtil;
import com.contained.game.util.Resources;
import com.contained.game.util.Util;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.common.config.Configuration;

/**
 * Loads and stores information from the mod's configuration files.
 */
public class Settings {	
	
	public boolean creativeOverride;
		
	public int[] maxTeamSize = new int[3];
	public int[] worldRadius = new int[3];
	public int[] numChunks = new int[3];
	public int[] gameDuration = new int[3];
	public int[] gameNumTeams = new int[3];
	public int[] flagXPCost = new int[2];
	public int[] claimDelay = new int[2];
	public int[] claimRadius = new int[2];
	public int[] antiClaimDelay = new int[2];
	public int[] antiClaimRadius = new int[2];
	public int[] largeTeamSize = new int[2];
	public int[] minOreRegen = new int[2];   //Min amount of time in seconds before ores regenerate.
	public int[] maxOreRegen = new int[2];  //Max amount of time in seconds before ores regenerate.
	
	public int pvpTerritorySize;
	public int pvpMaxLives;
	public int pvpResurrectLives;
	public int treasureChests;
	
	public boolean[] harvestRequiresTerritory = new boolean[2];
	public boolean[] enableDowsing = new boolean[2];
	
	public Settings(FMLPreInitializationEvent event) {
		Configuration config = new Configuration(event.getSuggestedConfigurationFile());
		config.load();
		
		creativeOverride = config.getBoolean("creativeOverride", Configuration.CATEGORY_GENERAL, true, 
				"Should a player in creative mode be exempt from the protection rules of a territory?");
				
		for(int i=0; i<=1; i++) {
			String category = "lobby_settings";
			if (i == Resources.MINIGAME)
				category = "minigame_settings";
			
			largeTeamSize[i] = config.getInt("largeTeamRequirement", category, 
					defaultValue(i, 100, 0), 0, 99999,
					"How many blocks of land must a team own before their territory is vulnerable to invasion from other teams?");
			flagXPCost[i] = config.getInt("flagXPCost", category, 
					defaultValue(i, 15, 10), 0, 999, 
					"How many Minecraft levels do you need to use the flag item?");
			claimRadius[i] = config.getInt("claimRadius", category, 
					defaultValue(i, 2, 3), 0, 10, 
					"What is the radius, in blocks, that the Territory Machines can claim land?");
			claimDelay[i] = config.getInt("claimDelay", category, 
					defaultValue(i, 90, 10), 1, 60000, 
					"How long, in seconds, does it take for the Territory Machine to claim a block of land?");
			antiClaimRadius[i] = config.getInt("antiClaimRadius", category, 
					defaultValue(i, 2, 2), 0, 10, 
					"What is the radius, in blocks, that the Anti-Territory Machines can steal land?");
			antiClaimDelay[i] = config.getInt("antiClaimDelay", category, 
					defaultValue(i, 90, 15), 1, 60000, 
					"How long, in seconds, does it take for the Anti-Territory Machine to steal a block of land?");
			minOreRegen[i] = config.getInt("minOreRegen", category, 
					defaultValue(i, 5400, 0), 0, 5000000, 
					"Minimum time (in seconds) before harvested ores regenerate.");
			maxOreRegen[i] = config.getInt("maxOreRegen", category, 
					defaultValue(i, 10000, 0), 0, 5000000, 
					"Maximum time (in seconds) before harvested ores regenerate.");
			harvestRequiresTerritory[i] = config.getBoolean("harvestTerritoryLock", category, 
					defaultValue(i, true, false), 
					"Are you only allowed to harvest ores in areas that you own the territory of?");
			enableDowsing[i] = config.getBoolean("enableDowsing", category, 
					defaultValue(i, false, true), 
					"Give players access to the dowsing rod item for finding ore veins?");
		}
		
		for(int i=0; i<=2; i++) {
			String category = "lobby_settings";
			String prefix = "";
			if (i > 0)
				category = "minigame_settings";
			if (i == Resources.PVP) {
				prefix = "pvp_";
				pvpTerritorySize = config.getInt(prefix+"territorySize", category, 
						3, 1, 99, 
						"What is the radius of the territory that the team starts with?");	
				pvpMaxLives = config.getInt(prefix+"maxLives", category, 
						5, 1, 99, 
						"What is the lives cap for a player (also the amount they start with)?");
				pvpResurrectLives = config.getInt(prefix+"resurrectLives", category, 
						3, 1, 99, 
						"How many lives does a player get back if resurrected with the life rod?");
			}
			if (i == Resources.TREASURE) {
				prefix = "treasure_";
				treasureChests = config.getInt(prefix+"numChests", category, 
						10, 1, 200, 
						"How many chests are actively spawned at any time during the treasure game?");
			}
			
			worldRadius[i] = config.getInt(prefix+"worldSize", category, 
					defaultValue(i, 40, 6, 15), 0, 500, 
					"Radius of the finite world in chunks (16x16 blocks), centered around spawn.");			
			maxTeamSize[i] = config.getInt(prefix+"maxTeamSize", category, 
					defaultValue(i, 5, 5, 5), 1, 999, 
					"What is the maximum player capacity of a team?");	
			
			if (i != Resources.OVERWORLD) {
				gameDuration[i] = config.getInt(prefix+"Duration", category, 
						defaultValue(i, 0, 2400, 2400), 1, 5000000, 
						"Time (in seconds) that the mini-game lasts.");
				gameNumTeams[i] = config.getInt(prefix+"NumTeams", category, 
						defaultValue(i, 0, 2, 2), 1, 100, 
						"Number of player teams that will participate in the mini-game.");
			}
		}
		
		recalculateNumChunks();		
		Contained.world.preInit(event, config);
		
		config.save();
	}
	
	private int defaultValue(int type, int overworldValue, int minigameValue) {
		if (type == Resources.OVERWORLD)
			return overworldValue;
		else
			return minigameValue;
	}
	
	private boolean defaultValue(int type, boolean overworldValue, boolean minigameValue) {
		if (type == Resources.OVERWORLD)
			return overworldValue;
		else
			return minigameValue;
	}
	
	private int defaultValue(int type, int overworldValue, int pvpValue, int treasureValue) {
		if (type == Resources.OVERWORLD)
			return overworldValue;
		else if (type == Resources.PVP)
			return pvpValue;
		else
			return treasureValue;
	}
	
	public static int getDimConfig(int dimID) {
		if (MiniGameUtil.isPvP(dimID) || MiniGameUtil.isTreasure(dimID))
			return Resources.MINIGAME;
		else
			return Resources.OVERWORLD;
	}
	
	public static int getGameConfig(int dimID) {
		if (MiniGameUtil.isPvP(dimID))
			return Resources.PVP;
		else if (MiniGameUtil.isTreasure(dimID))
			return Resources.TREASURE;
		else
			return Resources.OVERWORLD;
	}
	
	public int getWorldRadius(int dimID) {
		if (MiniGameUtil.isPvP(dimID))
			return worldRadius[Resources.PVP];
		else if (MiniGameUtil.isTreasure(dimID))
			return worldRadius[Resources.TREASURE];
		else
			return worldRadius[Resources.OVERWORLD];
	}
	
	public void setWorldRadius(int dimID, int radius) {
		if (MiniGameUtil.isPvP(dimID))
			worldRadius[Resources.PVP] = radius;
		else if (MiniGameUtil.isTreasure(dimID))
			worldRadius[Resources.TREASURE] = radius;
		else
			worldRadius[Resources.OVERWORLD] = radius;	
		recalculateNumChunks();
	}
	
	public int getNumChunks(int dimID) {
		if (MiniGameUtil.isPvP(dimID))
			return numChunks[Resources.PVP];
		else if (MiniGameUtil.isTreasure(dimID))
			return numChunks[Resources.TREASURE];
		else
			return numChunks[Resources.OVERWORLD];
	}
	
	private void recalculateNumChunks() {
		for(int i=0; i<=2; i+=1) 
			numChunks[i] = 0;
		int maxRadius = Math.max(Math.max(worldRadius[Resources.OVERWORLD], worldRadius[Resources.PVP]), worldRadius[Resources.TREASURE]);
		for(int chunkX=-(maxRadius+Resources.wastelandPadding); 
				chunkX<=(maxRadius+Resources.wastelandPadding); chunkX++) {
			for(int chunkZ=-(maxRadius+Resources.wastelandPadding); 
					chunkZ<=(maxRadius+Resources.wastelandPadding); chunkZ++) {
				float distDiff = Util.euclidDist(0, 0, chunkX, chunkZ);
				for(int i=0; i<=2; i+=1) {
					if (distDiff <= (worldRadius[i]+Resources.wastelandPadding))
						numChunks[i]++;
				}
			}
		}
	}
	
}

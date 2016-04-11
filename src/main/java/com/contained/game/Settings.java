package com.contained.game;

import com.contained.game.util.MiniGameUtil;
import com.contained.game.util.Resources;
import com.contained.game.util.Util;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;

/**
 * Loads and stores information from the mod's configuration files.
 */
public class Settings {	
	
	public boolean creativeOverride;
	
	public static final int OVERWORLD = 0;
	public static final int MINIGAME = 1;
	
	public int[] maxTeamSize = new int[2];
	public int[] flagXPCost = new int[2];
	public int[] claimDelay = new int[2];
	public int[] claimRadius = new int[2];
	public int[] antiClaimDelay = new int[2];
	public int[] antiClaimRadius = new int[2];
	public int[] largeTeamSize = new int[2];
	public int[] minOreRegen = new int[2];   //Min amount of time in seconds before ores regenerate.
	public int[] maxOreRegen = new int[2];  //Max amount of time in seconds before ores regenerate.
	
	public int[] smallGemEXPCost = new int[2];
	public int[] smallGemCount = new int[2];
	public int[] bulkGemEXPCost = new int[2];
	public int[] bulkGemCount = new int[2];
	public int[] terrMachineEXPCost = new int[2];
	
	public boolean[] harvestRequiresTerritory = new boolean[2];
	public boolean[] enableDowsing = new boolean[2];
	
	public int treasureDuration;
	public int pvpDuration;
	
	public int worldRadius;
	public int pvpRadius;
	public int treasureRadius;
	public int numWorldChunks; //Total number of chunks in the finite world.
	public int numPvPChunks;
	public int numTreasureChunks;
	
	public Settings(FMLPreInitializationEvent event) {
		Configuration config = new Configuration(event.getSuggestedConfigurationFile());
		config.load();
		
		treasureDuration = config.getInt("treasureDuration", Configuration.CATEGORY_GENERAL, 2400, 1, 5000000, 
				"Time (in seconds) that the Treasure Hunting mini-game lasts.");
		pvpDuration = config.getInt("pvpDuration", Configuration.CATEGORY_GENERAL, 2400, 1, 5000000, 
				"Time (in seconds) that the PvP mini-game lasts.");
		creativeOverride = config.getBoolean("creativeOverride", Configuration.CATEGORY_GENERAL, true, 
				"Should a player in creative mode be exempt from the protection rules of a territory?");
		
		for(int i=0; i<=1; i++) {
			String category = "lobby_settings";
			if (i == MINIGAME)
				category = "minigame_settings";
			
			if (i == OVERWORLD)
				worldRadius = config.getInt("worldSize", category, 
					40, 0, 500, 
					"Radius of the finite world in chunks (16x16 blocks), centered around spawn.");
			else if (i == MINIGAME) {
				pvpRadius = config.getInt("pvpWorldSize", category, 
					15, 0, 500, 
					"Radius of the PvP worlds in chunks (16x16 blocks), centered around spawn.");
				treasureRadius = config.getInt("treasureWorldSize", category, 
					25, 0, 500, 
					"Radius of the treasure hunting worlds in chunks (16x16 blocks), centered around spawn.");
			}
			
			largeTeamSize[i] = config.getInt("largeTeamRequirement", category, 
					defaultValue(i, 100, 0), 0, 99999,
					"How many blocks of land must a team own before their territory is vulnerable to invasion from other teams?");
			maxTeamSize[i] = config.getInt("maxTeamSize", category, 
					defaultValue(i, 5, 5), 1, 999, 
					"What is the maximum player capacity of a team?");
			flagXPCost[i] = config.getInt("flagXPCost", category, 
					defaultValue(i, 30, 10), 0, 999, 
					"How many Minecraft levels do you need to use the flag item?");
			claimRadius[i] = config.getInt("claimRadius", category, 
					defaultValue(i, 2, 3), 0, 10, 
					"What is the radius, in blocks, that the Territory Machines can claim land?");
			claimDelay[i] = config.getInt("claimDelay", category, 
					defaultValue(i, 90, 10), 1, 60000, 
					"How long, in seconds, does it take for the Territory Machine to claim a block of land?");
			antiClaimRadius[i] = config.getInt("antiClaimRadius", category, 
					defaultValue(i, 2, 3), 0, 10, 
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
			
			smallGemEXPCost[i] = config.getInt("shopSmallGemCost", category, 
					defaultValue(i, 1, 1), 1, 999, 
					"How much EXP does it cost to purchase a territory gem? (small quantity)");
			smallGemCount[i] = config.getInt("shopSmallGemCount", category, 
					defaultValue(i, 1, 5), 1, 64, 
					"How many territory gems do you get for the purchase? (small quantity)");
			bulkGemEXPCost[i] = config.getInt("shopBulkGemCost", category, 
					defaultValue(i, 7, 7), 1, 999, 
					"How much EXP does it cost to purchase many territory gems? (bulk quantity)");
			bulkGemCount[i] = config.getInt("shopBulkGemCount", category, 
					defaultValue(i, 10, 50), 1, 64, 
					"How many territory gems do you get for the purchase? (bulk quantity)");
			terrMachineEXPCost[i] = config.getInt("shopTerrMachineCost", category, 
					defaultValue(i, 30, 10), 1, 999, 
					"How much EXP does it cost to purchase a territory machine?");
		}
		
		recalculateNumChunks();		
		Contained.world.preInit(event, config);
		
		config.save();
	}
	
	private int defaultValue(int type, int overworldValue, int minigameValue) {
		if (type == OVERWORLD)
			return overworldValue;
		else
			return minigameValue;
	}
	
	private boolean defaultValue(int type, boolean overworldValue, boolean minigameValue) {
		if (type == OVERWORLD)
			return overworldValue;
		else
			return minigameValue;
	}
	
	public static int getDimConfig(int dimID) {
		if (MiniGameUtil.isPvP(dimID) || MiniGameUtil.isTreasure(dimID))
			return MINIGAME;
		else
			return OVERWORLD;
	}
	
	public int getWorldRadius(int dimID) {
		if (MiniGameUtil.isPvP(dimID))
			return pvpRadius;
		else if (MiniGameUtil.isTreasure(dimID))
			return treasureRadius;
		else
			return worldRadius;
	}
	
	public void setWorldRadius(int dimID, int radius) {
		if (MiniGameUtil.isPvP(dimID))
			pvpRadius = radius;
		else if (MiniGameUtil.isTreasure(dimID))
			treasureRadius = radius;
		else
			worldRadius = radius;	
		recalculateNumChunks();
	}
	
	public int getNumChunks(int dimID) {
		if (MiniGameUtil.isPvP(dimID))
			return numPvPChunks;
		else if (MiniGameUtil.isTreasure(dimID))
			return numTreasureChunks;
		else
			return numWorldChunks;
	}
	
	private void recalculateNumChunks() {
		numWorldChunks = 0;
		numPvPChunks = 0;
		numTreasureChunks = 0;
		int maxRadius = Math.max(Math.max(worldRadius, pvpRadius), treasureRadius);
		for(int chunkX=-(maxRadius+Resources.wastelandPadding); 
				chunkX<=(maxRadius+Resources.wastelandPadding); chunkX++) {
			for(int chunkZ=-(maxRadius+Resources.wastelandPadding); 
					chunkZ<=(maxRadius+Resources.wastelandPadding); chunkZ++) {
				float distDiff = Util.euclidDist(0, 0, chunkX, chunkZ);
				if (distDiff <= (worldRadius+Resources.wastelandPadding))
					numWorldChunks++;
				if (distDiff <= (pvpRadius+Resources.wastelandPadding))
					numPvPChunks++;
				if (distDiff <= (treasureRadius+Resources.wastelandPadding))
					numTreasureChunks++;
			}
		}
	}
	
}

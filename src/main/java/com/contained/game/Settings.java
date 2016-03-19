package com.contained.game;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.common.config.Configuration;

/**
 * Loads and stores information from the mod's configuration files.
 */
public class Settings {

	public static final String teamCategory = "teams";
	
	public int largeTeamSize;
	public boolean creativeOverride;
	
	public int maxTeamSize;
	public int flagXPCost;
	public int claimDelay;
	public int claimRadius;
	public int antiClaimDelay;
	public int antiClaimRadius;
	
	public Settings(FMLPreInitializationEvent event) {
		Configuration config = new Configuration(event.getSuggestedConfigurationFile());
		config.load();
		
		creativeOverride = config.getBoolean("creativeOverride", teamCategory, true, 
				"Should a player in creative mode be exempt from the protection rules of a territory?");
		largeTeamSize = config.getInt("largeTeamRequirement", teamCategory, 100, 0, 99999,
				"How many blocks of land must a team own before their territory is vulnerable to invasion from other teams?");
		maxTeamSize = config.getInt("maxTeamSize", teamCategory, 5, 1, 999, 
				"What is the maximum player capacity of a team?");
		flagXPCost = config.getInt("flagXPCost", teamCategory, 30, 0, 999, 
				"How many Minecraft levels do you need to use the flag item?");
		claimRadius = config.getInt("claimRadius", teamCategory, 2, 0, 10, 
				"What is the radius, in blocks, that the Territory Machines can claim land?");
		claimDelay = config.getInt("claimDelay", teamCategory, 90, 1, 60000, 
				"How long, in seconds, does it take for the Territory Machine to claim a block of land?");
		antiClaimRadius = config.getInt("antiClaimRadius", teamCategory, 2, 0, 10, 
				"What is the radius, in blocks, that the Anti-Territory Machines can steal land?");
		antiClaimDelay = config.getInt("antiClaimDelay", teamCategory, 90, 1, 60000, 
				"How long, in seconds, does it take for the Anti-Territory Machine to steal a block of land?");
		
		config.save();
	}
	
}
package com.contained.game;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.common.config.Configuration;

/**
 * Loads and stores information from the mod's configuration files.
 */
public class Settings {

	public static final String protectCategory = "Protection Rules";
	public static final String teamCategory = "Player Teams and Territory";
	
	public boolean buildProtect;
	public boolean breakProtect;
	public boolean bucketProtect;
	public boolean animalProtect;
	public boolean mobProtect;
	public boolean playerProtect;
	public boolean containerProtect;
	public boolean harvestProtect;
	public boolean itemProtect;
	public boolean interactProtect;
	public boolean creativeOverride;
	
	public int maxTeamSize;
	public int flagXPCost;
	
	public Settings(FMLPreInitializationEvent event) {
		Configuration config = new Configuration(event.getSuggestedConfigurationFile());
		config.load();
		buildProtect = config.getBoolean("buildProtect", protectCategory, true, 
				"Stops other players from being able to place blocks in your territory.");
		breakProtect = config.getBoolean("breakProtect", protectCategory, true, 
				"Stops other players from being able to break blocks in your territory.");
		bucketProtect = config.getBoolean("bucketProtect", protectCategory, true, 
				"Stops other players from being able to scoop fluids with a bucket in your territory.");
		animalProtect = config.getBoolean("animalProtect", protectCategory, true, 
				"Stops other players from being able to kill passive NPCs within your territory. (Animals, villagers, golems)");
		mobProtect = config.getBoolean("monsterProtect", protectCategory, false, 
				"Stops other players from being able to kill monsters within your territory.");
		playerProtect = config.getBoolean("playerProtect", protectCategory, true, 
				"Stops other players from being able to kill your teammates within your territory.");
		containerProtect = config.getBoolean("containerProtect", protectCategory, true, 
				"Stops other players from being able to interact with chests in your territory.");
		harvestProtect = config.getBoolean("harvestProtect", protectCategory, true, 
				"Stops other players from being able to harvest crops within your territory.");
		itemProtect = config.getBoolean("itemProtect", protectCategory, true, 
				"Stops other players from being able to pick up dropped items within your territory.");
		interactProtect = config.getBoolean("interactProtect", protectCategory, true, 
				"Stops other players from being able to interact with entities within your territory (trade with villagers, milk cows, etc).");
		creativeOverride = config.getBoolean("creativeOverride", protectCategory, true, 
				"Should a player in creative mode be exempt from the protection rules?");
		
		maxTeamSize = config.getInt("maxTeamSize", teamCategory, 5, 1, 999, 
				"What is the maximum player capacity of a team?");
		flagXPCost = config.getInt("flagXPCost", teamCategory, 30, 0, 999, 
				"How many Minecraft levels do you need to use the flag item?");
		
		config.save();
	}
	
}

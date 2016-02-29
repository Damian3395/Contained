package com.contained.game;

import com.contained.game.util.Resources;
import com.contained.game.util.Util;
import com.contained.game.world.ResourceCluster;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraft.init.Blocks;
import net.minecraftforge.common.config.Configuration;

/**
 * Loads and stores information from the mod's configuration files.
 */
public class Settings {

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
	
	public Settings(FMLPreInitializationEvent event) {
		Configuration config = new Configuration(event.getSuggestedConfigurationFile());
		config.load();
		buildProtect = config.getBoolean("buildProtect", Configuration.CATEGORY_GENERAL, true, 
				"Stops other players from being able to place blocks in your territory.");
		breakProtect = config.getBoolean("breakProtect", Configuration.CATEGORY_GENERAL, true, 
				"Stops other players from being able to break blocks in your territory.");
		bucketProtect = config.getBoolean("bucketProtect", Configuration.CATEGORY_GENERAL, true, 
				"Stops other players from being able to scoop fluids with a bucket in your territory.");
		animalProtect = config.getBoolean("animalProtect", Configuration.CATEGORY_GENERAL, true, 
				"Stops other players from being able to kill passive NPCs within your territory. (Animals, villagers, golems)");
		mobProtect = config.getBoolean("monsterProtect", Configuration.CATEGORY_GENERAL, false, 
				"Stops other players from being able to kill monsters within your territory.");
		playerProtect = config.getBoolean("playerProtect", Configuration.CATEGORY_GENERAL, true, 
				"Stops other players from being able to kill your teammates within your territory.");
		containerProtect = config.getBoolean("containerProtect", Configuration.CATEGORY_GENERAL, true, 
				"Stops other players from being able to interact with chests in your territory.");
		harvestProtect = config.getBoolean("harvestProtect", Configuration.CATEGORY_GENERAL, true, 
				"Stops other players from being able to harvest crops within your territory.");
		itemProtect = config.getBoolean("itemProtect", Configuration.CATEGORY_GENERAL, true, 
				"Stops other players from being able to pick up dropped items within your territory.");
		interactProtect = config.getBoolean("interactProtect", Configuration.CATEGORY_GENERAL, true, 
				"Stops other players from being able to interact with entities within your territory (trade with villagers, milk cows, etc).");
		creativeOverride = config.getBoolean("creativeOverride", Configuration.CATEGORY_GENERAL, true, 
				"Should a player in creative mode be exempt from the protection rules?");
		config.save();
	}
	
}

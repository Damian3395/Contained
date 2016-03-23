package com.contained.game.world;

import com.contained.game.util.Resources;
import com.contained.game.world.biome.BiomeProperties;
import com.contained.game.world.biome.WastelandBiome;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraft.init.Blocks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;

public class GenerateWorld {
	public static ResourceCluster[] oreSpawnProperties;
	public static BiomeProperties biomeProperties;
	WastelandBiome wastelandBiome = new WastelandBiome();
	
	public void init(){
		MinecraftForge.TERRAIN_GEN_BUS.register(new TerrainGen());
		MinecraftForge.ORE_GEN_BUS.register(new ResourceGen());
		wastelandBiome.load();
	}
	
	public void preInit(FMLPreInitializationEvent event){
		Configuration config = new Configuration(event.getSuggestedConfigurationFile());
		config.load();
		Resources.worldRadius = config.getInt("worldSize", Configuration.CATEGORY_GENERAL, 25, 0, 500, "Radius of the finite world in chunks (16x16 blocks), centered around spawn.");
		Resources.minOreRegen = config.getInt("minOreRegen", Configuration.CATEGORY_GENERAL, 15000, 1, 5000000, "Minimum time (in seconds) before harvested ores regenerate.");
		Resources.maxOreRegen = config.getInt("maxOreRegen", Configuration.CATEGORY_GENERAL, 86000, 1, 5000000, "Maximum time (in seconds) before harvested ores regenerate.");
		
		ResourceCluster.writeConfigComment(config);
		oreSpawnProperties = new ResourceCluster[Resources.NUM_MINERALS];
		oreSpawnProperties[Resources.DIAMOND] = ResourceCluster.generateFromConfig(config, 
				Blocks.diamond_ore, 2, 8, 6, 12, 6, 12, 2, 3, 2, 16);
		oreSpawnProperties[Resources.EMERALD] = ResourceCluster.generateFromConfig(config, 
				Blocks.emerald_ore, 2, 8, 6, 12, 6, 12, 2, 3, 2, 16);
		oreSpawnProperties[Resources.GOLD] = ResourceCluster.generateFromConfig(config, 
				Blocks.gold_ore, 8, 32, 2, 5, 5, 10, 3, 5, 2, 32);
		oreSpawnProperties[Resources.REDSTONE] = ResourceCluster.generateFromConfig(config, 
				Blocks.redstone_ore, 8, 32, 3, 6, 8, 15, 4, 6, 2, 24);
		oreSpawnProperties[Resources.LAPIS] = ResourceCluster.generateFromConfig(config, 
				Blocks.lapis_ore, 8, 32, 3, 6, 8, 15, 4, 6, 2, 24);
		oreSpawnProperties[Resources.IRON] = ResourceCluster.generateFromConfig(config, 
				Blocks.iron_ore, 12, 48, 3, 6, 5, 10, 4, 6, 24, 64);
		oreSpawnProperties[Resources.COAL] = ResourceCluster.generateFromConfig(config, 
				Blocks.coal_ore, 12, 48, 3, 6, 5, 10, 4, 6, 24, 64);
		
		BiomeProperties.writeConfigComment(config);
		biomeProperties = BiomeProperties.generateFromConfig(config);
		config.save();
	}
}

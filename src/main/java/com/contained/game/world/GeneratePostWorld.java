package com.contained.game.world;

import com.contained.game.handler.GlobalEvents;
import com.contained.game.util.Resources;
import com.contained.game.util.Util;
import com.contained.game.world.post.ResourceCluster;
import com.contained.game.world.post.ResourceGen;
import com.contained.game.world.post.TerrainGen;
import com.contained.game.world.post.biomes.WastelandBiome;
import com.contained.game.world.post.block.WastelandBlock;
import com.contained.game.world.post.block.WastelandBush;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraft.init.Blocks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;

public class GeneratePostWorld {
	public static ResourceCluster[] oreSpawnProperties;
	WastelandBlock wasteland = new WastelandBlock();
	WastelandBush wastelandBush = new WastelandBush();
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
		ResourceCluster.writeConfigComment(config);
		oreSpawnProperties = new ResourceCluster[Util.NUM_MINERALS];
		oreSpawnProperties[Util.DIAMOND] = ResourceCluster.generateFromConfig(config, 
				Blocks.diamond_ore, 2, 8, 6, 12, 6, 12, 2, 3, 2, 16);
		oreSpawnProperties[Util.EMERALD] = ResourceCluster.generateFromConfig(config, 
				Blocks.emerald_ore, 2, 8, 6, 12, 6, 12, 2, 3, 2, 16);
		oreSpawnProperties[Util.GOLD] = ResourceCluster.generateFromConfig(config, 
				Blocks.gold_ore, 8, 32, 2, 5, 5, 10, 3, 5, 2, 32);
		oreSpawnProperties[Util.REDSTONE] = ResourceCluster.generateFromConfig(config, 
				Blocks.redstone_ore, 8, 32, 3, 6, 8, 15, 4, 6, 2, 24);
		oreSpawnProperties[Util.LAPIS] = ResourceCluster.generateFromConfig(config, 
				Blocks.lapis_ore, 8, 32, 3, 6, 8, 15, 4, 6, 2, 24);
		oreSpawnProperties[Util.IRON] = ResourceCluster.generateFromConfig(config, 
				Blocks.iron_ore, 12, 48, 3, 6, 5, 10, 4, 6, 24, 64);
		oreSpawnProperties[Util.COAL] = ResourceCluster.generateFromConfig(config, 
				Blocks.coal_ore, 12, 48, 3, 6, 5, 10, 4, 6, 24, 64);
		config.save();
		
		wasteland.preInit(event);
		wastelandBush.preInit(event);
	}
}

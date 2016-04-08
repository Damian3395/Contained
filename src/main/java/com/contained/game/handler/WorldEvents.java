package com.contained.game.handler;

import java.awt.Point;

import com.contained.game.util.Load;
import com.contained.game.util.Save;
import com.contained.game.util.Util;
import com.contained.game.world.GenerateWorld;
import com.contained.game.world.WorldGenDecoration;
import com.contained.game.world.biome.WastelandBiome;
import com.contained.game.world.block.WastelandBlock;
import com.contained.game.world.block.WastelandBush;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.init.Blocks;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.event.terraingen.ChunkProviderEvent;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent;
import net.minecraftforge.event.world.WorldEvent;

public class WorldEvents {
	boolean readyToGen = false;
	
	@SubscribeEvent
	//Make all chunks within a specified distance from spawn be empty wasteland.
	public void biomeControl(ChunkProviderEvent.ReplaceBiomeBlocks event) {
		if (Util.isOverworld(event.world.provider.dimensionId) && readyToGen && !event.world.isRemote) {
			float wasteAmount = Util.isWasteland(event.world, event.chunkX, event.chunkZ);
			BiomeGenBase biomeOverride = null;
			if (wasteAmount > 0) {
				for (int i=0; i<event.blockArray.length; i++) {
					if (Util.isSolidBlock(event.blockArray[i])) {
						if (Math.random() <= wasteAmount)
							event.blockArray[i] = WastelandBlock.instance;
					}
				}
				if (wasteAmount == 1.0f)
					biomeOverride = WastelandBiome.biome;
			}
			if (biomeOverride == null) {
				Point p = new Point(event.chunkX, event.chunkZ);
				if (GenerateWorld.getBiomeProperties(event.world.provider.dimensionId).biomeMapping.containsKey(p))
					biomeOverride = GenerateWorld.getBiomeProperties(event.world.provider.dimensionId).biomeMapping.get(p);
				else
					biomeOverride = WastelandBiome.biome;
			}
			
			//Override biomes based on finite world configurations
			for(int i=0; i<event.biomeArray.length; i++)
				event.biomeArray[i] = biomeOverride;
			
			//Low areas of map should use netherrack instead of stone.
			for(int i=0;i<16;i++) {
				for(int j=0;j<16;j++) {
					for(int k=0;k<16;k++) {
						int val = i << 12 | j << 8 | k;
						if (event.blockArray[val] == Blocks.stone && Math.random() <= (17f-k)/8f)
							event.blockArray[val] = Blocks.netherrack;
					}
				}
			}
		}
	}
	
	
	@SubscribeEvent
	//Scatter some dead bushes around the wasteland, for effect.
	public void doDecorations(DecorateBiomeEvent.Pre event) {
		float absChunkX = event.chunkX/16;
		float absChunkZ = event.chunkZ/16;
		
		if (Util.isWasteland(event.world, absChunkX, absChunkZ) > 0) {
			int numDeadBushes = (int)(Math.random()*20D)+15;
			for (int i = 0; i < numDeadBushes; i++) {
				int x = event.chunkX + event.rand.nextInt(16) + 8;
				int z = event.chunkZ + event.rand.nextInt(16) + 8;
				int y = event.rand.nextInt(Math.max(1, event.world.getHeightValue(x, z) * 2));
				(new WorldGenDecoration(WastelandBush.instance)).generate(event.world, event.rand, x, y, z);
			}
		}
	}
	
	//
	// === File Handling ===
	//
	@SubscribeEvent
	public void init(WorldEvent.Load event) {
		if (Util.isOverworld(event.world.provider.dimensionId) && !event.world.isRemote)
			Load.loadWorldData(event.world, event.world.provider.dimensionId);
		readyToGen = true;
	}
	
	@SubscribeEvent
	public void close(WorldEvent.Unload event) {
		if (Util.isOverworld(event.world.provider.dimensionId))
			Save.saveWorldData(event.world.provider.dimensionId);
	}
	
	@SubscribeEvent
	public void close(WorldEvent.Save event) {
		if (Util.isOverworld(event.world.provider.dimensionId))
			Save.saveWorldData(event.world.provider.dimensionId);
	}
}

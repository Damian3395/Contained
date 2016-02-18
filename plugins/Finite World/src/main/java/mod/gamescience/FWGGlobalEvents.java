package mod.gamescience;

import net.minecraftforge.event.terraingen.ChunkProviderEvent;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent;
import net.minecraftforge.event.world.WorldEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

/**
 * Event handlers
 */
public class FWGGlobalEvents {	
	
	boolean readyToGen = false;
	
	@SubscribeEvent
	//Make all chunks within a specified distance from spawn be empty wasteland.
	public void biomeControl(ChunkProviderEvent.ReplaceBiomeBlocks event) {
		if (event.world.provider.dimensionId == 0) {
			float wasteAmount = FWGUtil.isWasteland(event.world, event.chunkX, event.chunkZ);
			if (wasteAmount > 0 && readyToGen) {
				for (int i=0; i<event.blockArray.length; i++) {
					if (FWGUtil.isSolidBlock(event.blockArray[i])) {
						if (Math.random() <= wasteAmount)
							event.blockArray[i] = WastelandBlock.block;
					}
				}
				if (wasteAmount == 1.0f) {
					for(int i=0; i<event.biomeArray.length; i++) {
						event.biomeArray[i] = WastelandBiome.biome;
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
		
		if (FWGUtil.isWasteland(event.world, absChunkX, absChunkZ) > 0) {
			int numDeadBushes = (int)(Math.random()*20D)+15;
			for (int i = 0; i < numDeadBushes; i++) {
				int x = event.chunkX + event.rand.nextInt(16) + 8;
				int z = event.chunkZ + event.rand.nextInt(16) + 8;
				int y = event.rand.nextInt(Math.max(1, event.world.getHeightValue(x, z) * 2));
				(new WorldGenDecoration(WastelandBush.block)).generate(event.world, event.rand, x, y, z);
			}
		}
	}
	
	//
	// === File Handling ===
	//
	@SubscribeEvent
	public void init(WorldEvent.Load event) {
		if (event.world.provider.dimensionId == 0 && !event.world.isRemote)
			FiniteWorldGen.loadCustomData(event.world);
		readyToGen = true;
	}
	
	@SubscribeEvent
	public void close(WorldEvent.Unload event) {
		if (event.world.provider.dimensionId == 0)
			FiniteWorldGen.saveCustomData();
	}
	
	@SubscribeEvent
	public void close(WorldEvent.Save event) {
		if (event.world.provider.dimensionId == 0)
			FiniteWorldGen.saveCustomData();
	}
}

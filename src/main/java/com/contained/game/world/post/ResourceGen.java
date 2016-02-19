package com.contained.game.world.post;

import java.util.Random;

import com.contained.game.Contained;
import com.contained.game.util.Util;
import com.contained.game.world.GeneratePostWorld;

import cpw.mods.fml.common.eventhandler.Event.Result;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.event.terraingen.OreGenEvent;
import net.minecraftforge.event.terraingen.OreGenEvent.GenerateMinable.EventType;

public class ResourceGen {
	@SubscribeEvent
	//Override Minecraft's ore generation with our own custom one.
	public void generateOre(OreGenEvent.GenerateMinable event) {	
		if (event.type == EventType.COAL) {
			oreGenFromProperties(event.world, event.rand, event.worldX
					, event.worldZ, GeneratePostWorld.oreSpawnProperties[Util.COAL]);
			event.setResult(Result.DENY);
		} 
		else if (event.type == EventType.IRON) {
			oreGenFromProperties(event.world, event.rand, event.worldX
					, event.worldZ, GeneratePostWorld.oreSpawnProperties[Util.IRON]);
			event.setResult(Result.DENY);
		} 
		else if (event.type == EventType.GOLD) {
			oreGenFromProperties(event.world, event.rand, event.worldX
					, event.worldZ, GeneratePostWorld.oreSpawnProperties[Util.GOLD]);
			event.setResult(Result.DENY);
		} 
		else if (event.type == EventType.DIAMOND) {
			oreGenFromProperties(event.world, event.rand, event.worldX
					, event.worldZ, GeneratePostWorld.oreSpawnProperties[Util.DIAMOND]);
			oreGenFromProperties(event.world, event.rand, event.worldX
					, event.worldZ, GeneratePostWorld.oreSpawnProperties[Util.EMERALD]);
			event.setResult(Result.DENY);
		} 
		else if (event.type == EventType.REDSTONE) {
			oreGenFromProperties(event.world, event.rand, event.worldX
					, event.worldZ, GeneratePostWorld.oreSpawnProperties[Util.REDSTONE]);
			event.setResult(Result.DENY);
		} 
		else if (event.type == EventType.LAPIS) {
			oreGenFromProperties(event.world, event.rand, event.worldX
					, event.worldZ, GeneratePostWorld.oreSpawnProperties[Util.LAPIS]);
			event.setResult(Result.DENY);
		} 
	}

	//Generate ore veins within the chunk based on a defined OreClusterProperties.
	public static boolean oreGenFromProperties(World w, Random r, int x, int z, ResourceCluster p) {
		int chunkX = x/16;
		int chunkZ = z/16;
		boolean canGenerate = false;
		for(int i=0; i<p.spawnChunks.size(); i++) {
			if (p.spawnChunks.get(i).x == chunkX && p.spawnChunks.get(i).y == chunkZ) {
				canGenerate = true;
				break;
			}
		}
		if (canGenerate) {
			WorldGenMinable generator = new WorldGenMinable(p.type, 
					Util.randomRange(p.veinSize.min(), p.veinSize.max()));
			generateVeins(w, r, generator, 
					Util.randomRange(p.veinCount.min(), p.veinCount.max()),
					x, z, p.spawnHeight.min(), p.spawnHeight.max());
		}
		return canGenerate;
	}
	
	//Determine where in the chunk the veins should generate.
    public static void generateVeins(World w, Random r, WorldGenerator gen, 
    		int numVeins, int x, int z, int minY, int maxY)
    {
        for (int i=0; i<numVeins; i++) {
            int genX = x + r.nextInt(16);
            int genY = r.nextInt(maxY - minY) + minY;
            int genZ = z + r.nextInt(16);
            gen.generate(w, r, genX, genY, genZ);
        }
    }
}

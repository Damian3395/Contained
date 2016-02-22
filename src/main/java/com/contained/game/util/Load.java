package com.contained.game.util;

import com.contained.game.world.GenerateWorld;

import net.minecraft.world.World;

public class Load {
	public static void loadWorldData(World w){
		for(int i=0; i<GenerateWorld.oreSpawnProperties.length; i++) {
			GenerateWorld.oreSpawnProperties[i].loadFromFile();
			GenerateWorld.oreSpawnProperties[i].determineAllChunks(w, Resources.worldRadius);	
		}
	}
}

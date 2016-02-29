package com.contained.game.util;

import com.contained.game.world.GenerateWorld;

public class Save {
	public static void saveWorldData() {
		for(int i=0; i<GenerateWorld.oreSpawnProperties.length; i++)
			GenerateWorld.oreSpawnProperties[i].saveToFile();
	}
}

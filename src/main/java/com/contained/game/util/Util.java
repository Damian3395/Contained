package com.contained.game.util;

import com.contained.game.Contained;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class Util {
	public static final int COAL = 0;
	public static final int IRON = 1;
	public static final int GOLD = 2;
	public static final int LAPIS = 3;
	public static final int REDSTONE = 4;
	public static final int DIAMOND = 5;
	public static final int EMERALD = 6;
	public static final int NUM_MINERALS = 7;
	
	/**
	 * Euclidean distance between two points
	 */
	public static float euclidDist(float x1, float y1, float x2, float y2) {
		return (float)Math.sqrt(Math.pow(x1-x2, 2) + Math.pow(y1-y2, 2));
	}
	
	public static boolean isSolidBlock(Block b) {
		if (b != null && !b.equals(Blocks.air) && !(b instanceof BlockBush) 
				&& !isLiquidBlock(b))
			return true;
		return false;
	}
	
	public static boolean isLiquidBlock(Block b) {
		Material mat = b.getMaterial();
		if (mat == Material.water || mat == Material.lava)
			return true;
		return false;
	}
	
	/**
	 * Check if the current chunks should be wasteland. Return value is in
	 * the range [0-1], 0.0 = not wasteland, 1.0 = wasteland, otherwise
	 * partially wasteland.
	 */
	public static float isWasteland(World w, float chunkX, float chunkZ) {
		if (w.provider.dimensionId != 0)
			return 0;
		
		float spawnX = w.getSpawnPoint().posX/16;
		float spawnZ = w.getSpawnPoint().posZ/16;
		float distDiff = Util.euclidDist(spawnX, spawnZ, chunkX, chunkZ);
		
		if (distDiff > Resources.worldRadius) {
			return Math.min(1f, (distDiff-Resources.worldRadius)/5f);
		}
		return 0;
	}
	
	/**
	 * Returns random value between min and max
	 */
	public static int randomRange(int min, int max) {
		return (int)(Math.random()*(double)(max-min))+min;
	}
}

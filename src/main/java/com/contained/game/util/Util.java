package com.contained.game.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;

public class Util {	
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
	
	public static String getDate(){
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
	}
	
	/**
	 * Makes the given entity invulnerable or not.
	 */
	public static void setEntityInvulnerability(Entity ent, boolean value) {
		NBTTagCompound ntc = new NBTTagCompound();
		ent.writeToNBT(ntc);
		ntc.setBoolean("Invulnerable", value);
		ent.readFromNBT(ntc);
	}
	
	/**
	 * Outputs a message to chat, if debug mode is enabled.
	 */
	public static void debugMessage(EntityPlayer player, String msg) {
		if (Resources.DEBUG_ENABLED)
			player.addChatComponentMessage(new ChatComponentText(msg));
	}
}

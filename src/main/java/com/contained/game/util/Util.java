package com.contained.game.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.contained.game.data.Data;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

public class Util {	
	public static String errorCode = "�4�l";
	public static String warningCode = "�6�l";
	public static String successCode = "�2�l";
	public static String infoCode = "�b�l";
	
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
		if (!Util.isOverworld(w.provider.dimensionId))
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
	 * Check if the given dimension ID refers to a dimension that should
	 * be treated as a finite overworld.
	 */
	public static boolean isOverworld(int dimID) {
		if (dimID == 0  
			|| (dimID >= Resources.MIN_PVP_DIMID && dimID <= Resources.MAX_PVP_DIMID)
			|| (dimID >= Resources.MIN_TREASURE_DIMID && dimID <= Resources.MAX_TREASURE_DIMID)) {
			return true;
		}
		return false;
	}
	
	public static String getDimensionString(int dimID) {
		if (dimID == 0)
			return "Lobby";
		else if (dimID == 1)
			return "Nether";
		else if (dimID == -1)
			return "End";
		else if (dimID >= Resources.MIN_PVP_DIMID && dimID <= Resources.MAX_PVP_DIMID)
			return "PvP";
		else if (dimID >= Resources.MIN_TREASURE_DIMID && dimID <= Resources.MAX_TREASURE_DIMID)
			return "Treasure";
		else return "Unknown";
	}
	
	/**
	 * Returns random value between min (inclusive) and max (exclusive)
	 */
	public static int randomRange(int min, int max) {
		return (int)(Math.random()*(double)(max-min))+min;
	}
	
	//Randomly return true/false, with true occurring with 'bias' probability.	
	public static boolean biasedCoinFlip(float bias) {
		if (Math.random() <= bias)
			return true;
		else
			return false;
	}
	
	public static byte biasedCoinFlipByte(float bias) {
		if (Math.random() <= bias)
			return 1;
		else
			return 0;
	}
	
	//Get a random value uniformly distributed around a fixed point.
	public static double randomBoth(double range) {
		return (Math.random()*range*2.0)-range;
	}
	
	public static String getDate(){
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
	}
	
	public static String getServerID(){
		if(Resources.LOGGING_ENABLED && MinecraftServer.getServer() != null)
			return MinecraftServer.getServer().getServerHostname();
		return "debugMode";
	}
	
	/**
	 * Tries to get the given player based on their display name, but only if they're currently online.
	 * If either the player doesn't exist, or is not currently online, this will return null.
	 */
	public static EntityPlayer getOnlinePlayer(String displayName) {
		@SuppressWarnings("rawtypes")
		List onlinePlayers = MinecraftServer.getServer().getConfigurationManager().playerEntityList;
		for (Object o : onlinePlayers) {
			if (o instanceof EntityPlayer) {
				EntityPlayer onlinePlayer = (EntityPlayer)o;
				if (onlinePlayer.getDisplayName().toLowerCase().equals(displayName.toLowerCase()))
					return onlinePlayer;
			}
		}	
		return null;
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
	 * Attempts to remove a certain quantity of an item from a player's inventory.
	 */
	public static void removeItem(ItemStack toRemove, EntityPlayer player) {
		int amountLeft = toRemove.stackSize;
		ItemStack[] inv = player.inventory.mainInventory;
		for(int i=0; i<inv.length; i++) {
			if (inv[i] == null) continue;
			if (Util.itemsEqual(inv[i], toRemove)) {
				if (inv[i].stackSize > amountLeft) {
					inv[i].stackSize -= amountLeft;
					amountLeft = 0;
					break;
				} else {
					amountLeft -= inv[i].stackSize;
					inv[i] = null;
				}
				if (amountLeft == 0)
					break;
			}
		}
	}
	
	/**
	 * Compares equivalency of two itemstacks, except disregards their
	 * stack size, unlike the normal ItemStack comparing function.
	 * Also disregard item ownership tracking.
	 */
	public static boolean itemsEqual(ItemStack itemA, ItemStack itemB) {
		ItemStack itemADup = itemA.copy();
		ItemStack itemBDup = itemB.copy();
		NBTTagCompound itemData = Data.getTagCompound(itemADup);
		itemData.removeTag("owner");
		itemADup.setTagCompound(itemData);
		
		itemData = Data.getTagCompound(itemBDup);
		itemData.removeTag("owner");
		itemBDup.setTagCompound(itemData);
		
		return itemADup.isItemEqual(itemBDup) 
				&& ItemStack.areItemStackTagsEqual(itemADup, itemBDup);
	}
	
	public static BiomeGenBase[] getBiomesArray() {
		ArrayList<BiomeGenBase> all_biomesList = new ArrayList<BiomeGenBase>();
		for( BiomeGenBase biome : BiomeGenBase.getBiomeGenArray() ) {
			if( biome != null )
				all_biomesList.add(biome);
		}
		BiomeGenBase[] all_biomes_array = new BiomeGenBase[all_biomesList.size()];
		return all_biomesList.toArray(all_biomes_array);
	}
	
	/**
	 * Outputs a local error message to the player, in red text.
	 */
	public static void displayError(EntityPlayer player, String msg) {
		player.addChatComponentMessage(new ChatComponentText("§c"+msg));
	}
	
	/**
	 * Outputs a local message to the player.
	 */
	public static void displayMessage(EntityPlayer player, String msg) {
		player.addChatComponentMessage(new ChatComponentText(msg));
	}
	
	/**
	 * Outputs a message to chat, if debug mode is enabled.
	 */
	public static void debugMessage(EntityPlayer player, String msg) {
		if (Resources.DEBUG_ENABLED)
			player.addChatComponentMessage(new ChatComponentText(msg));
	}
	
	public static float clamp(float val, float min, float max) {
		return Math.max(Math.min(max, val), min);
	}
}

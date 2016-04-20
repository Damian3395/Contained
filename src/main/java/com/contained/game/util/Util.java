package com.contained.game.util;

import java.awt.Point;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.contained.game.Contained;
import com.contained.game.data.Data;
import com.contained.game.entity.ExtendedLivingBase;
import com.contained.game.user.PlayerMiniGame;
import com.contained.game.user.PlayerTeamIndividual;
import com.contained.game.world.GameTeleporter;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.material.Material;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.BiomeGenBase;

public class Util {	
	public static String errorCode = "§4§l";
	public static String warningCode = "§6§l";
	public static String successCode = "§2§l";
	public static String infoCode = "§b§l";
	
	/**
	 * Euclidean distance between two points
	 */
	public static float euclidDist(float x1, float y1, float x2, float y2) {
		return (float)Math.sqrt(Math.pow(x1-x2, 2) + Math.pow(y1-y2, 2));
	}
	
	public static float euclidDist(float x1, float y1, float z1, float x2, float y2, float z2) {
		return (float)Math.sqrt(Math.pow(x1-x2, 2) + Math.pow(y1-y2, 2) + Math.pow(z1-z2, 2));
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
		
		if (distDiff > Contained.configs.getWorldRadius(w.provider.dimensionId))
			return Math.min(1f, (distDiff-Contained.configs.getWorldRadius(w.provider.dimensionId))/(float)Resources.wastelandPadding);
		return 0;
	}
	
	/**
	 * Return a random (x,z) coordinate within the bounds of the finite world.
	 */
	public static Point getRandomLocation(World w) {
		int dimID = w.provider.dimensionId;
		float spawnX = w.getSpawnPoint().posX;
		float spawnZ = w.getSpawnPoint().posZ;
		spawnX += randomBoth(16.0f);
		spawnZ += randomBoth(16.0f);
		spawnX += randomBoth(Contained.configs.getWorldRadius(dimID)*16.0f);
		spawnZ += randomBoth(Contained.configs.getWorldRadius(dimID)*16.0f);
		return new Point((int)spawnX, (int)spawnZ);
	}
	
	/**
	 * Check if the given dimension ID refers to a dimension that should
	 * be treated as a finite overworld.
	 */
	public static boolean isOverworld(int dimID) {
		if (dimID == 0  || MiniGameUtil.isPvP(dimID) || MiniGameUtil.isTreasure(dimID))
			return true;
		return false;
	}
	
	public static String getDimensionString(int dimID) {
		if (dimID == 0)
			return "Lobby";
		else if (dimID == -1)
			return "Nether";
		else if (dimID == 1)
			return "End";
		else if (MiniGameUtil.isPvP(dimID))
			return "PvP";
		else if (MiniGameUtil.isTreasure(dimID))
			return "Treasure";
		else return "Unknown";
	}
	
	public static int getGameID(int dimID) {
		PlayerMiniGame miniGame = PlayerMiniGame.get(dimID);
		if(miniGame == null)
			return -1;
		
		return miniGame.getGameID();
	}
	
	public static int getGameMode(int dimID) {
		if(MiniGameUtil.isPvP(dimID))
			return Resources.PVP;
		if(MiniGameUtil.isTreasure(dimID))
			return Resources.TREASURE;
		
		return Resources.OVERWORLD;
	}
	
	public static void travelToDimension(int dimID, EntityPlayer player) {
		if (!player.worldObj.isRemote && !player.isDead && player instanceof EntityPlayerMP) {
			EntityPlayerMP mpPlayer = (EntityPlayerMP)player;
			MinecraftServer mcServer = MinecraftServer.getServer();
			WorldServer newWorld = mcServer.worldServerForDimension(dimID);
			
			mcServer.getConfigurationManager().transferPlayerToDimension(
						mpPlayer, dimID, new GameTeleporter(newWorld));
		}
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
	
	public static String getTimestamp(int ticks) {
		int seconds = ticks/20;
		int minutes = seconds/60;
		int hours = minutes/60;
		if (hours == 0)
			return minutes+":"+String.format("%02d", seconds%60);
		else
			return hours+":"+String.format("%02d", minutes%60)+":"+String.format("%02d", seconds%60);
	}
	
	@SuppressWarnings("unused")
	public static String getServerID() {
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
	 * Outputs a local message to chat, but only if debug mode is enabled.
	 */
	public static void debugMessage(EntityPlayer player, String msg) {
		if (Resources.DEBUG_ENABLED)
			player.addChatComponentMessage(new ChatComponentText(msg));
	}
	
	/**
	 * Outputs a global message to chat, visible to all players.
	 */
	public static void serverMessage(String msg) {
		MinecraftServer.getServer().getCommandManager().executeCommand(MinecraftServer.getServer(), "say "+msg);
	}
	
	/**
	 * Outputs a global message to chat, visible to all players, but only if
	 * debug mode is enabled.
	 */
	public static void serverDebugMessage(String msg) {
		if (Resources.DEBUG_ENABLED)
			serverMessage(msg);
	}
	
	public static void dimensionMessage(int dim, String msg) {
		List players = MinecraftServer.getServer().worldServers[dim].playerEntities;
		Iterator iterator = players.iterator();
		while(iterator.hasNext()){
			EntityPlayer player = (EntityPlayer) iterator.next();
			player.addChatComponentMessage(new ChatComponentText(msg));
		}
	}
	
	public static float clamp(float val, float min, float max) {
		return Math.max(Math.min(max, val), min);
	}
	
    public static void dropBlockAsItem(World w, int x, int y, int z, ItemStack item)
    {
        if (!w.isRemote && w.getGameRules().getGameRuleBooleanValue("doTileDrops") && !w.restoringBlockSnapshots)
        {
            float f = 0.7F;
            double d0 = (double)(w.rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
            double d1 = (double)(w.rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
            double d2 = (double)(w.rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
            EntityItem entityitem = new EntityItem(w, (double)x + d0, (double)y + d1, (double)z + d2, item);
            entityitem.delayBeforeCanPickup = 10;
            w.spawnEntityInWorld(entityitem);
        }
    }
}

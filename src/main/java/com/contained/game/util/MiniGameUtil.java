package com.contained.game.util;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;

import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.ChestGenHooks;
import net.minecraftforge.common.DimensionManager;
import codechicken.lib.packet.PacketCustom;
import codechicken.lib.vec.BlockCoord;

import com.contained.game.Contained;
import com.contained.game.entity.ExtendedPlayer;
import com.contained.game.handler.games.PVPEvents;
import com.contained.game.handler.games.TreasureEvents;
import com.contained.game.network.ClientPacketHandlerUtil;
import com.contained.game.user.PlayerMiniGame;
import com.contained.game.user.PlayerTeam;
import com.contained.game.user.PlayerTeamIndividual;

public class MiniGameUtil {
	public static boolean isPvP(int dimID) {
		if (dimID >= Resources.MIN_PVP_DIMID && dimID <= Resources.MAX_PVP_DIMID)
			return true;
		return false;
	}
	
	public static boolean isTreasure(int dimID) {
		if (dimID >= Resources.MIN_TREASURE_DIMID && dimID <= Resources.MAX_TREASURE_DIMID)
			return true;
		return false;
	}
	
	public static boolean isDimensionEmpty(int dim) {
		for(PlayerMiniGame games : Contained.miniGames)
			if(games.getGameDimension() == dim)
				return false;
		
		return true;
	}
	
	public static int gameMode(int dimID) {
		if (isPvP(dimID))
			return Resources.PVP;
		else if (isTreasure(dimID))
			return Resources.TREASURE;
		else
			return Resources.OVERWORLD;
	}
	
	public static void startGame(PlayerMiniGame data) {
		int dimID = data.getGameDimension();
		int mode = gameMode(dimID);
		Contained.timeLeft[dimID] = Contained.configs.gameDuration[mode]*20;
		Contained.gameActive[dimID] = true;
		
		if (mode == Resources.PVP)
			PVPEvents.initializePVPGame(dimID);
		else if (mode == Resources.TREASURE)
			TreasureEvents.initializeTreasureGame(dimID);
		
		WorldServer w = DimensionManager.getWorld(dimID);
		if (w != null)
			w.setWorldTime(0);
		
		ClientPacketHandlerUtil.syncMinigameStart(data);
	}
	
	public static void startGame(int dimID, EntityPlayerMP player) {
		int gameMode = Resources.OVERWORLD;
		if (isPvP(dimID)){
			Contained.timeLeft[dimID] = Contained.configs.gameDuration[Resources.PVP]*20;
			gameMode = Resources.PVP;
		}else if (isTreasure(dimID)){
			Contained.timeLeft[dimID] = Contained.configs.gameDuration[Resources.TREASURE]*20;
			gameMode = Resources.TREASURE;
		}else
			return;
		
		ExtendedPlayer startMiniGame = ExtendedPlayer.get(player);
		startMiniGame.setGameMode(gameMode);
		startMiniGame.setGame(true);
		
		//Create MiniGame & Start It
		Contained.gameActive[dimID] = true;
		PlayerMiniGame newGame = new PlayerMiniGame(dimID, gameMode);
		newGame.testLaunch(player);
		Contained.miniGames.add(newGame);
		
		PlayerTeamIndividual pdata = PlayerTeamIndividual.get(player);
		pdata.xp = player.experienceTotal;
		pdata.armor = player.inventory.armorInventory;
		pdata.inventory = player.inventory.mainInventory;
		
		int invSize = 0;
		for(ItemStack item : pdata.inventory)
			if(item != null)
				invSize++;
		
		int armorSize = 0;
		for(ItemStack item : pdata.armor)
			if(item != null)
				armorSize++;
		
		PacketCustom miniGamePacket = new PacketCustom(Resources.MOD_ID, ClientPacketHandlerUtil.SAVE_PLAYER);
		miniGamePacket.writeInt(player.experienceTotal);
		miniGamePacket.writeInt(armorSize);
		int index = 0;
		for(ItemStack item : pdata.armor)
			if(item != null){
				miniGamePacket.writeInt(index);
				NBTTagCompound itemSave = new NBTTagCompound();
				item.writeToNBT(itemSave);
				miniGamePacket.writeNBTTagCompound(itemSave);
			}
		
		index = 0;
		miniGamePacket.writeInt(invSize);
		for(ItemStack item : pdata.inventory)
			if(item != null){
				miniGamePacket.writeInt(index);
				NBTTagCompound itemSave = new NBTTagCompound();
				item.writeToNBT(itemSave);
				miniGamePacket.writeNBTTagCompound(itemSave);
				index++;
			}
		Contained.channel.sendTo(miniGamePacket.toPacket(), (EntityPlayerMP)player);
		
		player.experienceTotal = 0;
		clearMainInventory(player);
		clearArmorInventory(player);
		
		//Sync MiniGame & Teams
		miniGamePacket = new PacketCustom(Resources.MOD_ID, ClientPacketHandlerUtil.MINIGAME_STARTED);
		miniGamePacket.writeInt(newGame.getGameMode());
		NBTTagCompound miniGameData = new NBTTagCompound();
		newGame.writeToNBT(miniGameData);
		miniGamePacket.writeNBTTagCompound(miniGameData);
		miniGamePacket.writeInt(dimID);
		miniGamePacket.writeInt(Contained.getTeamList(dimID).size());
		for(PlayerTeam team : Contained.getTeamList(dimID)){
			NBTTagCompound teamData = new NBTTagCompound();
			team.writeToNBT(teamData);
			miniGamePacket.writeNBTTagCompound(teamData);
		}
		Contained.channel.sendTo(miniGamePacket.toPacket(), player);
		
		//Sync Timer
		ClientPacketHandlerUtil.syncMinigameTime(dimID);
	}
	
	public static void stopGame(int dimID, EntityPlayerMP player) {
		ExtendedPlayer endMiniGame = ExtendedPlayer.get(player);
		endMiniGame.setGameMode(Resources.OVERWORLD);
		endMiniGame.setGame(false);
		Contained.getTeamList(dimID).clear();
		for(PlayerMiniGame game : Contained.miniGames)
			if(game.getGameDimension() == dimID){
				Contained.miniGames.remove(game);
				break;
			}
		for(int i = 0; i < Contained.gameScores[dimID].length; i++)
			Contained.gameScores[dimID][i] = 0;
		
		MiniGameUtil.clearMainInventory(player);
		MiniGameUtil.clearArmorInventory(player);
		
		PlayerTeamIndividual restorePdata = PlayerTeamIndividual.get(player.getDisplayName());
		player.experienceTotal = restorePdata.xp;
		player.inventory.armorInventory = restorePdata.armor;
		player.inventory.mainInventory = restorePdata.inventory;
		restorePdata.revertMiniGameChanges();
		
		if(MiniGameUtil.isTreasure(dimID))
			Contained.getActiveTreasures(0).clear();
		
		int invSize = 0;
		for(ItemStack item : restorePdata.inventory)
			if(item != null)
				invSize++;
		
		int armorSize = 0;
		for(ItemStack item : restorePdata.armor)
			if(item != null)
				armorSize++;
		
		PacketCustom miniGamePacket = new PacketCustom(Resources.MOD_ID, ClientPacketHandlerUtil.RESTORE_PLAYER);
		miniGamePacket.writeInt(restorePdata.xp);
		miniGamePacket.writeInt(armorSize);
		int index = 0;
		for(ItemStack item : restorePdata.armor)
			if(item != null){
				miniGamePacket.writeInt(index);
				NBTTagCompound itemSave = new NBTTagCompound();
				item.writeToNBT(itemSave);
				miniGamePacket.writeNBTTagCompound(itemSave);
			}
		
		index = 0;
		miniGamePacket.writeInt(invSize);
		for(ItemStack item : restorePdata.inventory)
			if(item != null){
				miniGamePacket.writeInt(index);
				NBTTagCompound itemSave = new NBTTagCompound();
				item.writeToNBT(itemSave);
				miniGamePacket.writeNBTTagCompound(itemSave);
				index++;
			}
		Contained.channel.sendTo(miniGamePacket.toPacket(), (EntityPlayerMP)player);
		
		restorePdata.xp = 0;
		restorePdata.armor = null;
		restorePdata.inventory = null;
		
		miniGamePacket = new PacketCustom(Resources.MOD_ID, ClientPacketHandlerUtil.MINIGAME_ENDED);
		miniGamePacket.writeInt(dimID);
		Contained.channel.sendTo(miniGamePacket.toPacket(), player);
		
		ClientPacketHandlerUtil.syncMinigameTime(dimID);
	}
	public static void resetGame(int dimID) {
		// TODO: Kick any remaining people in this dimension (including offline
		// players) back to the lobby, clear any stale data regarding this game,
		// and regenerate the dimension.
		//stopGame(dimID);
		WorldServer w = DimensionManager.getWorld(dimID);
		if (w != null) {
			ArrayList<EntityPlayer> toTeleport = new ArrayList<EntityPlayer>();
			for(Object p : w.playerEntities) {
				if (p instanceof EntityPlayer)
					toTeleport.add((EntityPlayer)p);
			}
			for(EntityPlayer p : toTeleport)
				Util.travelToDimension(0, p);
		}
	}
	
	public static PlayerMiniGame findOrCreateGame(int pendingPlayers){
		// Check for pending games.
		for(PlayerMiniGame game : Contained.miniGames)
			if(!game.isGameReady() && game.getCapacity() <= pendingPlayers)
				return game;
		
		// If no pending games, try creating a new game.
		PlayerMiniGame newGame = new PlayerMiniGame(pendingPlayers);
		if(newGame.getGameMode() == -1){
			// Couldn't create new game, because all mini-game dimensions are
			// already full.
			return null;
		}
		Contained.miniGames.add(newGame);
		return newGame;
	}
	
	public static int getCapacity(int gameMode) {
		return Contained.configs.maxTeamSize[gameMode]*Contained.configs.gameNumTeams[gameMode];
	}
	
	public static void generateChest(World w, int chestAmount, ChestGenHooks hook){
		if (w.isRemote)
			return;
		
		int x,y,z;
		Random r = new Random();		
		ArrayList<BlockCoord> generatedPoints = new ArrayList<BlockCoord>();
		for(int i=0;i<chestAmount;i++){
			Point randomSpawnPoint = Util.getRandomLocation(w);
			x=randomSpawnPoint.x;
			z=randomSpawnPoint.y;			
			y=w.getTopSolidOrLiquidBlock(x, z);		//coordinates to generate chests
			
			w.setBlock(x, y, z, Blocks.chest);		//generate a chest
			TileEntity chest = w.getTileEntity(x, y, z);	
			if (chest instanceof IInventory)
				WeightedRandomChestContent.generateChestContents(r, hook.getItems(r), (IInventory)chest, hook.getCount(r));
			generatedPoints.add(new BlockCoord(x, y, z));			
		}		
		ClientPacketHandlerUtil.addTreasureAndSync(w.provider.dimensionId, generatedPoints);
	}
	
	public static void clearMainInventory(EntityPlayer player){
		for(int i = 0; i < player.inventory.getSizeInventory(); i++)
			player.inventory.setInventorySlotContents(i, null);
	}
	
	public static void clearArmorInventory(EntityPlayer player){
		for(int i = 0; i < player.inventory.armorInventory.length; i++)
			player.inventory.armorInventory[i] = null;
	}
}

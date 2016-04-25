package com.contained.game.util;

import java.awt.Point;
import java.util.ArrayList;

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
	
	public static void testStartGame(int dimID, EntityPlayer player) {
		int gameMode = Resources.OVERWORLD;
		if (MiniGameUtil.isPvP(dimID))
			gameMode = Resources.PVP;
		else if (MiniGameUtil.isTreasure(dimID))
			gameMode = Resources.TREASURE;
		PlayerMiniGame newGame = new PlayerMiniGame(dimID, gameMode);
		newGame.testLaunch(player);
		ArrayList<EntityPlayer> joining = new ArrayList<EntityPlayer>();
		joining.add(player);
		startGame(newGame, joining);
	}
	
	public static void startGame(PlayerMiniGame game, ArrayList<EntityPlayer> playersJoining) {
		int dimID = game.getGameDimension();
		
		int gameMode = game.getGameMode();		
		Contained.timeLeft[dimID] = Contained.configs.gameDuration[gameMode]*20;
		Contained.gameActive[dimID] = true;
		Contained.miniGames.add(game);
		
		if (gameMode == Resources.PVP)
			PVPEvents.initializePVPGame(dimID);
		else if (gameMode == Resources.TREASURE)
			TreasureEvents.initializeTreasureGame(dimID);
		
		WorldServer w = DimensionManager.getWorld(dimID);
		if (w != null)
			w.setWorldTime(0);
		
		for (EntityPlayer player: playersJoining) {
			Util.travelToDimension(dimID, player);
			ExtendedPlayer startMiniGame = ExtendedPlayer.get(player);
			startMiniGame.setGameMode(gameMode);
			startMiniGame.setGame(true);
			
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
		}
		
		//Sync MiniGame & Teams
		PacketCustom miniGamePacket = new PacketCustom(Resources.MOD_ID, ClientPacketHandlerUtil.MINIGAME_STARTED);
		miniGamePacket.writeInt(gameMode);
		NBTTagCompound miniGameData = new NBTTagCompound();
		game.writeToNBT(miniGameData);
		miniGamePacket.writeNBTTagCompound(miniGameData);
		miniGamePacket.writeInt(dimID);
		miniGamePacket.writeInt(Contained.getTeamList(dimID).size());
		for(PlayerTeam team : Contained.getTeamList(dimID)){
			NBTTagCompound teamData = new NBTTagCompound();
			team.writeToNBT(teamData);
			miniGamePacket.writeNBTTagCompound(teamData);
		}
		Contained.channel.sendToDimension(miniGamePacket.toPacket(), dimID);
		
		//Sync Timer
		ClientPacketHandlerUtil.syncMinigameTime(dimID);
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

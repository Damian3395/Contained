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
	
	public static void startGame(int dimID, EntityPlayerMP player) {
		//Create Timer
		if (isPvP(dimID))
			Contained.timeLeft[dimID] = Contained.configs.gameDuration[Resources.PVP]*20;
		else if (isTreasure(dimID))
			Contained.timeLeft[dimID] = Contained.configs.gameDuration[Resources.TREASURE]*20;
		else
			return;
		
		//Create MiniGame & Start It
		Contained.gameActive[dimID] = true;
		PlayerMiniGame newGame = new PlayerMiniGame(dimID);
		newGame.testLaunch(player);
		Contained.miniGames.add(newGame);
		
		//Sync MiniGame & Teams
		PacketCustom miniGamePacket = new PacketCustom(Resources.MOD_ID, ClientPacketHandlerUtil.MINIGAME_STARTED);
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
		
		PlayerTeamIndividual restorePdata = PlayerTeamIndividual.get(player.getDisplayName());
		player.experienceTotal = restorePdata.xp;
		player.inventory.armorInventory = restorePdata.armor;
		List inventory = restorePdata.inventory;
		Iterator iterator = inventory.iterator();
		while(iterator.hasNext()){
			ItemStack restoreItem = (ItemStack) iterator.next();
			if(restoreItem != null)
				player.inventory.addItemStackToInventory(restoreItem);
		}
		restorePdata.revertMiniGameChanges();
		
		if(MiniGameUtil.isTreasure(dimID))
			Contained.getActiveTreasures(dimID).clear();
		
		PacketCustom miniGamePacket = new PacketCustom(Resources.MOD_ID, ClientPacketHandlerUtil.MINIGAME_ENDED);
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
}

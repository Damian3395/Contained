package com.contained.game.util;

import java.awt.Point;
import java.util.ArrayList;
import net.minecraft.entity.player.EntityPlayerMP;

import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.ChestGenHooks;
import net.minecraftforge.common.DimensionManager;
import codechicken.lib.packet.PacketCustom;
import codechicken.lib.vec.BlockCoord;

import com.contained.game.Contained;
import com.contained.game.handler.games.PVPEvents;
import com.contained.game.handler.games.TreasureEvents;
import com.contained.game.entity.ExtendedPlayer;
import com.contained.game.network.ClientPacketHandlerUtil;
import com.contained.game.user.PlayerMiniGame;
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
	
	public static int gameMode(int dimID) {
		if (isPvP(dimID))
			return Resources.PVP;
		else if (isTreasure(dimID))
			return Resources.TREASURE;
		else
			return Resources.OVERWORLD;
	}
	
	public static boolean isDimensionEmpty(int dim) {
		for(PlayerMiniGame games : Contained.miniGames)
			if(games.getGameDimension() == dim)
				return false;
		
		return true;
	}
	
	public static void startSPTestGame(int dimID, EntityPlayerMP player) {
		PlayerMiniGame newGame = new PlayerMiniGame(dimID);
		newGame.testLaunch(player);
		Contained.miniGames.add(newGame);
		startGame(dimID, newGame);
	}
	
	public static void startGame(int dimID, PlayerMiniGame data) {
		int mode = gameMode(dimID);
		Contained.timeLeft[dimID] = Contained.configs.gameDuration[mode]*20;
		Contained.gameActive[dimID] = true;
		
		if (mode == Resources.PVP)
			PVPEvents.initializePVPGame(dimID);
		else
			TreasureEvents.initializeTreasureGame(dimID);
		
		WorldServer w = DimensionManager.getWorld(dimID);
		if (w != null)
			w.setWorldTime(0);
		
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
}

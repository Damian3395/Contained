package com.contained.game.handler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.storage.RegionFileCache;
import net.minecraftforge.common.DimensionManager;
import codechicken.lib.vec.BlockCoord;

import com.contained.game.Contained;
import com.contained.game.ContainedRegistry;
import com.contained.game.Settings;
import com.contained.game.entity.ExtendedPlayer;
import com.contained.game.network.ClientPacketHandlerUtil;
import com.contained.game.user.PlayerMiniGame;
import com.contained.game.util.MiniGameUtil;
import com.contained.game.util.Resources;
import com.contained.game.util.Save;
import com.contained.game.util.Util;
import com.contained.game.world.GenerateWorld;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;

public class FMLEvents {

	@SubscribeEvent
	public void onTick(TickEvent.ServerTickEvent event) {
		if (event.phase == Phase.START) {
			int rand = Util.randomRange(0, 100);
			
			//Periodically see if a pending mini-game has enough players to start.
			if (rand == 2) {
				if (DimensionManager.getWorld(0) != null) {
					WorldServer w = DimensionManager.getWorld(0);
					List<EntityPlayer> lobbyPlayers = w.playerEntities;
					int waitingPlayers = 0;
					if (lobbyPlayers != null) {
						for(EntityPlayer player : lobbyPlayers) {
							if (ExtendedPlayer.get(player).isWaitingForMiniGame())
								waitingPlayers++;
						}
					}
					
					PlayerMiniGame toEnter = MiniGameUtil.findOrCreateGame(waitingPlayers);
					if (toEnter != null) {
						ArrayList<EntityPlayer> playersToJoin = new ArrayList<EntityPlayer>();
						// TODO: Prioritize this by sorting by amount of time player has been
						// waiting in the queue. Players who have been waiting longest should
						// be brought into the game first.
						int count = 0;
						for(EntityPlayer player : lobbyPlayers) {
							if (ExtendedPlayer.get(player).isWaitingForMiniGame()) {
								toEnter.addPlayer(player);
								count++;
								if (count >= toEnter.getCapacity())
									break;
							}
						}
						
						toEnter.launchGame();								
						// TODO: Right now this will instantaneously start a game and teleport
						// everyone into the dimension as soon as a game is ready. Change this
						// so that there's a 30-60 second delay, and send a chat message to
						// everyone who has been selected to join letting them know that their
						// mini-game is going to start soon.
					}
				}
			}
			
			//Tick the mini-game timers, and check for game-over.
			for(int i=Resources.MIN_PVP_DIMID; i<=Resources.MAX_PVP_DIMID; i++) {
				processGameTick(i);
				checkDimensionReset(i);
			}
			for(int i=Resources.MIN_TREASURE_DIMID; i<=Resources.MAX_TREASURE_DIMID; i++) {
				processGameTick(i);
				checkDimensionReset(i);
				
				//Periodically see if any treasure chests are missing in the treasure
				//mini-game (this can happen if they're blown up by TNT/Creepers/etc)
				if (rand == (10+i)%99) {
					if (DimensionManager.getWorld(i) != null && Contained.gameActive[i]) {
						WorldServer w = DimensionManager.getWorld(i);
						ArrayList<BlockCoord> toRemove = new ArrayList<BlockCoord>();
						for (BlockCoord point : Contained.getActiveTreasures(i)) {
							Block b = w.getBlock(point.x, point.y, point.z);
							if (!(b instanceof BlockChest))
								toRemove.add(new BlockCoord(point.x, point.y, point.z));
						}
						for (BlockCoord point : toRemove) {
							ClientPacketHandlerUtil.removeTreasureAndSync(i, point);
							MiniGameUtil.generateChest(w, 1, ContainedRegistry.CUSTOM_CHEST_LOOT);
						}
					}
				}
			}				
		}
	}
	
	public void checkDimensionReset(int dimID) {		
		if (DimensionManager.getWorld(dimID) == null && !Contained.gameActive[dimID]) {
			//Dimension is empty and the game is over.
			File dimDir = new File(DimensionManager.getCurrentSaveRootDirectory(), "DIM"+dimID);
			if (dimDir.exists() && dimDir.isDirectory()) {
				//...but the dimension still exists on disk.
				//Remove it from disk.
				
				// TODO! This is failing, because something is preventing the
				// ability to delete some the files in the dimension's 'region'
				// folder...
				//
				// This might be on the right track... but doesn't seem to solve
				// the problem:
				System.out.println("deleting "+dimID);
				RegionFileCache.clearRegionFileReferences();
				System.gc();
				
				//System.out.println("Deleting DIM"+dimID);
				try {
					FileUtils.deleteDirectory(dimDir);
				} catch (IOException e) { }
				Save.removeDimFiles(dimID);
				
				//And remove data about it from memory.
				Contained.teamData.remove(dimID);
				Contained.territoryData.remove(dimID);
				Contained.trades.remove(dimID);
				GenerateWorld.resetBiomeProperties(dimID);
				GenerateWorld.resetOreProperties(dimID);
			}
		}
		else if (DimensionManager.getWorld(dimID) != null) {
			List players = DimensionManager.getWorld(dimID).playerEntities;
			if (players == null || players.size() == 0) 
				DimensionManager.setWorld(dimID, null);
		}
	}
	
	/**
	 * Handle ticking of mini-game timer on client and server.
	 */
	@SubscribeEvent
	public void onClientTick(TickEvent.ClientTickEvent event) {
		if (event.phase == Phase.START) { 
			if (Contained.gameActive[0])
				Contained.tickTimeLeft(0);
		}
	}
	
	public void processGameTick(int dimID) {
		if (Contained.gameActive[dimID]) {
			Contained.tickTimeLeft(dimID);
			int timeRemaining = Contained.timeLeft[dimID];
			if (timeRemaining % 300 == 0 || timeRemaining == 0)
				ClientPacketHandlerUtil.syncMinigameTime(dimID);
			if (timeRemaining == 0)
				MiniGameUtil.resetGame(dimID);
		}
	}
	
}

package com.contained.game.handler.games;

import java.awt.Point;
import java.util.HashMap;

import com.contained.game.Contained;
import com.contained.game.ContainedRegistry;
import com.contained.game.entity.ExtendedPlayer;
import com.contained.game.network.ClientPacketHandlerUtil;
import com.contained.game.user.PlayerMiniGame;
import com.contained.game.user.PlayerTeam;
import com.contained.game.user.PlayerTeamIndividual;
import com.contained.game.util.MiniGameUtil;
import com.contained.game.util.Resources;

import codechicken.lib.vec.BlockCoord;
import net.minecraft.block.BlockChest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;

public class TreasureEvents {
	public static HashMap<String, Point> initializeTreasureGame(int dimID) {
		WorldServer w = DimensionManager.getWorld(dimID);
		Contained.getActiveTreasures(dimID).clear();
		if (w != null)
			MiniGameUtil.generateChest(w, Contained.configs.treasureChests, ContainedRegistry.CUSTOM_CHEST_LOOT);
		
		// Find spawn points for each of the teams.
		ChunkCoordinates spawn = w.getSpawnPoint();
		float angle = 0;
		HashMap<String, Point> teamSpawnPoints = new HashMap<String, Point>();
		Contained.getTerritoryMap(dimID).clear();
		
		for (PlayerTeam team : Contained.getTeamList(dimID)) {
			Point newSpawnLocation = new Point(
					(int)(spawn.posX+Contained.configs.getWorldRadius(dimID)*Math.cos(angle)),
					(int)(spawn.posZ+Contained.configs.getWorldRadius(dimID)*Math.sin(angle)));	
			teamSpawnPoints.put(team.id, newSpawnLocation);
			angle += (2.0*Math.PI)/Contained.configs.maxTeamSize[Resources.PVP];
			
			int territoryRadius = 8;
			for (int i=territoryRadius;i<=territoryRadius;i++)
				for (int j=-territoryRadius;j<=territoryRadius;j++)
					Contained.getTerritoryMap(dimID).put(new Point(newSpawnLocation.x+i, newSpawnLocation.y+j), team.id);
		}
		
		// Generate Emblem Receivers
		
		return teamSpawnPoints;
	}
	
	@SubscribeEvent
	public void onTreasureChestOpen(PlayerInteractEvent event){
		if(event.action == Action.RIGHT_CLICK_BLOCK && event.entityPlayer != null)
			handleTreasureChest(event.world, event.entityPlayer, event.x, event.y, event.z);
	}
	
	@SubscribeEvent
	public void onTreasureChestBreak(BlockEvent.BreakEvent event){
		if(event.getPlayer() != null)
			handleTreasureChest(event.world, event.getPlayer(), event.x, event.y, event.z);		
	}
	
	public void handleTreasureChest(World w, EntityPlayer p, int x, int y, int z) {
		if(!w.isRemote && (w.getBlock(x, y, z) instanceof BlockChest)) {	
			BlockCoord eventLocation = new BlockCoord(x, y, z);
			int dimID = w.provider.dimensionId;
			if (Contained.getActiveTreasures(dimID).contains(eventLocation)) {
				ExtendedPlayer properties = ExtendedPlayer.get(p);
				properties.treasuresOpened++;
				properties.curTreasuresOpened++;
				
				PlayerTeamIndividual pdata = PlayerTeamIndividual.get(p);
				PlayerMiniGame miniGame = PlayerMiniGame.get(p.dimension);
				int teamID = miniGame.getTeamID(pdata);
				if (teamID != -1) {
					Contained.gameScores[p.dimension][teamID]++;
					ClientPacketHandlerUtil.syncMiniGameScore(p.dimension, teamID, Contained.gameScores[p.dimension][teamID]);
				}
				
				ClientPacketHandlerUtil.removeTreasureAndSync(dimID, eventLocation);
				MiniGameUtil.generateChest(w, 1, ContainedRegistry.CUSTOM_CHEST_LOOT);
			}
		}
	}
}

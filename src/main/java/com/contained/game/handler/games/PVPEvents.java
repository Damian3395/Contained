package com.contained.game.handler.games;

import java.awt.Point;
import java.util.HashMap;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemFood;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.DamageSource;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerUseItemEvent;
import codechicken.lib.packet.PacketCustom;

import com.contained.game.Contained;
import com.contained.game.entity.ExtendedPlayer;
import com.contained.game.network.ClientPacketHandlerUtil;
import com.contained.game.user.PlayerMiniGame;
import com.contained.game.user.PlayerTeam;
import com.contained.game.user.PlayerTeamIndividual;
import com.contained.game.util.Resources;
import com.contained.game.util.Util;
import com.contained.game.world.block.TownManageBlock;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class PVPEvents {
	public static HashMap<String, Point> initializePVPGame(int dimID) {
		// Find spawn points for each of the teams. They should be placed as far
		// separated from each other as possible.
		WorldServer w = Util.getWorldOrInitialize(dimID);
		
		ChunkCoordinates spawn = w.getSpawnPoint();
		float angle = 0;
		HashMap<String, Point> teamSpawnPoints = new HashMap<String, Point>();
		Contained.getTerritoryMap(dimID).clear();
		
		for (PlayerTeam team : Contained.getTeamList(dimID)) {
			Point newSpawnLocation = new Point(
					(int)(spawn.posX+(double)Contained.configs.getWorldRadius(dimID)*8D*Math.cos(angle)),
					(int)(spawn.posZ+(double)Contained.configs.getWorldRadius(dimID)*8D*Math.sin(angle)));	
			teamSpawnPoints.put(team.id, newSpawnLocation);
			angle += (2.0F*Math.PI)/(float)Contained.configs.gameNumTeams[Resources.PVP];			
			
			for (int i=-Contained.configs.pvpTerritorySize;i<=Contained.configs.pvpTerritorySize;i++) 
				for (int j=-Contained.configs.pvpTerritorySize;j<=Contained.configs.pvpTerritorySize;j++) 
					Contained.getTerritoryMap(dimID).put(new Point(newSpawnLocation.x+i, newSpawnLocation.y+j), team.id);
		
			int x = newSpawnLocation.x;
			int z = newSpawnLocation.y; 
			int y = w.getTopSolidOrLiquidBlock(x, z);
			w.setBlock(x, y, z, TownManageBlock.instance);
		}
		
		return teamSpawnPoints;
	}
	
	@SubscribeEvent
	public void onEntityDeath(LivingDeathEvent event) {
		DamageSource source = event.source;
		if(event.entityLiving != null && event.entityLiving instanceof EntityPlayer 
				&& source.getSourceOfDamage() instanceof EntityPlayer
				&& !event.entityLiving.worldObj.isRemote){
			EntityPlayer victim = (EntityPlayer) event.entityLiving;
			EntityPlayer killer = (EntityPlayer) source.getSourceOfDamage();
			ExtendedPlayer victimProp = ExtendedPlayer.get(victim);
			ExtendedPlayer killerProp = ExtendedPlayer.get(killer);
			
			if(victimProp.inGame() && victimProp.gameMode == Resources.PVP
					&& killerProp.inGame() && killerProp.gameMode == Resources.PVP){
				victimProp.removeLife();
				victimProp.curDeaths++;
				killerProp.curKills++;
				
				PlayerTeamIndividual killerData = PlayerTeamIndividual.get(killer);
				PlayerMiniGame miniGame = PlayerMiniGame.get(killerData.teamID);
				if(miniGame == null)
					return;
				
				int teamID = miniGame.getTeamID(killerData);
				if (teamID != -1) {
					Contained.gameScores[miniGame.getGameDimension()][teamID]+=3;
					ClientPacketHandlerUtil.syncMiniGameScore(killer.dimension, teamID, Contained.gameScores[killer.dimension][teamID]);
				}
			}
		}
	}
	
	@SubscribeEvent
	public void onItemUsed(PlayerUseItemEvent.Finish event) {
		if(event.entityPlayer != null && event.item != null
				&& !event.entityPlayer.worldObj.isRemote && event.item.getItem() instanceof ItemFood){
			ExtendedPlayer properties = ExtendedPlayer.get(event.entityPlayer);
			if(properties.inGame() && properties.gameMode == Resources.PVP 
					&& event.item.getDisplayName().equals("Apple of Life")){
				properties.addLife();
				PacketCustom syncLifePacket = new PacketCustom(Resources.MOD_ID, ClientPacketHandlerUtil.SYNC_LIVES);
				syncLifePacket.writeInt(properties.lives);
				Contained.channel.sendTo(syncLifePacket.toPacket(), (EntityPlayerMP) event.entityPlayer);
			}
		}
	}
}

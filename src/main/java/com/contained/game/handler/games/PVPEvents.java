package com.contained.game.handler.games;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemFood;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerUseItemEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.Clone;
import codechicken.lib.packet.PacketCustom;

import com.contained.game.Contained;
import com.contained.game.entity.ExtendedPlayer;
import com.contained.game.network.ClientPacketHandlerUtil;
import com.contained.game.user.PlayerMiniGame;
import com.contained.game.user.PlayerTeamIndividual;
import com.contained.game.util.Resources;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class PVPEvents {
	@SubscribeEvent
	public void onSpawn(Clone event){
		if(event.wasDeath && event.entityPlayer != null && !event.entityPlayer.worldObj.isRemote){
			ExtendedPlayer properties = ExtendedPlayer.get(event.entityPlayer);
			if(properties.inGame() && properties.gameMode == Resources.PVP){				
				properties.setLives(ExtendedPlayer.get(event.original).lives);
				
				PacketCustom syncLifePacket = new PacketCustom(Resources.MOD_ID, ClientPacketHandlerUtil.SYNC_LIVES);
				syncLifePacket.writeInt(properties.lives);
				Contained.channel.sendTo(syncLifePacket.toPacket(), (EntityPlayerMP) event.entityPlayer);
				
				if(properties.lives == 0){
					PacketCustom spectatorPacket = new PacketCustom(Resources.MOD_ID, ClientPacketHandlerUtil.PLAYER_SPECTATOR);
					Contained.channel.sendTo(spectatorPacket.toPacket(), (EntityPlayerMP) event.entityPlayer);
				}
			}
		}
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
				victimProp.deaths++;
				victimProp.curDeaths++;
				killerProp.kills++;
				killerProp.curKills++;
				
				PacketCustom syncLifePacket = new PacketCustom(Resources.MOD_ID, ClientPacketHandlerUtil.SYNC_LIVES);
				syncLifePacket.writeInt(victimProp.lives);
				Contained.channel.sendTo(syncLifePacket.toPacket(), (EntityPlayerMP) victim);
			
				PlayerTeamIndividual killerData = PlayerTeamIndividual.get(killer);
				PlayerMiniGame miniGame = PlayerMiniGame.get(killerData.teamID);
				if(miniGame == null)
					return;
				
				int teamID = miniGame.getTeamID(killerData);
				Contained.gameScores[miniGame.getGameDimension()][teamID]++;
				ClientPacketHandlerUtil.syncMiniGameScore(killer.dimension, teamID, Contained.gameScores[killer.dimension][teamID]);
				
				if(Contained.gameScores[miniGame.getGameDimension()][teamID] == 50)
					miniGame.endGame();
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

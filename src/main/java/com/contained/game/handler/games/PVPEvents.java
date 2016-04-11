package com.contained.game.handler.games;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemFood;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerUseItemEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.Clone;
import codechicken.lib.packet.PacketCustom;

import com.contained.game.Contained;
import com.contained.game.entity.ExtendedPlayer;
import com.contained.game.network.ClientPacketHandlerUtil;
import com.contained.game.util.Resources;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class PVPEvents {
	@SubscribeEvent
	public void onSpawn(Clone event){
		if(event.wasDeath && event.entityPlayer != null && !event.entityPlayer.worldObj.isRemote){
			ExtendedPlayer properties = ExtendedPlayer.get(event.entityPlayer);
			if(properties.inGame() && properties.gameMode == Resources.PVP_MODE){
				properties.setLives(ExtendedPlayer.get(event.original).lives);
				
				PacketCustom syncLifePacket = new PacketCustom(Resources.MOD_ID, ClientPacketHandlerUtil.SYNC_LIVES);
				syncLifePacket.writeInt(properties.lives);
				Contained.channel.sendTo(syncLifePacket.toPacket(), (EntityPlayerMP) event.entityPlayer);
				
				if(properties.lives == 0){
					PacketCustom endGamePacket = new PacketCustom(Resources.MOD_ID, ClientPacketHandlerUtil.END_GAME);
					Contained.channel.sendTo(endGamePacket.toPacket(), (EntityPlayerMP) event.entityPlayer);
				}
			}
		}
	}
	
	@SubscribeEvent
	public void onEntityDeath(LivingDeathEvent event) {
		if(event.entityLiving != null && event.entityLiving instanceof EntityPlayer && !event.entityLiving.worldObj.isRemote){
			EntityPlayer player = (EntityPlayer) event.entityLiving;
			ExtendedPlayer properties = ExtendedPlayer.get(player);
			if(properties.inGame() && properties.gameMode == Resources.PVP_MODE){
				properties.removeLife();
				PacketCustom syncLifePacket = new PacketCustom(Resources.MOD_ID, ClientPacketHandlerUtil.SYNC_LIVES);
				syncLifePacket.writeInt(properties.lives);
				Contained.channel.sendTo(syncLifePacket.toPacket(), (EntityPlayerMP) player);
			}
		}
	}
	
	@SubscribeEvent
	public void onItemUsed(PlayerUseItemEvent.Finish event) {
		if(event.entityPlayer != null && event.item != null
				&& !event.entityPlayer.worldObj.isRemote && event.item.getItem() instanceof ItemFood){
			ExtendedPlayer properties = ExtendedPlayer.get(event.entityPlayer);
			if(properties.inGame() && properties.gameMode == Resources.PVP_MODE 
					&& event.item.getDisplayName().equals("Apple of Life")){
				properties.addLife();
				PacketCustom syncLifePacket = new PacketCustom(Resources.MOD_ID, ClientPacketHandlerUtil.SYNC_LIVES);
				syncLifePacket.writeInt(properties.lives);
				Contained.channel.sendTo(syncLifePacket.toPacket(), (EntityPlayerMP) event.entityPlayer);
			}
		}
	}
}

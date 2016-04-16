package com.contained.game.handler.games;

import com.contained.game.Contained;
import com.contained.game.entity.ExtendedPlayer;
import com.contained.game.network.ServerPacketHandlerUtil;
import com.contained.game.util.Resources;
import com.contained.game.util.Util;

import codechicken.lib.packet.PacketCustom;
import net.minecraft.block.BlockChest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class TreasureEvents {
	@SubscribeEvent
	public void onTreasureChestOpen(PlayerInteractEvent event){
		if(event.entityLiving instanceof EntityPlayer
				&&!event.entityPlayer.getEntityWorld().isRemote 
				&& (event.entityPlayer.getEntityWorld().getBlock(event.x, event.y, event.z) instanceof BlockChest) 
				&& event.action == Action.RIGHT_CLICK_BLOCK){
			ExtendedPlayer properties = ExtendedPlayer.get(event.entityPlayer);
			if(properties.inGame() && properties.gameMode == Resources.TREASURE_MODE){
//				BlockChest chest = (BlockChest)event.entityPlayer.getEntityWorld().getBlock(event.x, event.y, event.z);
//				chest.dropBlockAsItem(event.entityPlayer.getEntityWorld(), event.x, event.y, event.z, event.entityPlayer.getEntityWorld().getBlockMetadata(event.x, event.y, event.z), 0);
				event.entityPlayer.getEntityWorld().setBlockToAir(event.x, event.y, event.z);
				System.out.println("setBlockToAir("+event.x+","+event.y+","+event.z+")");
				PacketCustom refreshChestPacket = new PacketCustom(Resources.MOD_ID, ServerPacketHandlerUtil.REFRESH_CHEST);
				Contained.channel.sendToServer(refreshChestPacket.toPacket());
				Util.dimensionMessage(event.entityPlayer.dimension, Util.infoCode + event.entityPlayer.getDisplayName()+"has found a chest!");
			}
		}
	}
}

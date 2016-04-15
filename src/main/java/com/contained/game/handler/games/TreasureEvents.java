package com.contained.game.handler.games;

import net.minecraft.block.BlockChest;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class TreasureEvents {
	@SubscribeEvent
	public void onTreasureChestOpen(PlayerInteractEvent event){
		
		if(!event.entityPlayer.getEntityWorld().isRemote 
				&& (event.entityPlayer.getEntityWorld().getBlock(event.x, event.y, event.z) instanceof BlockChest) 
				&& event.action == Action.RIGHT_CLICK_BLOCK){
			System.out.println("Event Detected");
			// TODO: Keep queue of active chests, and remove chest from queue
			// upon open. This is important because opening the same chest twice
			// shouldn't award more than one point AND it's very important to
			// distinguish between chests belonging to the mini-game, and chests
			// crafted by players.
			
			// TODO: Packet sending is unnecessary here! Sending packet from
			// server to server?? Although when visualizations are ready, will
			// need a packet here to update visualizations on the client side.
			//PacketCustom refreshChestPacket = new PacketCustom(Resources.MOD_ID, ServerPacketHandlerUtil.REFRESH_CHEST);
			//Contained.channel.sendToServer(refreshChestPacket.toPacket());
			
			System.out.println(event.entityPlayer.getDisplayName()+"has found a chest!");
			return;
		}
		
	}
}

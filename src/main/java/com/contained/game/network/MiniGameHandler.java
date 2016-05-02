package com.contained.game.network;
import codechicken.lib.packet.PacketCustom;

import com.contained.game.Contained;
import com.contained.game.entity.ExtendedPlayer;
import com.contained.game.util.Resources;

import net.minecraft.entity.player.EntityPlayerMP;

public class MiniGameHandler {
	public MiniGameHandler(){}
	
	public static void joinMiniGame(EntityPlayerMP player){
		ExtendedPlayer properties = ExtendedPlayer.get(player);
		properties.setJoiningGame(true);
		
		PacketCustom packet = new PacketCustom(Resources.MOD_ID, ClientPacketHandlerUtil.JOIN_MINI_GAME);
		Contained.channel.sendTo(packet.toPacket(), player);
	}
	
	public static void cancelMiniGame(EntityPlayerMP player){
		ExtendedPlayer properties = ExtendedPlayer.get(player);
		properties.setJoiningGame(false);
		
		PacketCustom packet = new PacketCustom(Resources.MOD_ID, ClientPacketHandlerUtil.CANCEL_JOIN_MINI_GAME);
		Contained.channel.sendTo(packet.toPacket(), player);
	}
}

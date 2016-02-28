package com.contained.game.network;

import com.contained.game.data.Data;
import com.contained.game.entity.ExtendedPlayer;

import codechicken.lib.packet.PacketCustom;
import codechicken.lib.packet.PacketCustom.IServerPacketHandler;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.INetHandlerPlayServer;

public class ServerPacketHandler implements IServerPacketHandler{
	@Override
	public void handlePacket(PacketCustom packet, EntityPlayerMP player, INetHandlerPlayServer server) {
		switch(packet.getType()) {
		case 1:
			//Update Player Selected Class
			ExtendedPlayer.get(player).occupationClass = packet.readInt();
			break;
		case 2:
			//Update Player Class Level
			ExtendedPlayer.get(player).occupationLevel = packet.readInt();
			break;
		}
	}
}

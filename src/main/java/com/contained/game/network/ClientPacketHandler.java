package com.contained.game.network;

import net.minecraft.client.Minecraft;
import net.minecraft.network.play.INetHandlerPlayClient;

import com.contained.game.data.Data;
import com.contained.game.entity.ExtendedPlayer;
import com.contained.game.ui.DataVisualization;

import codechicken.lib.packet.PacketCustom;
import codechicken.lib.packet.PacketCustom.IClientPacketHandler;

public class ClientPacketHandler implements IClientPacketHandler{
	private DataVisualization gui;
	
	public ClientPacketHandler(DataVisualization gui) {
		this.gui = gui;
	}
	
	@Override
	public void handlePacket(PacketCustom packet, Minecraft mc, INetHandlerPlayClient net) {
		switch(packet.getType()) {
			case 1:
				//Occupational Data
				for(int i=0; i<Data.occupationNames.length; i++)
					ExtendedPlayer.get(mc.thePlayer).setOccupation(i, packet.readInt());
				break;
				
			case 2:
				//Item Usage Data
				ExtendedPlayer.get(mc.thePlayer).usedOwnItems = packet.readInt();
				ExtendedPlayer.get(mc.thePlayer).usedOthersItems = packet.readInt();
				ExtendedPlayer.get(mc.thePlayer).usedByOthers = packet.readInt();
				break;
		}
	}
}

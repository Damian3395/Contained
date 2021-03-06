package com.contained.game.network;

import codechicken.lib.packet.PacketCustom;

import com.contained.game.Contained;
import com.contained.game.data.DataLogger;
import com.contained.game.entity.ExtendedPlayer;
import com.contained.game.util.Resources;
import com.contained.game.util.Util;

import net.minecraft.entity.player.EntityPlayerMP;

public class PerkHandler {
	public PerkHandler(){}
	
	public void levelUp(EntityPlayerMP player, int perkID, int level){
		ExtendedPlayer.get(player).addPerk(perkID);
		ExtendedPlayer.get(player).occupationLevel = level;
		
		PacketCustom perkPacket = new PacketCustom(Resources.MOD_ID, ClientPacketHandlerUtil.LEVEL_UP);
		perkPacket.writeInt(level);
		perkPacket.writeInt(perkID);
		Contained.channel.sendTo(perkPacket.toPacket(), player);
		
		String world = Util.getDimensionString(player.dimension);
		DataLogger.insertPerk("debugmode", player.getDisplayName(), world, perkID, Util.getDate());
	}
	
	public void selectClassOccupation(EntityPlayerMP player, int classID){
		ExtendedPlayer.get(player).occupationClass = classID;
		
		PacketCustom perkPacket = new PacketCustom(Resources.MOD_ID, ClientPacketHandlerUtil.SELECT_CLASS);
		perkPacket.writeInt(classID);
		Contained.channel.sendTo(perkPacket.toPacket(), player);
	}
}

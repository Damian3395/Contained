package com.contained.game.network;

import net.minecraft.nbt.NBTTagCompound;
import codechicken.lib.packet.PacketCustom;

import com.contained.game.Contained;
import com.contained.game.user.PlayerTeam;
import com.contained.game.user.PlayerTeamIndividual;
import com.contained.game.util.Resources;

import cpw.mods.fml.common.network.internal.FMLProxyPacket;

public class ServerPacketHandlerUtil {
	public static final int UPDATE_CLASS = 1;
	public static final int UPDATE_CLASSLEVEL = 2;
	public static final int OFFSET_XPLEVEL = 3;
	public static final int INVENTORY_REMOVE = 4;
	public static final int INVENTORY_ADD = 5;
	
	public static final int GUILD_JOIN = 6;
	public static final int GUILD_LEAVE = 7;
	public static final int GUILD_CREATE = 8;
	public static final int GUILD_DISBAND = 9;
	public static final int GUILD_UPDATE = 10;
	public static final int PLAYER_INVITE = 11;
	public static final int PLAYER_DECLINE = 12;
	public static final int PLAYER_KICK = 13;
	public static final int PLAYER_PROMOTE = 14;
	public static final int PLAYER_DEMOTE = 15;
	
	public static final int LEVEL_UP = 16;
	public static final int SELECT_CLASS = 17;
	
	public static final int UPDATE_PERMISSIONS = 18;
	
	public static final int PLAYER_TRADE = 19;
	public static final int CREATE_TRADE = 20;
	public static final int CANCEL_TRADE = 21;
	
	public static final int UPDATE_SURVEY = 22;
	public static final int JOIN_MINI_GAME = 23;
	public static final int CANCEL_JOIN_MINI_GAME = 24;
	public static final int REVIVE_PLAYER = 25;
	
	public static final int BECOME_ADMIN = 26;
	public static final int ADMIN_CREATE = 27;
	public static final int ADMIN_CHANGE = 28;
	public static final int ADMIN_JOIN = 29;
	public static final int ADMIN_KICK = 30;
	public static final int ADMIN_SPECT = 31;
	public static final int ADMIN_WORLD_INFO = 32;
	
	public static final int LOG_PERSONALITY = 33;
	
	public static void sendToServer(FMLProxyPacket packet) {
		Contained.channel.sendToServer(packet);
	}
	
	public static PacketCustom packetUpdatePermissions(PlayerTeam toSync) {
		PacketCustom permPacket = new PacketCustom(Resources.MOD_ID, UPDATE_PERMISSIONS);
		NBTTagCompound teamData = new NBTTagCompound();
		toSync.writeToNBT(teamData);
		permPacket.writeNBTTagCompound(teamData);
		return permPacket;
	}

	public static PacketCustom packetUpdateSurvey(PlayerTeamIndividual pdata) {
		PacketCustom surveyPacket = new PacketCustom(Resources.MOD_ID, UPDATE_SURVEY);
		NBTTagCompound surveyData = new NBTTagCompound();
		pdata.surveyResponses.writeToNBT(surveyData);
		surveyPacket.writeString(pdata.playerName);
		surveyPacket.writeNBTTagCompound(surveyData);
		return surveyPacket;
	}
}

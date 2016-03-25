package com.contained.game.network;

import java.util.ArrayList;

import com.contained.game.Contained;
import com.contained.game.entity.ExtendedPlayer;
import com.contained.game.user.PlayerTeam;
import com.contained.game.user.PlayerTeamIndividual;
import com.contained.game.util.Resources;
import com.contained.game.util.Util;

import codechicken.lib.packet.PacketCustom;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent.ServerCustomPacketEvent;
import cpw.mods.fml.common.network.internal.FMLProxyPacket;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetHandlerPlayServer;

/**
 * Handling of packets sent from client to server.
 */
public class ServerPacketHandler {

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
	
	public static final int UPDATE_SURVEY = 21;

	protected String channelName;
	protected EntityPlayerMP player;

	private GuildHandler guild = new GuildHandler();
	private PerkHandler perk = new PerkHandler();
	
	@SubscribeEvent
	public void handlePacket(ServerCustomPacketEvent event) {		
		channelName = event.packet.channel();
		NetHandlerPlayServer net = (NetHandlerPlayServer)event.handler;
		player = net.playerEntity;

		if (channelName.equals(Resources.MOD_ID) && event.packet.getTarget() == Side.SERVER) {
			PacketCustom packet = new PacketCustom(event.packet.payload());
			
			switch(packet.getType()) {
				case UPDATE_CLASS:
					ExtendedPlayer.get(player).occupationClass = packet.readInt();
				break;
	
				case UPDATE_CLASSLEVEL:
					ExtendedPlayer.get(player).occupationLevel = packet.readInt();
				break;
	
				case OFFSET_XPLEVEL:
					player.experienceLevel += packet.readInt();
				break;
	
				case INVENTORY_REMOVE:
					ItemStack toRemove = packet.readItemStack();
					Util.removeItem(toRemove, player);
				break;
				
				case INVENTORY_ADD:
					ItemStack toSpawn = packet.readItemStack();
					player.worldObj.spawnEntityInWorld(new EntityItem(player.worldObj, player.posX, player.posY+1, player.posZ, toSpawn));
				break;
				
				case GUILD_JOIN:
					guild.joinTeam(player, packet.readString());
				break;
				
				case GUILD_LEAVE:
					guild.leaveTeam(player);
				break;
				
				case GUILD_CREATE:
					guild.createTeam(player, packet.readString(), packet.readInt());
				break;
				
				case GUILD_DISBAND:
					guild.disbandTeam(player, packet.readString());
				break;
				
				case GUILD_UPDATE:
					guild.updateTeam(player, packet.readString(), packet.readInt());
				break;
				
				case PLAYER_INVITE:
					guild.invitePlayer(player, packet.readString());
				break;
				
				case PLAYER_DECLINE:
					guild.declineInvite(player, packet.readString());
				break;
				
				case PLAYER_KICK:
					guild.kickPlayer(player, packet.readString());
				break;
				
				case PLAYER_PROMOTE:
					guild.promotePlayer(player , packet.readString());
				break;
				
				case PLAYER_DEMOTE:
					guild.demotePlayer(player);
				break;
				
				case LEVEL_UP:
					perk.levelUp(player, packet.readInt(), packet.readInt());
				break;
				
				case SELECT_CLASS:
					perk.selectClassOccupation(player, packet.readInt());
				break;
				
				case UPDATE_PERMISSIONS:
					PlayerTeam team = new PlayerTeam(packet.readNBTTagCompound());
					// Prune out any teams from the permission list that may have
					// gone defunct between syncing.
					ArrayList<String> teamPermsToRemove = new ArrayList<String>();
					for (String teamID : team.permissions.keySet()) {
						if (PlayerTeam.get(teamID) == null)
							teamPermsToRemove.add(teamID);
					}
					for (String teamID : teamPermsToRemove)
						team.permissions.remove(teamID);
					
					PlayerTeam toModify = PlayerTeam.get(team);
					toModify.permissions = team.permissions;
					
					//Sync new permission data to all clients.
					PacketCustom sync = ClientPacketHandler.packetUpdatePermissions(toModify);
					Contained.channel.sendToAll(sync.toPacket());
				break;
				
				case PLAYER_TRADE:
					
				break;
				
				case CREATE_TRADE:
				
				break;
				
				case UPDATE_SURVEY:
					PlayerTeamIndividual toUpdate = PlayerTeamIndividual.get(packet.readString());
					toUpdate.surveyProgress = packet.readInt();
					NBTTagCompound surveyData = packet.readNBTTagCompound();
					toUpdate.surveyResponses = surveyData.getIntArray("surveyResponses");
				break;
			}
		}
	}

	public static void sendToServer(FMLProxyPacket packet) {
		Contained.channel.sendToServer(packet);
	}
	
	/**
	 * ====================================
	 *   Packet Sending Util
	 * ====================================
	 */
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
		surveyData.setIntArray("surveyResponses", pdata.surveyResponses);
		surveyPacket.writeString(pdata.playerName);
		surveyPacket.writeInt(pdata.surveyProgress);
		surveyPacket.writeNBTTagCompound(surveyData);
		return surveyPacket;
	}
}

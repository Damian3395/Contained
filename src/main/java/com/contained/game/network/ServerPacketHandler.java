package com.contained.game.network;

import java.util.ArrayList;

import com.contained.game.Contained;
import com.contained.game.entity.ExtendedPlayer;
import com.contained.game.user.PlayerTeam;
import com.contained.game.user.PlayerTeamPermission;
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
	public static final int UPDATE_GUILD_STATUS = 6;
	public static final int UPDATE_PERMISSIONS = 7;

	protected String channelName;
	protected EntityPlayerMP player;

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
				
				case UPDATE_GUILD_STATUS:
					ExtendedPlayer.get(player).guild = packet.readInt();
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
}

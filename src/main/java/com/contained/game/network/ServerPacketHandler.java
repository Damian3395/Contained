package com.contained.game.network;

import com.contained.game.Contained;
import com.contained.game.entity.ExtendedPlayer;
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
			}
		}
	}

	public static void sendToServer(FMLProxyPacket packet) {
		Contained.channel.sendToServer(packet);
	}
}

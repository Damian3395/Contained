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
import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.network.NetHandlerPlayServer;

/**
 * Handling of packets sent from client to server.
 */
public class ServerPacketHandler {
	protected String channelName;
	protected EntityPlayerMP player;

	private GuildHandler guild = new GuildHandler();
	private PerkHandler perk = new PerkHandler();
	private TradeHandler trade = new TradeHandler();
	
	@SubscribeEvent
	public void handlePacket(ServerCustomPacketEvent event) {		
		channelName = event.packet.channel();
		NetHandlerPlayServer net = (NetHandlerPlayServer)event.handler;
		player = net.playerEntity;

		if (channelName.equals(Resources.MOD_ID) && event.packet.getTarget() == Side.SERVER) {
			PacketCustom packet = new PacketCustom(event.packet.payload());
			
			switch(packet.getType()) {
				case ServerPacketHandlerUtil.UPDATE_CLASS:
					ExtendedPlayer.get(player).occupationClass = packet.readInt();
				break;
	
				case ServerPacketHandlerUtil.UPDATE_CLASSLEVEL:
					ExtendedPlayer.get(player).occupationLevel = packet.readInt();
				break;
	
				case ServerPacketHandlerUtil.OFFSET_XPLEVEL:
					player.experienceLevel += packet.readInt();
				break;
	
				case ServerPacketHandlerUtil.INVENTORY_REMOVE:
					ItemStack toRemove = packet.readItemStack();
					Util.removeItem(toRemove, player);
				break;
				
				case ServerPacketHandlerUtil.INVENTORY_ADD:
					ItemStack toSpawn = packet.readItemStack();
					player.worldObj.spawnEntityInWorld(new EntityItem(player.worldObj, player.posX, player.posY+1, player.posZ, toSpawn));
				break;
				
				case ServerPacketHandlerUtil.GUILD_JOIN:
					guild.joinTeam(player, packet.readString());
				break;
				
				case ServerPacketHandlerUtil.GUILD_LEAVE:
					guild.leaveTeam(player);
				break;
				
				case ServerPacketHandlerUtil.GUILD_CREATE:
					guild.createTeam(player, packet.readString(), packet.readInt());
				break;
				
				case ServerPacketHandlerUtil.GUILD_DISBAND:
					guild.disbandTeam(player, packet.readString());
				break;
				
				case ServerPacketHandlerUtil.GUILD_UPDATE:
					guild.updateTeam(player, packet.readString(), packet.readInt());
				break;
				
				case ServerPacketHandlerUtil.PLAYER_INVITE:
					guild.invitePlayer(player, packet.readString());
				break;
				
				case ServerPacketHandlerUtil.PLAYER_DECLINE:
					guild.declineInvite(player, packet.readString());
				break;
				
				case ServerPacketHandlerUtil.PLAYER_KICK:
					guild.kickPlayer(player, packet.readString());
				break;
				
				case ServerPacketHandlerUtil.PLAYER_PROMOTE:
					guild.promotePlayer(player , packet.readString());
				break;
				
				case ServerPacketHandlerUtil.PLAYER_DEMOTE:
					guild.demotePlayer(player);
				break;
				
				case ServerPacketHandlerUtil.LEVEL_UP:
					perk.levelUp(player, packet.readInt(), packet.readInt());
				break;
				
				case ServerPacketHandlerUtil.SELECT_CLASS:
					perk.selectClassOccupation(player, packet.readInt());
				break;
				
				case ServerPacketHandlerUtil.UPDATE_PERMISSIONS:
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
					PacketCustom sync = ClientPacketHandlerUtil.packetUpdatePermissions(toModify);
					Contained.channel.sendToAll(sync.toPacket());
				break;
				
				case ServerPacketHandlerUtil.PLAYER_TRADE:
					trade.transaction(player, packet.readNBTTagCompound());
				break;
				
				case ServerPacketHandlerUtil.CREATE_TRADE:
					trade.create(player, packet.readInt(), packet.readItemStack(), packet.readItemStack());
				break;
				
				case ServerPacketHandlerUtil.CANCEL_TRADE:
					trade.cancel(player, packet.readString());
				break;
				
				case ServerPacketHandlerUtil.UPDATE_SURVEY:
					PlayerTeamIndividual toUpdate = PlayerTeamIndividual.get(packet.readString());
					toUpdate.surveyResponses.readFromNBT(packet.readNBTTagCompound());
				break;
			}
		}
	}
}

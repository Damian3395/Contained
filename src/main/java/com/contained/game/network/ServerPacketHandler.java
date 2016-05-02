package com.contained.game.network;

import java.util.ArrayList;

import com.contained.game.Contained;
import com.contained.game.data.DataLogger;
import com.contained.game.entity.ExtendedPlayer;
import com.contained.game.ui.survey.SurveyData;
import com.contained.game.user.PlayerTeam;
import com.contained.game.user.PlayerTeamIndividual;
import com.contained.game.util.MiniGameUtil;
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
import net.minecraft.server.MinecraftServer;

/**
 * Handling of packets sent from client to server.
 */
public class ServerPacketHandler {
	protected String channelName;
	protected EntityPlayerMP player;

	private GuildHandler guild = new GuildHandler();
	private PerkHandler perk = new PerkHandler();
	private TradeHandler trade = new TradeHandler();
	private MiniGameHandler games = new MiniGameHandler();
	private AdminHandler admin = new AdminHandler();
	
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
						if (PlayerTeam.get(teamID, team.dimID) == null)
							teamPermsToRemove.add(teamID);
					}
					for (String teamID : teamPermsToRemove)
						team.permissions.remove(teamID);
					
					PlayerTeam toModify = PlayerTeam.get(team, team.dimID);
					toModify.permissions = team.permissions;
					
					//Sync new permission data to all clients.
					PacketCustom sync = ClientPacketHandlerUtil.packetUpdatePermissions(toModify);
					Contained.channel.sendToAll(sync.toPacket());
				break;
				
				case ServerPacketHandlerUtil.PLAYER_TRADE:
					trade.transaction(player, packet.readString());
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
				
				case ServerPacketHandlerUtil.JOIN_MINI_GAME:
					games.joinMiniGame(player);
				break;
				
				case ServerPacketHandlerUtil.CANCEL_JOIN_MINI_GAME:
					games.cancelMiniGame(player);
				break;
				
				case ServerPacketHandlerUtil.REVIVE_PLAYER:
					int dimID = packet.readInt();
					String revivePlayer = packet.readString();
					EntityPlayerMP teamPlayer = (EntityPlayerMP) MinecraftServer.getServer().worldServers[dimID].getPlayerEntityByName(revivePlayer);
					ExtendedPlayer deadProperties = ExtendedPlayer.get(teamPlayer);
					
					if(!MiniGameUtil.isPvP(dimID))
						return;
					
					if(deadProperties.lives == 0){
						//Revive Player
						deadProperties.resurrect();
						
						//Sync Client Lives
						PacketCustom syncLifePacket = new PacketCustom(Resources.MOD_ID, ClientPacketHandlerUtil.SYNC_LIVES);
						syncLifePacket.writeInt(deadProperties.lives);
						Contained.channel.sendTo(syncLifePacket.toPacket(), teamPlayer);
						
						//Sync Client GameMode
						PacketCustom normalPacket = new PacketCustom(Resources.MOD_ID, ClientPacketHandlerUtil.PLAYER_NORMAL);
						Contained.channel.sendTo(normalPacket.toPacket(), teamPlayer);
						
						//Remove Stick of Life
						int slotID = 0;
						for(int i = 0; i < player.inventory.getSizeInventory(); i++)
							if(player.inventory.getStackInSlot(i) != null
								&& player.inventory.getStackInSlot(i).getDisplayName().equals("Stick of Life")){
								slotID = i;
								player.inventory.setInventorySlotContents(i, null);
								break;
							}
						
						//Sync Client Items
						PacketCustom removeLifeStickPacket = new PacketCustom(Resources.MOD_ID, ClientPacketHandlerUtil.REMOVE_ITEM);
						syncLifePacket.writeInt(slotID);
						Contained.channel.sendTo(removeLifeStickPacket.toPacket(), teamPlayer);
						
						DataLogger.insertRestoreLife(Util.getServerID(), Util.getDimensionString(dimID), Util.getGameID(dimID), PlayerTeamIndividual.get(player.getDisplayName()).teamID, player.getDisplayName(), revivePlayer, Util.getDate());
					}else
						Util.displayMessage(player, "Selected Team-Player Is Already Alive!");
				break;
				
				case ServerPacketHandlerUtil.BECOME_ADMIN:
					admin.becomeAdmin(player);					
				break;
				
				/**
				 *  packet input:
				 *  @String object: name of Object to generate
				 *  @String name:	name of Player around whom generation occurs
				 */
				case ServerPacketHandlerUtil.ADMIN_CREATE:
					admin.create(player, packet.readString(), packet.readString());
				break;
				
				/**
				 *  packet input:
				 *  @Int healthPercentage: the percentage of health to set 
				 *  @Int hungerPercentage: the percentage of food level to set
				 *  @String playerName: name of Player whose status is to change
				 */
				case ServerPacketHandlerUtil.ADMIN_CHANGE:
					admin.change(player, packet.readInt(), packet.readInt(), packet.readString());
				break;
				
				/**
				 *  packet input:
				 *  @Int dimension: the dimension the Admin wants to join
				 */
				case ServerPacketHandlerUtil.ADMIN_JOIN:
					admin.join(player, packet.readInt());
				break;
				
				/**
				 *  packet input:
				 *  @Int dimension: the dimension the Admin wants to view info of
				 */
				case ServerPacketHandlerUtil.ADMIN_WORLD_INFO:
					admin.viewWorldInfo(player, packet.readInt());
				break;
				
				/**
				 *  packet input:
				 *  @Int dimension: the dimension the Admin wants to join
				 *  @String targetName: the player's name the Admin wants to spect
				 */
				case ServerPacketHandlerUtil.ADMIN_SPECT:
					admin.spect(player, packet.readString());
				break;
				
				/**
				 *  packet input:
				 *  @String targetName: the target player's name
				 */
				case ServerPacketHandlerUtil.ADMIN_KICK:
					admin.kick(player, packet.readString());
				break;
				
				case ServerPacketHandlerUtil.ADMIN_REGULAR_PLAYER:
					admin.becomeRegularPlayer(player);
				break;
				
				case ServerPacketHandlerUtil.LOG_PERSONALITY:
					PlayerTeamIndividual pdata = PlayerTeamIndividual.get(player);
					DataLogger.insertPersonality(Util.getServerID(), player.getDisplayName(), 
							pdata.surveyResponses.age, pdata.surveyResponses.mcYears, pdata.surveyResponses.mcMonths, 
							pdata.surveyResponses.ethnicity, 
							SurveyData.scoreResponses(SurveyData.Q.OPENNESS, pdata.surveyResponses.personality), 
							SurveyData.scoreResponses(SurveyData.Q.CONSCIENTIOUSNESS, pdata.surveyResponses.personality),
							SurveyData.scoreResponses(SurveyData.Q.EXTRAVERSION, pdata.surveyResponses.personality), 
							SurveyData.scoreResponses(SurveyData.Q.AGREEABLENESS, pdata.surveyResponses.personality), 
							SurveyData.scoreResponses(SurveyData.Q.NEUROTICISM, pdata.surveyResponses.personality), Util.getDate());
				break;
			}
		}
	}
}

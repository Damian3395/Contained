package com.contained.game.network;

import java.util.ArrayList;

import com.contained.game.Contained;
import com.contained.game.entity.ExtendedPlayer;
import com.contained.game.user.PlayerTeam;
import com.contained.game.user.PlayerTeamIndividual;
import com.contained.game.util.ObjectGenerator;
import com.contained.game.util.Resources;
import com.contained.game.util.Util;

import codechicken.lib.packet.PacketCustom;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent.ServerCustomPacketEvent;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
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
					}else
						Util.displayMessage(player, "Selected Team-Player Is Already Alive!");
				break;
				
				case ServerPacketHandlerUtil.BECOME_ADMIN:
					player.setInvisible(true);
					player.capabilities.allowFlying = true;
					player.capabilities.disableDamage = true;
					ExtendedPlayer.get(player).setAdminRights(true);
					PacketCustom adminPacket = new PacketCustom(Resources.MOD_ID, ClientPacketHandlerUtil.PLAYER_ADMIN);
					Contained.channel.sendTo(adminPacket.toPacket(),this.player);
				break;
				
				case ServerPacketHandlerUtil.ADMIN_CREATE:
					int x,y,z;
					String object,name;
					object=packet.readString();
					name=packet.readString();
					x = this.player.getServerForPlayer().getPlayerEntityByName(name).getPlayerCoordinates().posX;
					y = this.player.getServerForPlayer().getPlayerEntityByName(name).getPlayerCoordinates().posY;
					z = this.player.getServerForPlayer().getPlayerEntityByName(name).getPlayerCoordinates().posZ;
					ObjectGenerator og = new ObjectGenerator();
					if(!og.generate(object, this.player.getServerForPlayer(), x, y, z)){
						System.out.println("Server: Cannot generate <"+object+"> ");
					}else{
						System.out.println("Server: Generate <"+object+"> succedded");
					}
				break;
					
				case ServerPacketHandlerUtil.ADMIN_CHANGE:
					int healthPercentage,hungerPercentage;
					String playerName;
					healthPercentage=packet.readInt();
					hungerPercentage=packet.readInt();
					playerName=packet.readString();
					try{
	    				EntityPlayer target = this.player.getServerForPlayer().getPlayerEntityByName(playerName); 
	    				if(healthPercentage!=-1){
	    					
	    					if(healthPercentage<=100 && healthPercentage>0){
		    					target.setHealth(target.getMaxHealth()*healthPercentage/100);
		    				}
	    				}
	    				if(hungerPercentage!=-1){
	    					
	    					if(hungerPercentage<=100 && hungerPercentage>0){
		    					target.getFoodStats().setFoodLevel(20*hungerPercentage/100);
		    				}
	    				}
					}catch(Exception e){
						//handle exception, tell client side.
					}
				break;
			}
		}
	}
}

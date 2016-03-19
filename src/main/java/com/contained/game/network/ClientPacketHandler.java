package com.contained.game.network;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;

import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;

import com.contained.game.data.Data;
import com.contained.game.entity.ExtendedPlayer;
import com.contained.game.ui.ClassPerks;
import com.contained.game.ui.DataVisualization;
import com.contained.game.ui.GuiGuild;
import com.contained.game.ui.TerritoryRender;
import com.contained.game.ui.guild.GuildBase;
import com.contained.game.ui.guild.GuildLeader;
import com.contained.game.user.PlayerTeam;
import com.contained.game.util.Resources;
import com.contained.game.world.block.TerritoryMachineTE;

import codechicken.lib.packet.PacketCustom;
import codechicken.lib.vec.BlockCoord;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent.ClientCustomPacketEvent;
import cpw.mods.fml.relauncher.Side;

/**
 * Handling of packets sent from server to client.
 */
public class ClientPacketHandler extends ServerPacketHandler {
	private DataVisualization gui;
	private TerritoryRender render;
	
	public static final int OCCUPATIONAL_DATA = 1;
	public static final int ITEM_USAGE_DATA = 2;
	public static final int FULL_TERRITORY_SYNC = 3;
	public static final int ADD_TERRITORY_BLOCK = 4;
	public static final int REMOVE_TERRITORY_BLOCK = 5;
	public static final int SYNC_TEAMS = 6;
	public static final int TE_PARTICLE = 7;
	public static final int TMACHINE_STATE = 8;

	public static final int GUILD_JOIN = 20;
	public static final int GUILD_LEAVE = 21;
	public static final int GUILD_CREATE = 22;
	public static final int GUILD_DISBAND = 23;
	public static final int GUILD_UPDATE = 24;
	public static final int PLAYER_INVITE = 25;
	public static final int PLAYER_DECLINE = 26;
	public static final int PLAYER_KICK = 27;
	public static final int PLAYER_PROMOTE = 28;
	public static final int PLAYER_DEMOTE = 29;
	public static final int GUILD_INFO = 30;
	
	public static final int LEVEL_UP = 31;
	public static final int SELECT_CLASS = 32;
	public static final int PERK_INFO = 33;
	
	public ClientPacketHandler(DataVisualization gui, TerritoryRender render) {
		this.gui = gui;
		this.render = render;
	}
	
	@SubscribeEvent
	public void handlePacket(ClientCustomPacketEvent event) {	
		BlockCoord bc;
		TileEntity te;
		channelName = event.packet.channel();
		Minecraft mc = Minecraft.getMinecraft();
		
		if (channelName.equals(Resources.MOD_ID) && event.packet.getTarget() == Side.CLIENT) {
			PacketCustom packet = new PacketCustom(event.packet.payload());
		
			String status;
			int color;
			switch(packet.getType()) {
				case OCCUPATIONAL_DATA:
					for(int i=0; i<Data.occupationNames.length; i++)
						ExtendedPlayer.get(mc.thePlayer).setOccupation(i, packet.readInt());
					break;
					
				case ITEM_USAGE_DATA:
					ExtendedPlayer.get(mc.thePlayer).usedOwnItems = packet.readInt();
					ExtendedPlayer.get(mc.thePlayer).usedOthersItems = packet.readInt();
					ExtendedPlayer.get(mc.thePlayer).usedByOthers = packet.readInt();
					break;
					
				case FULL_TERRITORY_SYNC:
					int numBlocks = packet.readInt();
					render.teamBlocks.clear();
					for(int i=0; i<numBlocks; i++) {
						bc = packet.readCoord();
						render.teamBlocks.put(new Point(bc.x, bc.z), packet.readString());
					}
					render.regenerateEdges();
				break;
					
				case ADD_TERRITORY_BLOCK:
					bc = packet.readCoord();
					render.teamBlocks.put(new Point(bc.x, bc.z), packet.readString());
					render.regenerateEdges();
				break;
				
				case REMOVE_TERRITORY_BLOCK:
					bc = packet.readCoord();
					render.teamBlocks.remove(new Point(bc.x, bc.z));
					render.regenerateEdges();
				break;
					
				case SYNC_TEAMS:
					int numTeamsBefore = render.teamData.size();
					render.teamData.clear();
					int numTeams = packet.readInt();
					for(int i=0; i<numTeams; i++)
						render.teamData.add(new PlayerTeam(packet.readString(), packet.readString(),packet.readInt()));
				
					if (render.teamData.size() < numTeamsBefore) {
						//Some team got disbanded. Need to remove stale territory blocks.
						ArrayList<Point> terrToRemove = new ArrayList<Point>();
						for(Point p : render.teamBlocks.keySet()) {
							String terrID = render.teamBlocks.get(p);
							if (PlayerTeam.get(render.teamData, terrID) == null)
								terrToRemove.add(p);
						}
						for (Point p : terrToRemove)
							render.teamBlocks.remove(p);
						render.regenerateEdges();
					}
				break;
				
				case TE_PARTICLE:
					te = mc.theWorld.getTileEntity(packet.readInt(), packet.readInt(), packet.readInt());
					if (te instanceof TerritoryMachineTE) {
						TerritoryMachineTE machine = (TerritoryMachineTE)te;
						machine.displayParticle = packet.readString();
					}
				break;
				
				case TMACHINE_STATE:
					te = mc.theWorld.getTileEntity(packet.readInt(), packet.readInt(), packet.readInt());
					if (te instanceof TerritoryMachineTE) {
						TerritoryMachineTE machine = (TerritoryMachineTE)te;
						machine.tickTimer = packet.readInt();
						String teamID = packet.readString();
						if (teamID.equals(""))
							teamID = null;
						machine.teamID = teamID;
						machine.shouldClaim = packet.readBoolean();
						machine.refreshColor();
					}
				break;
				
				case GUILD_JOIN:
					status = packet.readString();
					color = packet.readInt();
					if(status.equals("Joined Team")){
						ExtendedPlayer.get(mc.thePlayer).guild = GuiGuild.TEAM_PLAYER;
						mc.displayGuiScreen(new GuiGuild());
					}else if(mc.currentScreen instanceof GuiGuild){
						GuildBase.statusInfo = status;
						GuildBase.statusColor = new Color(color);
					}
				break;
				
				case GUILD_LEAVE:
					ExtendedPlayer.get(mc.thePlayer).guild = GuiGuild.LONER;
					if(mc.currentScreen instanceof GuiGuild)
						mc.displayGuiScreen(new GuiGuild());
				break;
				
				case GUILD_CREATE:
					status = packet.readString();
					color = packet.readInt();
					if(status.equals("Team Successfully Created")){
						ExtendedPlayer.get(mc.thePlayer).guild = GuiGuild.LEADER;
						mc.displayGuiScreen(new GuiGuild());
					}else if(mc.currentScreen instanceof GuiGuild){
						GuildBase.statusInfo = status;
						GuildBase.statusColor = new Color(color);
					}
				break;
				
				case GUILD_DISBAND:
					ExtendedPlayer.get(mc.thePlayer).guild = GuiGuild.LONER;
					if(mc.currentScreen instanceof GuiGuild)
						mc.displayGuiScreen(new GuiGuild());
				break;
				
				case GUILD_UPDATE:
					status = packet.readString();
					color = packet.readInt();
					if(status.equals("Changed Saved")){
						if(mc.currentScreen instanceof GuiGuild){
							GuildLeader.teamUpdateStatus = status;
							GuildLeader.teamUpdateColor = new Color(color);
						}
					}
				break;
				
				case PLAYER_INVITE:
					
				break;
				
				case PLAYER_DECLINE:
					status = packet.readString();
					color = packet.readInt();
					if(status.equals("Invitation has been removed")){
						if(mc.currentScreen instanceof GuiGuild){
							GuildBase.invites.remove(GuildBase.currentCol);
							GuildBase.currentCol = (GuildBase.currentCol < GuildBase.invites.size()) ? GuildBase.currentCol++ : 0;
						}
					}else if(mc.currentScreen instanceof GuiGuild){
						GuildBase.statusInfo = status;
						GuildBase.statusColor = new Color(color);
					}
				break;
				
				case PLAYER_KICK:
					ExtendedPlayer.get(mc.thePlayer).guild = GuiGuild.LONER;
					if(mc.currentScreen instanceof GuiGuild)
						mc.displayGuiScreen(new GuiGuild());
				break;
				
				case PLAYER_PROMOTE:
					
				break;
				
				case PLAYER_DEMOTE:
					status = packet.readString();
					color = packet.readInt();
					if(status.equals("Successfully Demoted")){
						ExtendedPlayer.get(mc.thePlayer).guild = GuiGuild.TEAM_PLAYER;
						if(mc.currentScreen instanceof GuiGuild)
							mc.displayGuiScreen(new GuiGuild());
					}
				break;
				
				case GUILD_INFO:
					ExtendedPlayer.get(mc.thePlayer).guild = packet.readInt();
				break;
					
				case LEVEL_UP:
					ExtendedPlayer.get(mc.thePlayer).occupationLevel = packet.readInt();
					ExtendedPlayer.get(mc.thePlayer).addPerk(packet.readInt());
					if(mc.currentScreen instanceof ClassPerks)
						mc.displayGuiScreen(new ClassPerks());
				break;
					
				case SELECT_CLASS:
					ExtendedPlayer.get(mc.thePlayer).occupationClass = packet.readInt();
					if(mc.currentScreen instanceof ClassPerks)
						mc.displayGuiScreen(new ClassPerks());
				break;
				
				case PERK_INFO:
					int perkID;
					for(int i = 0; i < 5; i++)
						if((perkID = packet.readInt()) != -1)
							ExtendedPlayer.get(mc.thePlayer).addPerk(perkID);
					ExtendedPlayer.get(mc.thePlayer).occupationClass = packet.readInt();
					ExtendedPlayer.get(mc.thePlayer).occupationLevel = packet.readInt();
				break;
			}
		}
	}
	
	/**
	 * ====================================
	 *   Packet Sending Util
	 * ====================================
	 */
	public static PacketCustom packetSyncTerritories(HashMap<Point, String> territoryData) {
		PacketCustom territoryPacket = new PacketCustom(Resources.MOD_ID, FULL_TERRITORY_SYNC);
		territoryPacket.writeInt(territoryData.size());
		for(Point p : territoryData.keySet()) {
			territoryPacket.writeCoord(p.x, 0, p.y);
			territoryPacket.writeString(territoryData.get(p));
		}
		return territoryPacket;
	}
	
	public static PacketCustom packetAddTerrBlock(String teamID, int x, int z) {
		PacketCustom blockPacket = new PacketCustom(Resources.MOD_ID, ADD_TERRITORY_BLOCK);
		blockPacket.writeCoord(x, 0, z);
		blockPacket.writeString(teamID);
		return blockPacket;
	}
	
	public static PacketCustom packetRemoveTerrBlock(int x, int z) {
		PacketCustom blockPacket = new PacketCustom(Resources.MOD_ID, REMOVE_TERRITORY_BLOCK);
		blockPacket.writeCoord(x, 0, z);
		return blockPacket;
	}
	
	public static PacketCustom packetSyncTeams(ArrayList<PlayerTeam> teams) {
		PacketCustom teamPacket = new PacketCustom(Resources.MOD_ID, SYNC_TEAMS);
		teamPacket.writeInt(teams.size());
		for(PlayerTeam team : teams) {
			teamPacket.writeString(team.id);
			teamPacket.writeString(team.displayName);
			teamPacket.writeInt(team.getColorID());
		}
		return teamPacket;
	}
}

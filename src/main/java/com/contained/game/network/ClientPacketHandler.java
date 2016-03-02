package com.contained.game.network;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;

import net.minecraft.client.Minecraft;
import net.minecraft.network.play.INetHandlerPlayClient;

import com.contained.game.data.Data;
import com.contained.game.entity.ExtendedPlayer;
import com.contained.game.ui.DataVisualization;
import com.contained.game.ui.TerritoryRender;
import com.contained.game.user.PlayerTeam;
import com.contained.game.util.Resources;

import codechicken.lib.packet.PacketCustom;
import codechicken.lib.packet.PacketCustom.IClientPacketHandler;
import codechicken.lib.vec.BlockCoord;

public class ClientPacketHandler implements IClientPacketHandler{
	private DataVisualization gui;
	private TerritoryRender render;
	
	public static final int OCCUPATIONAL_DATA = 1;
	public static final int ITEM_USAGE_DATA = 2;
	public static final int FULL_TERRITORY_SYNC = 3;
	public static final int ADD_TERRITORY_BLOCK = 4;
	public static final int REMOVE_TERRITORY_BLOCK = 5;
	public static final int SYNC_TEAMS = 6;
	
	public ClientPacketHandler(DataVisualization gui, TerritoryRender render) {
		this.gui = gui;
		this.render = render;
	}
	
	@Override
	public void handlePacket(PacketCustom packet, Minecraft mc, INetHandlerPlayClient net) {
		BlockCoord bc;
		
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
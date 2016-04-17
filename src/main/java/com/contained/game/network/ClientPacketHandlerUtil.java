package com.contained.game.network;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import codechicken.lib.packet.PacketCustom;
import codechicken.lib.vec.BlockCoord;

import com.contained.game.Contained;
import com.contained.game.user.PlayerTeam;
import com.contained.game.user.PlayerTeamIndividual;
import com.contained.game.user.PlayerTeamInvitation;
import com.contained.game.user.PlayerTrade;
import com.contained.game.util.Resources;
import com.contained.game.util.Util;

public class ClientPacketHandlerUtil {
	public static final int OCCUPATIONAL_DATA = 1;
	public static final int ITEM_USAGE_DATA = 2;
	public static final int FULL_TERRITORY_SYNC = 3;
	public static final int ADD_TERRITORY_BLOCK = 4;
	public static final int REMOVE_TERRITORY_BLOCK = 5;
	public static final int SYNC_TEAMS = 6;
	public static final int TE_PARTICLE = 7;
	public static final int TMACHINE_STATE = 8;
	
	public static final int GUILD_JOIN = 9;
	public static final int GUILD_LEAVE = 10;
	public static final int GUILD_CREATE = 11;
	public static final int GUILD_DISBAND = 12;
	public static final int GUILD_UPDATE = 13;
	public static final int PLAYER_INVITE = 14;
	public static final int PLAYER_DECLINE = 15;
	public static final int PLAYER_KICK = 16;
	public static final int PLAYER_PROMOTE = 17;
	public static final int PLAYER_DEMOTE = 18;
	
	public static final int LEVEL_UP = 20;
	public static final int SELECT_CLASS = 21;
	public static final int PERK_INFO = 22;
	
	public static final int UPDATE_PERMISSIONS = 23;
	public static final int SYNC_LOCAL_PLAYER = 24;
	
	public static final int ADD_ITEM = 25;
	public static final int REMOVE_ITEM = 26;
	public static final int CREATE_TRADE = 27;
	public static final int REMOVE_TRADE = 28;
	public static final int TRADE_TRANS = 29;
	public static final int SYNC_TRADE = 30;
	
	public static final int PLAYER_ADMIN = 31;
	public static final int NEW_PLAYER = 32;
	public static final int UPDATE_PLAYER = 33;
	public static final int PLAYER_LIST = 34;
	public static final int SYNC_INVITATIONS = 35;
	
	public static final int END_GAME = 36;
	public static final int RESURRECT = 37;
	public static final int SYNC_LIVES = 38;
	public static final int PLAYER_SPECTATOR = 39;
	public static final int PLAYER_NORMAL = 40;
	
	public static final int MINIGAME_TIMER_SYNC = 41;
	public static final int JOIN_MINI_GAME = 42;
	public static final int CANCEL_JOIN_MINI_GAME = 43;
	public static final int MINIGAME_STARTED = 44;
	public static final int MINIGAME_ENDED = 45;
	public static final int SYNC_PVP_STATS = 46;
	public static final int SYNC_TEASURE_STATS = 47;
	public static final int SYNC_GAME_SCORE = 48;
	
	public static final int ADD_TREASURE_POINTS = 49;
	public static final int REMOVE_TREASURE_POINTS = 50;
	
	public static final int SAVE_PLAYER = 51;
	public static final int RESTORE_PLAYER = 52;
	
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
			NBTTagCompound ntc = new NBTTagCompound();
			team.writeToNBT(ntc);
			teamPacket.writeNBTTagCompound(ntc);
		}
		return teamPacket;
	}
	
	public static PacketCustom packetUpdatePermissions(PlayerTeam toSync) {
		PacketCustom permPacket = new PacketCustom(Resources.MOD_ID, UPDATE_PERMISSIONS);
		NBTTagCompound teamData = new NBTTagCompound();
		toSync.writeToNBT(teamData);
		permPacket.writeNBTTagCompound(teamData);
		return permPacket;
	}

	public static PacketCustom packetSyncLocalPlayer(EntityPlayer joined) {
		PacketCustom pdataPacket = new PacketCustom(Resources.MOD_ID, SYNC_LOCAL_PLAYER);
		NBTTagCompound ntc = new NBTTagCompound();
		PlayerTeamIndividual pdata = PlayerTeamIndividual.get(joined);
		pdata.writeToNBT(ntc);
		pdataPacket.writeNBTTagCompound(ntc);
		return pdataPacket;
	}
	
	public static PacketCustom packetNewPlayer(String name) {
		PacketCustom packet = new PacketCustom(Resources.MOD_ID, NEW_PLAYER);
		packet.writeString(name);
		return packet;
	}
	
	public static PacketCustom packetUpdatePlayer(PlayerTeamIndividual player) {
		PacketCustom packet = new PacketCustom(Resources.MOD_ID, UPDATE_PLAYER);
		packet.writeString(player.playerName);
		if (player.teamID == null)
			packet.writeString("");
		else
			packet.writeString(player.teamID);
		return packet;
	}
	
	public static PacketCustom packetPlayerList(ArrayList<PlayerTeamIndividual> allPlayers) {
		PacketCustom packet = new PacketCustom(Resources.MOD_ID, PLAYER_LIST);
		packet.writeInt(allPlayers.size());
		for(PlayerTeamIndividual pdata : allPlayers) {
			packet.writeString(pdata.playerName);
			if (pdata.teamID == null)
				packet.writeString("");
			else
				packet.writeString(pdata.teamID);
		}
		return packet;
	}
	
	public static PacketCustom packetSyncRelevantInvites(EntityPlayer player) {
		PlayerTeamIndividual pdata = PlayerTeamIndividual.get(player);
		ArrayList<PlayerTeamInvitation> invitesToSync = new ArrayList<PlayerTeamInvitation>();
		for(PlayerTeamInvitation invite : Contained.teamInvitations) {
			if (invite.playerName.equals(pdata.playerName)
					|| (pdata.isLeader && invite.teamID != null && invite.teamID.equals(pdata.teamID))) {
				invitesToSync.add(invite);
			}
		}
		
		PacketCustom packet = new PacketCustom(Resources.MOD_ID, SYNC_INVITATIONS);
		packet.writeInt(invitesToSync.size());
		for(PlayerTeamInvitation invite : invitesToSync) {
			NBTTagCompound ntc = new NBTTagCompound();
			invite.writeToNBT(ntc);
			packet.writeNBTTagCompound(ntc);
		}
		return packet;
	}
	
	public static PacketCustom packetSyncTrades(PlayerTrade trade, boolean type) {
		PacketCustom teamPacket;
		if(type){ //Create New Trade
			teamPacket = new PacketCustom(Resources.MOD_ID, CREATE_TRADE);
			NBTTagCompound tradeData = new NBTTagCompound();
			trade.writeToNBT(tradeData);
			teamPacket.writeNBTTagCompound(tradeData);
			return teamPacket;
		}
		
		// Remove Existing Trade
		teamPacket = new PacketCustom(Resources.MOD_ID, REMOVE_TRADE);
		teamPacket.writeString(trade.id);
		return teamPacket;
	}
	
	public static PacketCustom packetSyncTrades(ArrayList<PlayerTrade> trades){
		PacketCustom tradePacket = new PacketCustom(Resources.MOD_ID, SYNC_TRADE);
		tradePacket.writeInt(trades.size());
		for(PlayerTrade trade : trades){
			NBTTagCompound ntc = new NBTTagCompound();
			trade.writeToNBT(ntc);
			tradePacket.writeNBTTagCompound(ntc);
		}
		return tradePacket;
	}
	
	public static void syncTeamMembershipChangeToAll(PlayerTeamIndividual memberChanged) {
		EntityPlayer playerServerEnt = Util.getOnlinePlayer(memberChanged.playerName);
		if (playerServerEnt != null)
			Contained.channel.sendTo(ClientPacketHandlerUtil.packetSyncLocalPlayer(playerServerEnt).toPacket(), (EntityPlayerMP)playerServerEnt);
		Contained.channel.sendToAll(ClientPacketHandlerUtil.packetUpdatePlayer(memberChanged).toPacket());
	}
	
	public static void syncMinigameTime(int dimID) {
		PacketCustom timePacket = new PacketCustom(Resources.MOD_ID, MINIGAME_TIMER_SYNC);
		timePacket.writeInt(Contained.timeLeft[dimID]);
		Contained.channel.sendToDimension(timePacket.toPacket(), dimID);
	}
	
	public static void syncMiniGameScore(int dimID, int teamID, int score){
		for(PlayerTeamIndividual pdata : Contained.teamMemberData)
			if(pdata.teamID.equals(Contained.getTeamList(dimID).get(teamID).displayName)){
				EntityPlayerMP miniGamePlayer = (EntityPlayerMP) MinecraftServer.getServer().worldServers[dimID].getPlayerEntityByName(pdata.playerName);
				PacketCustom syncGameScorePacket = new PacketCustom(Resources.MOD_ID, ClientPacketHandlerUtil.SYNC_GAME_SCORE);
				syncGameScorePacket.writeInt(dimID);
				syncGameScorePacket.writeInt(teamID);
				syncGameScorePacket.writeInt(score);
				Contained.channel.sendTo(syncGameScorePacket.toPacket(), miniGamePlayer);
			} 	
	}
	
	public static void addTreasureAndSync(int dimID, ArrayList<BlockCoord> points) {
		ArrayList<BlockCoord> activeSoFar = Contained.getActiveTreasures(dimID);
		for (BlockCoord p : points)
			activeSoFar.add(p);
		
		PacketCustom treasurePacket = packetAddTreasures(points, false);
		Contained.channel.sendToDimension(treasurePacket.toPacket(), dimID);
	}
	
	public static void removeTreasureAndSync(int dimID, BlockCoord point) {
		ArrayList<BlockCoord> p = new ArrayList<BlockCoord>();
		p.add(point);
		removeTreasureAndSync(dimID, p);
	}
	
	public static void removeTreasureAndSync(int dimID, ArrayList<BlockCoord> points) {
		ArrayList<BlockCoord> activeSoFar = Contained.getActiveTreasures(dimID);
		for (BlockCoord p : points)
			activeSoFar.remove(p);
		
		PacketCustom treasurePacket = new PacketCustom(Resources.MOD_ID, REMOVE_TREASURE_POINTS);
		treasurePacket.writeInt(points.size());
		for (BlockCoord p : points)
			treasurePacket.writeCoord(p);
		Contained.channel.sendToDimension(treasurePacket.toPacket(), dimID);		
	}
	
	/**
	 * @param reset Should this append to (reset=false) or overwrite (reset=true)
	 * 				the previous chest data on the client side?
	 */
	public static PacketCustom packetAddTreasures(ArrayList<BlockCoord> points, boolean reset) {
		PacketCustom treasurePacket = new PacketCustom(Resources.MOD_ID, ADD_TREASURE_POINTS);
		treasurePacket.writeInt(points.size());
		treasurePacket.writeBoolean(reset);
		for (BlockCoord p : points)
			treasurePacket.writeCoord(p);
		return treasurePacket;
	}
}

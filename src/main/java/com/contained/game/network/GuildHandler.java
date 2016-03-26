package com.contained.game.network;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.WorldServer;
import codechicken.lib.packet.PacketCustom;

import com.contained.game.Contained;
import com.contained.game.data.DataLogger;
import com.contained.game.user.PlayerTeam;
import com.contained.game.user.PlayerTeamIndividual;
import com.contained.game.user.PlayerTeamInvitation;
import com.contained.game.util.ErrorCase;
import com.contained.game.util.Resources;
import com.contained.game.util.Util;

public class GuildHandler {
	public GuildHandler(){}
	
	public void joinTeam(EntityPlayerMP player, String team){
		String statusInfo = "Joined Team";
		Color statusColor = Color.GREEN;
		PlayerTeamIndividual pdata = PlayerTeamIndividual.get(player);
		ArrayList<PlayerTeamInvitation> myInvites = PlayerTeamInvitation.getInvitations(pdata);
			
		PlayerTeam matchedTeam = null;
		PlayerTeamIndividual matchedPlayer = null;
			
		String inviteName = team.toLowerCase();
		for(PlayerTeamInvitation inv : myInvites) {
			PlayerTeam teamData = PlayerTeam.get(inv.teamID);
			if (teamData != null && ((pdata.isLeader && inv.playerName.toLowerCase().equals(inviteName))
				|| (!pdata.isLeader && teamData.displayName.toLowerCase().equals(inviteName)))) 
			{
				matchedTeam = teamData;
				matchedPlayer = PlayerTeamIndividual.get(inv.playerName);
				break;
			}
		}
		
		//Try to join the player to the team.
		ErrorCase.Error result = matchedPlayer.joinTeam(matchedTeam.id);
		if (result == ErrorCase.Error.NOT_EXISTS){
			statusInfo = "Team No Longer Exists";
			statusColor = Color.RED;
		}else if (result == ErrorCase.Error.TEAM_FULL){
			statusInfo = "Team Is Already Full";
			statusColor = Color.RED;
		} else if (result == ErrorCase.Error.NONE) {
			matchedTeam.sendMessageToTeam(matchedTeam.getFormatCode()
					+ "[NOTICE] §l"+ matchedPlayer.playerName + "§r"
					+ matchedTeam.getFormatCode() + " has joined the team!");
					
			//Accept successful, remove invitation.
			ArrayList<PlayerTeamInvitation> toRemove = new ArrayList<PlayerTeamInvitation>();
			toRemove.add(new PlayerTeamInvitation(matchedPlayer.playerName, matchedTeam.id, PlayerTeamInvitation.NEITHER));
			Contained.teamInvitations.removeAll(toRemove);
				
			statusInfo = "Joined Team";
			statusColor = Color.GREEN;
			
			String world = player.dimension == 0 ? "Normal" : "Nether";
			DataLogger.insertJoinTeam("debugmode", player.getDisplayName(), world, team, Util.getDate());
		}
		
		PacketCustom guildPacket = new PacketCustom(Resources.MOD_ID, ClientPacketHandlerUtil.GUILD_JOIN);
		guildPacket.writeString(statusInfo);
		guildPacket.writeInt(statusColor.hashCode());
		Contained.channel.sendTo(guildPacket.toPacket(), player);
		Contained.channel.sendTo(ClientPacketHandlerUtil.packetSyncRelevantInvites(player).toPacket(), player);
	}
	
	public void leaveTeam(EntityPlayerMP player){
		PlayerTeamIndividual pdata = PlayerTeamIndividual.get(player);
		PlayerTeam team = PlayerTeam.get(pdata.teamID);
		
		pdata.leaveTeam();
		team.sendMessageToTeam(team.getFormatCode()+"[NOTICE] "+pdata.playerName+" has left the team.");
		
		PacketCustom guildPacket = new PacketCustom(Resources.MOD_ID, ClientPacketHandlerUtil.GUILD_LEAVE);
		Contained.channel.sendTo(guildPacket.toPacket(), player);
	}
	
	public void kickPlayer(EntityPlayerMP player, String teammate){
		PlayerTeamIndividual pdata = PlayerTeamIndividual.get(player);
		PlayerTeam team = PlayerTeam.get(pdata.teamID);
		
		PlayerTeamIndividual toKick = PlayerTeamIndividual.get(teammate);
		team.sendMessageToTeam(team.getFormatCode()+"[NOTICE] "+toKick.playerName+" has been kicked from the team.");
		toKick.leaveTeam();
		
		PacketCustom guildPacket = new PacketCustom(Resources.MOD_ID, ClientPacketHandlerUtil.PLAYER_KICK);
		Contained.channel.sendTo(guildPacket.toPacket(), player);
		
		String world = player.dimension == 0 ? "Normal" : "Nether";
		DataLogger.insertKickPlayer("debugmode", player.getDisplayName(), world, toKick.playerName, team.displayName, Util.getDate());
	}
	
	public void promotePlayer(EntityPlayerMP player, String teammate){
		PlayerTeamIndividual pdata = PlayerTeamIndividual.get(player);
		PlayerTeam team = PlayerTeam.get(pdata.teamID);
		
		PlayerTeamIndividual toPromote = PlayerTeamIndividual.get(teammate);
		toPromote.promote();
		
		String world = player.dimension == 0 ? "Normal" : "Nether";
		DataLogger.insertPromoteTeamPlayer("debugmode", player.getDisplayName(), world, team.displayName, teammate, Util.getDate());
	}
	
	public void demotePlayer(EntityPlayerMP player){
		String statusInfo = "Successfully Demoted";
		Color statusColor = Color.GREEN;
		
		PlayerTeamIndividual pdata = PlayerTeamIndividual.get(player);
		PlayerTeam team = PlayerTeam.get(pdata.teamID);
		
		ErrorCase.Error result = pdata.demote();
		if(result == ErrorCase.Error.CANNOT_DEMOTE){
			System.out.println("You're the only member of this group... you must stay the leader."); //Render This
			statusInfo = "Failed To Demote, Need More Players";
			statusColor = Color.RED;
		}else{
			team.sendMessageToTeam(team.getFormatCode()+"[NOTICE] "+pdata.playerName+" is no longer a team leader.");
			
			String world = player.dimension == 0 ? "Normal" : "Nether";
			DataLogger.insertPromoteTeamPlayer("debugmode", player.getDisplayName(), world, team.displayName, player.getDisplayName(), Util.getDate());
		}
		
		PacketCustom guildPacket = new PacketCustom(Resources.MOD_ID, ClientPacketHandlerUtil.PLAYER_DEMOTE);
		guildPacket.writeString(statusInfo);
		guildPacket.writeInt(statusColor.hashCode());
		Contained.channel.sendTo(guildPacket.toPacket(), player);
	}
	
	public void createTeam(EntityPlayerMP player, String team, int color){
		String statusInfo = "Team Successfully Created";
		Color statusColor = Color.GREEN;
		PlayerTeamIndividual pdata = PlayerTeamIndividual.get(player);

		boolean allowedName = true;
		for(PlayerTeam t : Contained.teamData) {
			if (t.displayName.toLowerCase().equals(team.toLowerCase())) {
				allowedName = false;
				break;
			}
		}
		
		PlayerTeam newTeam = new PlayerTeam(team, color);
		if (allowedName) {
			Contained.teamData.add(newTeam);
			System.out.println(pdata.joinTeam(newTeam.id, true).toString());
			ClientPacketHandlerUtil.packetSyncTeams(Contained.teamData).sendToClients();
			
			String world = player.dimension == 0 ? "Normal" : "Nether";
			DataLogger.insertCreateTeam("debugMode", pdata.playerName, world, newTeam.displayName, Util.getDate());
		} else {
			statusInfo = "Team Name Already In-Use";
			statusColor = Color.RED;
		}
		
		PacketCustom guildPacket = new PacketCustom(Resources.MOD_ID, ClientPacketHandlerUtil.GUILD_CREATE);
		guildPacket.writeString(statusInfo);
		guildPacket.writeInt(statusColor.hashCode());
		Contained.channel.sendTo(guildPacket.toPacket(), player);
		Contained.channel.sendTo(ClientPacketHandlerUtil.packetSyncRelevantInvites(player).toPacket(), player);
	}
	
	public void disbandTeam(EntityPlayerMP leader, String id){
		PlayerTeam team = PlayerTeam.get(id);
		
		ArrayList<PlayerTeamIndividual> players = Contained.teamMemberData;
		for(PlayerTeamIndividual player : players){
			if(player.teamID.equals(id)){
				WorldServer[] servers = MinecraftServer.getServer().worldServers;
				for(WorldServer server : servers){
					ListIterator iterator = server.playerEntities.listIterator();
					while(iterator.hasNext()){
						EntityPlayerMP playerMP = (EntityPlayerMP) iterator.next();
						if(player.playerName.equals(playerMP.getDisplayName())){
							PacketCustom guildPacket = new PacketCustom(Resources.MOD_ID, ClientPacketHandlerUtil.GUILD_DISBAND);
							Contained.channel.sendTo(guildPacket.toPacket(), playerMP);
						}
					}
				}
			}
		}
		
		team.disbandTeam();
		
		String world = leader.dimension == 0 ? "Normal" : "Nether";
		DataLogger.insertDisbandTeam("debugmod", leader.getDisplayName(), world, team.displayName, Util.getDate());
	}
	
	public void updateTeam(EntityPlayerMP player, String name, int color){
		String statusInfo = "Changed Saved";
		Color statusColor = Color.GREEN;
		boolean update = true;
		PlayerTeamIndividual pdata = PlayerTeamIndividual.get(player);
		PlayerTeam team = PlayerTeam.get(pdata.teamID);
		
		for(PlayerTeam t : Contained.teamData) {
			if (t.displayName.toLowerCase().equals(name.toLowerCase())) {
				update = false;
				break;
			}
		}
		
		if(update){
			team.displayName = name;
			team.colorID = color;
			team.sendMessageToTeam(team.getFormatCode() + "[NOTICE] Team Update: " + team.getFormatCode() + "§l" + team.displayName + team.getFormatCode() +".");
			Contained.channel.sendToAll(ClientPacketHandlerUtil.packetSyncTeams(Contained.teamData).toPacket());
		}else{
			statusInfo = "Team Name Taken";
			statusColor = Color.RED;
		}
		
		PacketCustom guildPacket = new PacketCustom(Resources.MOD_ID, ClientPacketHandlerUtil.GUILD_UPDATE);
		guildPacket.writeString(statusInfo);
		guildPacket.writeInt(statusColor.hashCode());
		Contained.channel.sendTo(guildPacket.toPacket(), player);
	}
	
	public void invitePlayer(EntityPlayerMP player, String newTeammate){
		PlayerTeamIndividual leader = PlayerTeamIndividual.get(player);
		PlayerTeam team = PlayerTeam.get(leader.teamID);
		
		PlayerTeamIndividual recipient = PlayerTeamIndividual.get(newTeammate);
		PlayerTeamInvitation newInvite = new PlayerTeamInvitation(recipient.playerName, team.id, PlayerTeamInvitation.FROM);
		
		if (Contained.teamInvitations.indexOf(newInvite) == -1) {
			Contained.teamInvitations.add(newInvite);

			//Send message to invited player to let them know they got an invitation.
			@SuppressWarnings("rawtypes")
			List onlinePlayers = MinecraftServer.getServer().getConfigurationManager().playerEntityList;
			for (Object o : onlinePlayers) {
				if (o instanceof EntityPlayerMP) {
					EntityPlayerMP onlinePlayer = (EntityPlayerMP)o;
					PlayerTeamIndividual onlineData = PlayerTeamIndividual.get(onlinePlayer);
					if (onlinePlayer.getDisplayName().toLowerCase().equals(newInvite.playerName.toLowerCase())) {
						PlayerTeam teamData = PlayerTeam.get(newInvite.teamID);
						onlinePlayer.addChatComponentMessage(new ChatComponentText("[*] "+teamData.getFormatCode()+"§l"+teamData.displayName+"§r would like you to join their group."));
						Contained.channel.sendTo(ClientPacketHandlerUtil.packetSyncRelevantInvites(onlinePlayer).toPacket(), onlinePlayer);
					}
				}
			}		
		}
		
		String world = player.dimension == 0 ? "Normal" : "Nether";
		DataLogger.insertInvitePlayer("debugmode", player.getDisplayName(), world, newTeammate, team.displayName, Util.getDate());
	}
	
	public void declineInvite(EntityPlayerMP player, String team){
		String statusInfo = "Invitation has been removed";
		Color statusColor = Color.GREEN;
		PlayerTeamIndividual pdata = PlayerTeamIndividual.get(player);
		ArrayList<PlayerTeamInvitation> myInvites
						= PlayerTeamInvitation.getInvitations(pdata);
		
		String inviteName = team;
		PlayerTeamInvitation probe = null;
		if (pdata.isLeader)
			probe = new PlayerTeamInvitation(inviteName, "", PlayerTeamInvitation.TO);
		else {
			for (PlayerTeam t : Contained.teamData) {
				if (t.displayName.toLowerCase().equals(inviteName)) {
					probe = new PlayerTeamInvitation("", t.id, PlayerTeamInvitation.FROM);
					break;
				}
			}
		}
		
		//Try to remove the invitation(s) from the invitations list.
		ArrayList<PlayerTeamInvitation> toRemove = new ArrayList<PlayerTeamInvitation>();
		toRemove.add(probe);
		int beforeSize = Contained.teamInvitations.size();
		Contained.teamInvitations.removeAll(toRemove);
		int afterSize = Contained.teamInvitations.size();
		
		if (beforeSize == afterSize){
			statusInfo = "Could Not Remove Invite";
			statusColor = Color.RED;
		}
	
		PacketCustom guildPacket = new PacketCustom(Resources.MOD_ID, ClientPacketHandlerUtil.PLAYER_DECLINE);
		guildPacket.writeString(statusInfo);
		guildPacket.writeInt(statusColor.hashCode());
		Contained.channel.sendTo(guildPacket.toPacket(), player);
		Contained.channel.sendTo(ClientPacketHandlerUtil.packetSyncRelevantInvites(player).toPacket(), player);
	}
}

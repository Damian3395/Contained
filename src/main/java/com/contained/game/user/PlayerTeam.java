package com.contained.game.user;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.contained.game.Contained;
import com.contained.game.network.ClientPacketHandler;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;

/**
 * Represents a group of players which share a common territory.
 */
public class PlayerTeam {

	public String id;
	public String displayName;
	private int colorID; //Index for format codes below
	
	/*
	 *  http://minecraft.gamepedia.com/Formatting_codes
	 */
	public static final String[] formatCodes = {
		"§0", "§1", "§2", "§3", "§4", "§5", "§6", "§7", "§8", "§9", 
		"§a", "§b", "§c", "§d", "§e", "§f"
	};
	public static final int[] formatColors = {
		0x000000, 
		0x0000AA,
		0x00AA00,
		0x00AAAA,
		0xAA0000,
		0xAA00AA,
		0xFFAA00,
		0xAAAAAA,
		0x555555,
		0x5555FF,
		0x55FF55,
		0x55FFFF,
		0xFF5555,
		0xFF55FF,
		0xFFFF55,
		0xFFFFFF
	};
	
	public PlayerTeam() {
		this(UUID.randomUUID().toString());
	}
	
	public PlayerTeam(String id) {
		this(id, "", 0);
	}
	
	public PlayerTeam(String name, int color) {
		this(UUID.randomUUID().toString(), name, color);
	}
	
	public PlayerTeam(String id, String name, int color) {
		this.id = id;
		this.displayName = name;
		setColor(color);
	}
	
	public PlayerTeam(NBTTagCompound ntc) {
		this.readFromNBT(ntc);
	}
	
	/**
	 * Removes the team, and all of its associated data (territory, players, etc)
	 */
	public void disbandTeam() {
		MinecraftServer.getServer().getCommandManager().executeCommand(MinecraftServer.getServer(), "say The team, "+displayName+", has been disbanded.");
		
		//For all players in this team, have them leave the team.
		for(PlayerTeamIndividual player : Contained.teamMemberData) {
			if (player.teamID != null && player.teamID.equals(this.id))
				player.leaveTeam();
		}
		
		//Remove team's territory.
		ArrayList<Point> territoryToRemove = new ArrayList<Point>();
		for(Point p : Contained.territoryData.keySet()) {
			if (Contained.territoryData.get(p).equals(this.id))
				territoryToRemove.add(p);
		}
		for (Point p : territoryToRemove) //Second pass, to avoid concurrent modification.
			Contained.territoryData.remove(p); 
		
		//Remove any pending invitations involving this team.
		ArrayList<PlayerTeamInvitation> invitationsToRemove = new ArrayList<PlayerTeamInvitation>();
		for(PlayerTeamInvitation invite : Contained.teamInvitations) {
			if (invite.teamID != null && invite.teamID.equals(this.id))
				invitationsToRemove.add(invite);
		}
		for (PlayerTeamInvitation invite : invitationsToRemove)
			Contained.teamInvitations.remove(invite);
		
		//Remove the team.
		Contained.teamData.remove(this);	
		
		ClientPacketHandler.packetSyncTeams(Contained.teamData).sendToClients();
	}
	
	/**
	 * Gets all of the players from this team that are currently online.
	 */
	public ArrayList<EntityPlayer> getOnlinePlayers() {
		ArrayList<EntityPlayer> ret = new ArrayList<EntityPlayer>();
		@SuppressWarnings("rawtypes")
		List onlinePlayers = MinecraftServer.getServer().getConfigurationManager().playerEntityList;
		for (Object o : onlinePlayers) {
			if (o instanceof EntityPlayer) {
				EntityPlayer p = (EntityPlayer)o;
				PlayerTeamIndividual pdata = PlayerTeamIndividual.get(p);
				if (pdata.teamID != null && pdata.teamID.equals(this.id))
					ret.add(p);
			}
		}
		return ret;
	}
	
	/**
	 * Sends a chat message to all players on this team.
	 */
	public void sendMessageToTeam(String msg) {
		ArrayList<EntityPlayer> teamMembers = getOnlinePlayers();
		for(EntityPlayer member : teamMembers)
			member.addChatComponentMessage(new ChatComponentText(msg));
	}
	
	/**
	 * Get the number of players currently in this team.
	 */
	public int numMembers() {
		int members = 0;
		for(PlayerTeamIndividual player : Contained.teamMemberData) {
			if (player.teamID != null && player.teamID.equals(this.id))
				members++;
		}
		return members;
	}
	
	/**
	 * Sets the player's color via a string command.
	 * Accepted commands:
	 * 		<ul>
	 * 		<li>"random": Set this team to a random color.</li>
	 * 		<li>[0-9] or [a-f]: Set to the color associated with the corresponding format code.</li>
	 * 		<li>An integer: Set to the color indexed by this number.</li>
	 * 		</ul> 
	 * @return Whether or not the given command was valid.
	 */
	public boolean setColor(String command) {
		String lcmd = command.toLowerCase();
		if (lcmd.equals("random")) {
			randomColor();
			return true;
		}
		else {
			try {
				int index = Integer.parseInt(lcmd);
				setColor(index);
				return true;
			} catch (NumberFormatException e) {
				for(int i=0; i<formatCodes.length; i++) {
					if (lcmd.contains(String.valueOf(formatCodes[i].charAt(1)))) {
						setColor(i);
						return true;
					}
				}
			}
		}
		return false;
	}
	
	public void setColor(int id) {
		this.colorID = Math.min(PlayerTeam.formatColors.length-1, Math.max(0,id));
	}
	
	public void randomColor() {
		setColor((int)(Math.random()*formatColors.length));
	}
	
	public String getFormatCode() {
		return formatCodes[this.colorID];
	}
	
	public int getColor() {
		return formatColors[this.colorID];
	}
	
	public int getColorID() {
		return this.colorID;
	}
	
	public void writeToNBT(NBTTagCompound ntc) {
		ntc.setString("id", this.id);
		ntc.setInteger("color", this.colorID);
		ntc.setString("name", this.displayName);
	}
	
	public void readFromNBT(NBTTagCompound ntc) {
		this.id = ntc.getString("id");
		this.displayName = ntc.getString("name");
		setColor(ntc.getInteger("color"));
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof PlayerTeam) {
			PlayerTeam pt = (PlayerTeam)o;
			if (pt.id.equals(this.id))
				return true;
		} else if (o instanceof String) {
			String id = (String)o;
			if (id.equals(this.id))
				return true;
		}
		return false;
	}
	
	@Override 
	public int hashCode() {
		return this.id.hashCode();
	}
	
	@Override
	public String toString() {
		return this.id;
	}
	
	public static PlayerTeam get(ArrayList<PlayerTeam> teams, Object comp) {
		for (int i=0; i<teams.size(); i++) {
			if (teams.get(i).equals(comp))
				return teams.get(i);
		}
		return null;
	}
	
	public static PlayerTeam get(Object comp) {
		return PlayerTeam.get(Contained.teamData, comp);
	}
}

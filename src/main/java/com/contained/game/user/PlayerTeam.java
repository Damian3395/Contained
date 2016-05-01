package com.contained.game.user;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import com.contained.game.Contained;
import com.contained.game.network.ClientPacketHandlerUtil;
import com.contained.game.util.MiniGameUtil;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;

/**
 * Represents a group of players which share a common territory.
 */
public class PlayerTeam {

	public String id;
	public String displayName;
	public HashMap<String, PlayerTeamPermission> permissions;
	public int colorID; //Index for format codes below
	public int dimID;
	
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
	public static final int[] rgbColors = {
		Color.BLACK.hashCode(),
		new Color(0,0, 170).hashCode(), //Dark Blue
		new Color(0, 170, 0).hashCode(), //Dark Green
		new Color(0, 170, 170).hashCode(), //Dark Aqua
		new Color(170, 0, 0).hashCode(), //Dark Red
		new Color(170, 0, 170).hashCode(), //Dark Purple
		new Color(255, 170, 0).hashCode(), //Gold
		new Color(170, 170, 170).hashCode(), //Gray
		new Color(85, 85, 85).hashCode(), //Dark Gray
		new Color(85, 85, 255).hashCode(), //Blue
		new Color(85, 255, 85).hashCode(), //Green
		new Color(85, 255, 255).hashCode(), //Aqua
		new Color(255, 85, 85).hashCode(), //Red
		new Color(255, 85, 255).hashCode(), //Light Purple
		new Color(255, 255, 85).hashCode(), //Yellow
		Color.WHITE.hashCode()
	};
	
	public PlayerTeam(int dimID) {
		this(UUID.randomUUID().toString(), dimID);
	}
	
	public PlayerTeam(String id, int dimID) {
		this(id, "", 0, dimID);
	}
	
	public PlayerTeam(String name, int color, int dimID) {
		this(UUID.randomUUID().toString(), name, color, dimID);
	}
	
	public PlayerTeam(String id, String name, int color, int dimID) {
		this.id = id;
		this.dimID = dimID;
		this.displayName = name;
		this.permissions = new HashMap<String, PlayerTeamPermission>();
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
		for (PlayerTeamIndividual player : Contained.teamMemberData) {
			if (player.teamID != null && player.teamID.equals(this.id))
				player.leaveTeam();
		}
		
		//Remove team's territory.
		ArrayList<Point> territoryToRemove = new ArrayList<Point>();
		for (Point p : Contained.getTerritoryMap(dimID).keySet()) {
			if (Contained.getTerritoryMap(dimID).get(p).equals(this.id))
				territoryToRemove.add(p);
		}
		for (Point p : territoryToRemove) //Second pass, to avoid concurrent modification.
			MiniGameUtil.removeTerritoryBlock(p, dimID);
		
		//Remove any pending invitations involving this team.
		ArrayList<PlayerTeamInvitation> invitationsToRemove = new ArrayList<PlayerTeamInvitation>();
		for (PlayerTeamInvitation invite : Contained.teamInvitations) {
			if (invite.teamID != null && invite.teamID.equals(this.id))
				invitationsToRemove.add(invite);
		}
		for (PlayerTeamInvitation invite : invitationsToRemove)
			Contained.teamInvitations.remove(invite);
		
		//Remove any custom permissions involving this team.
		for (PlayerTeam team : Contained.getTeamList(dimID))
			team.permissions.remove(this.id);
		
		//Remove the team.
		Contained.teamData.remove(this);	
		
		Contained.channel.sendToAll(ClientPacketHandlerUtil.packetSyncTeams(Contained.getTeamList(dimID)).toPacket());
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
	
	/*
	 * Gets all of the players from this team both online/offline
	 */
	public List<String> getTeamPlayers(){
		List<String> list = new ArrayList<String>();
		
		for(PlayerTeamIndividual pdata : Contained.teamMemberData){
			if(pdata.teamID != null && pdata.teamID.equals(this.id))
				list.add(pdata.playerName);
		}
		
		return list;
	}
	
	/*
	 * Gets all of the players that have logged into the server
	 */
	public List<String> getPlayersList(){
		List<String> list = new ArrayList<String>();
		
		for(PlayerTeamIndividual pdata : Contained.teamMemberData)
			list.add(pdata.playerName);
		
		return list;
	}
	
	/*
	 * Get all of the players that are not in a team
	 */
	public List<String> getLonerList(String username){
		List<String> list = new ArrayList<String>();
		
		for(PlayerTeamIndividual pdata : Contained.teamMemberData){
			if(pdata.teamID == null)
				list.add(pdata.playerName);
		}
		
		return list;
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
	 * Get the number of blocks of land this team owns.
	 */
	public int territoryCount() {
		int count = 0;
		for(Point territory : Contained.getTerritoryMap(dimID).keySet()) {
			if (Contained.getTerritoryMap(dimID).get(territory).equals(id))
				count++;
		}
		return count;
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
	
	public String formattedName() {
		return getFormatCode()+"§l"+displayName;
	}
	
	public int getColor() {
		return formatColors[this.colorID];
	}
	
	public int getColorID() {
		return this.colorID;
	}
	
	public PlayerTeamPermission getPermissions(String teamID) {
		if (teamID == null)
			return (new PlayerTeamPermission());
		if (teamID.equals(this.id)) {
			//Requested permissions for our own team. Nothing should be disabled.
			PlayerTeamPermission retPerm = new PlayerTeamPermission();
			retPerm.setAllowAll();
			return retPerm;
		}
		else if (!this.permissions.containsKey(teamID)) {
			//We don't have the requested team stored, return default permissions.
			if (teamID.equals(getDefaultPermissionsKey()))
				return (new PlayerTeamPermission());
			else
				return getDefaultPermissions();
		}
		else
			return this.permissions.get(teamID);
	}
	
	public PlayerTeamPermission getDefaultPermissions() {
		return getPermissions(getDefaultPermissionsKey());
	}
	
	public void setDefaultPermissions(PlayerTeamPermission perm) {
		this.permissions.put(getDefaultPermissionsKey(), perm);
	}
	
	public static String getDefaultPermissionsKey() {
		return "default";
	}
	
	public void writeToNBT(NBTTagCompound ntc) {
		ntc.setString("id", this.id);
		ntc.setInteger("color", this.colorID);
		ntc.setInteger("dimID", this.dimID);
		ntc.setString("name", this.displayName);
		
		NBTTagList permList = new NBTTagList();
		for(String team : this.permissions.keySet()) {
			NBTTagCompound permData = new NBTTagCompound();
			permData.setString("teamID", team);
			this.permissions.get(team).writeToNBT(permData);
			permList.appendTag(permData);
		}
		ntc.setTag("permissions", permList);
	}
	
	public void readFromNBT(NBTTagCompound ntc) {
		this.id = ntc.getString("id");
		this.displayName = ntc.getString("name");
		this.dimID = ntc.getInteger("dimID");
		setColor(ntc.getInteger("color"));
		
		this.permissions = new HashMap<String, PlayerTeamPermission>();
		NBTTagList permList = ntc.getTagList("permissions", (byte)10);
		for(int i=0; i<permList.tagCount(); i++) {
			NBTTagCompound permData = permList.getCompoundTagAt(i);
			String team = permData.getString("teamID");
			this.permissions.put(team, new PlayerTeamPermission(permData));
		}
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
	
	public static PlayerTeam get(Object comp, int dim) {
		return PlayerTeam.get(Contained.getTeamList(dim), comp);
	}
	
	public static PlayerTeam get(Object comp) {
		for(int i : Contained.teamData.keySet()) {
			PlayerTeam team = PlayerTeam.get(Contained.teamData.get(i), comp);
			if (team != null)
				return team;
		}
		return null;
	}
}

package com.contained.game.user;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.contained.game.Contained;
import com.contained.game.Settings;
import com.contained.game.network.ClientPacketHandlerUtil;
import com.contained.game.ui.SurveyData;
import com.contained.game.util.ErrorCase;
import com.contained.game.util.Util;
import com.contained.game.util.ErrorCase.Error;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

/**
 * Special info regarding a single specific player within a team.
 */
public class PlayerTeamIndividual {
	public static final int LONER = 0;
	public static final int TEAM_PLAYER = 1;
	public static final int LEADER = 2;
	
	public String playerName;
	public String teamID;
	public boolean isLeader;
	public SurveyData.SurveyResponse surveyResponses;
	public long joinTime; //Timestamp of when this player first joined their team. 
	public long lastOnline; //Timestamp of when this player was last online.
	private List inventory;
	
	public PlayerTeamIndividual(String name) {
		this.playerName = name;
		this.teamID = null;
		this.joinTime = 0;
		this.lastOnline = 0;
		this.surveyResponses = (new SurveyData()).new SurveyResponse();
		this.isLeader = false;
	}
	
	public PlayerTeamIndividual(NBTTagCompound ntc) {
		this.readFromNBT(ntc);
	}
	
	public PlayerTeamIndividual(PlayerTeamIndividual pdata) {
		NBTTagCompound ntc = new NBTTagCompound();
		pdata.writeToNBT(ntc);
		this.readFromNBT(ntc);
	}
	
	public void setInventory(List inventory){
		this.inventory = inventory;
	}
	
	public List getInventory(){
		return inventory;
	}

	/**
	 * Attempts to join the given team. 
	 * Possible failures: NOT_EXISTS, TEAM_FULL, IND_ONLY
	 */
	public ErrorCase.Error joinTeam(String teamID) {
		return joinTeam(teamID, false);
	}
	
	public ErrorCase.Error joinTeam(String teamID, boolean isLeader) {
		if (this.teamID == null) {
			PlayerTeam requestedTeam = PlayerTeam.get(teamID);
			if (requestedTeam == null)
				return Error.NOT_EXISTS; //Team doesn't exist.
			else if (requestedTeam.numMembers() >= Contained.configs.maxTeamSize[Settings.getDimConfig(requestedTeam.dimID)])
				return Error.TEAM_FULL; //Team is full.
			
			this.teamID = teamID;
			this.joinTime = System.currentTimeMillis();
			if (isLeader)
				this.isLeader = true;
			ClientPacketHandlerUtil.syncTeamMembershipChangeToAll(this);
			return Error.NONE; //Successfully joined team.
		}
		return Error.IND_ONLY; //Already in a team.
	}
	
	/**
	 * Have this player leave the team, also disbanding the team if this was its last player.
	 * Possible failures: TEAM_ONLY
	 */
	public ErrorCase.Error leaveTeam() {
		if (this.teamID == null)
			return Error.TEAM_ONLY; //This player wasn't in a team.
		
		PlayerTeam team = PlayerTeam.get(this.teamID);
		this.demote();
		this.teamID = null;
		this.joinTime = 0;
		
		if (team.numMembers() == 0) {
			this.isLeader = false;
			team.disbandTeam();
		}
		
		ClientPacketHandlerUtil.syncTeamMembershipChangeToAll(this);
		return Error.NONE;
	}
	
	/**
	 * Make this player a team leader, if they aren't already.
	 * Possible failures: TEAM_ONLY, ALREADY_LEADER
	 */
	public ErrorCase.Error promote() {
		if (this.teamID == null)
			return Error.TEAM_ONLY;
		if (this.isLeader)
			return Error.ALREADY_LEADER;
		
		PlayerTeam team = PlayerTeam.get(this.teamID);
		this.isLeader = true;
		team.sendMessageToTeam(team.getFormatCode()+"[NOTICE] "+this.playerName+" is now a leader of this team.");
		
		EntityPlayer playerServerEnt = Util.getOnlinePlayer(this.playerName);
		if (playerServerEnt != null)
			Contained.channel.sendTo(ClientPacketHandlerUtil.packetSyncLocalPlayer(playerServerEnt).toPacket(), (EntityPlayerMP)playerServerEnt);
		
		return Error.NONE;
	}
	
	/**
	 * Remove this player's leadership position. If the team has no more leaders after this action,
	 * then transfer leadership to the member who has been in the group the longest.
	 * Possible failures: LEADER_ONLY, TEAM_ONLY, CANNOT_DEMOTE
	 */
	public ErrorCase.Error demote() {
		if (this.teamID == null)
			return Error.TEAM_ONLY;
		if (!this.isLeader)
			return Error.LEADER_ONLY;
		
		PlayerTeam team = PlayerTeam.get(this.teamID);	
		if (team.numMembers() == 1)
			return Error.CANNOT_DEMOTE; //This is the last member in the team! Can't leave leadership.
		
		this.isLeader = false;
		int leaderCount = 0;
		for(PlayerTeamIndividual ind : Contained.teamMemberData) {
			if (ind.teamID != null && ind.teamID.equals(team.id) && ind.isLeader) {
				leaderCount += 1;
			}
		}

		if (leaderCount == 0) {
			//This team has no more leaders, transfer leadership.
			PlayerTeamIndividual candidate = null;
			long oldestJoinTime = Long.MAX_VALUE;
			
			for (PlayerTeamIndividual player : Contained.teamMemberData) {
				if (player.teamID != null && player.teamID.equals(team.id) && !player.playerName.equals(this.playerName)) {
					if (player.joinTime < oldestJoinTime) {
						oldestJoinTime = player.joinTime;
						candidate = player;
					}
				}
			}
			
			candidate.promote();
		}
		
		EntityPlayer playerServerEnt = Util.getOnlinePlayer(this.playerName);
		if (playerServerEnt != null)
			Contained.channel.sendTo(ClientPacketHandlerUtil.packetSyncLocalPlayer(playerServerEnt).toPacket(), (EntityPlayerMP)playerServerEnt);
		
		return Error.NONE;
	}
	
	public void writeToNBT(NBTTagCompound ntc) {
		ntc.setString("name", this.playerName);
		ntc.setBoolean("isLeader", this.isLeader);
		ntc.setLong("joined", joinTime);
		ntc.setLong("lastOnline", lastOnline);
		NBTTagCompound surveyData = new NBTTagCompound();
		this.surveyResponses.writeToNBT(surveyData);
		ntc.setTag("surveyResponses", surveyData);
		if (this.teamID != null)
			ntc.setString("team", this.teamID);
		else if (ntc.hasKey("team"))
			ntc.removeTag("team");
		NBTTagList inventoryList = new NBTTagList();
		if(inventory != null){
			Iterator iterator = inventory.iterator();
			while(iterator.hasNext()){
				NBTTagCompound saveItem = new NBTTagCompound();
				ItemStack item = (ItemStack) iterator.next();
				if(item != null){
					item.writeToNBT(saveItem);
					inventoryList.appendTag(saveItem);
				}
			}
		}
		ntc.setTag("inventory", inventoryList);
	}
	
	public void readFromNBT(NBTTagCompound ntc) {
		this.playerName = ntc.getString("name");
		this.isLeader = ntc.getBoolean("isLeader");
		this.joinTime = ntc.getLong("joined");
		this.lastOnline = ntc.getLong("lastOnline");
		NBTTagCompound surveyData = ntc.getCompoundTag("surveyResponses");
		this.surveyResponses = (new SurveyData()).new SurveyResponse();
		this.surveyResponses.readFromNBT(surveyData);
		if (ntc.hasKey("team"))
			this.teamID = ntc.getString("team");
		else
			this.teamID = null;
		NBTTagList inventoryList = ntc.getTagList("inventory", (byte)10);
		for(int i=0; i<inventoryList.tagCount(); i++) {
			NBTTagCompound data = inventoryList.getCompoundTagAt(i);
			ItemStack item = ItemStack.loadItemStackFromNBT(data);
			inventory.add(item);
		}
	}
	
	public static boolean isLeader(EntityPlayer p) {
		PlayerTeamIndividual ind = PlayerTeamIndividual.get(p);
		if (ind != null && ind.isLeader)
			return true;
		return false;
	}
	
	public int getStatus() {
		if (this.teamID == null)
			return LONER;
		else if (this.isLeader)
			return LEADER;
		else
			return TEAM_PLAYER;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof PlayerTeamIndividual)
			return this.playerName.equals(((PlayerTeamIndividual)o).playerName);
		else if (o instanceof String)
			return this.playerName.toLowerCase().equals(((String)o).toLowerCase());
		else if (o instanceof EntityPlayer)
			return this.playerName.equals(((EntityPlayer)o).getDisplayName());
		return false;
	}
	
	@Override
	public int hashCode() {
		return this.playerName.hashCode();
	}
	
	public static PlayerTeamIndividual get(ArrayList<PlayerTeamIndividual> players, Object comp) {
		for (int i=0; i<players.size(); i++) {
			if (players.get(i).equals(comp))
				return players.get(i);
		}
		return null;
	}
	
	public static PlayerTeamIndividual get(Object comp) {
		return PlayerTeamIndividual.get(Contained.teamMemberData, comp);
	}
}

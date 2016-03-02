package com.contained.game.user;

import java.util.ArrayList;

import com.contained.game.Contained;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

/**
 * An invitation to join a team.
 */
public class PlayerTeamInvitation {
	
	public final static int NEITHER = -1;
	public final static int TO = 0;		//Invitation to a team from a player.
	public final static int FROM = 1;	//Invitation from a team leader to a player.
	
	public String playerName; //The player who is not currently in a team.
	public String teamID;	  //The team the player will join if the invitation is accepted.
	public int direction;	  //TO or FROM
	
	public PlayerTeamInvitation(String playerName, String teamID, int direction) {
		this.playerName = playerName;
		this.teamID = teamID;
		this.direction = direction;
	}

	public PlayerTeamInvitation(NBTTagCompound ntc) {
		this.readFromNBT(ntc);
	}
	
	/**
	 * Get all team invitations relevant to the given player.
	 */
	public static ArrayList<PlayerTeamInvitation> getInvitations(EntityPlayer p) {
		PlayerTeamIndividual pdata = PlayerTeamIndividual.get(p);
		return getInvitations(pdata);
	}
	
	public static ArrayList<PlayerTeamInvitation> getInvitations(PlayerTeamIndividual p) {
		ArrayList<PlayerTeamInvitation> myInvites = new ArrayList<PlayerTeamInvitation>();
		
		for(PlayerTeamInvitation invite : Contained.teamInvitations) {
				if (invite.direction == TO && p.isLeader && p.teamID.equals(invite.teamID))
					myInvites.add(invite);
				else if (invite.direction == FROM && p.teamID == null) {
					PlayerTeam request = PlayerTeam.get(invite.teamID);
					if (request == null || request.numMembers() >= Contained.configs.maxTeamSize)
						// Exclude this invite if the team has reached member capacity 
						// since the time the invite was sent.
						continue;
					myInvites.add(invite);
				}
		}
		
		return myInvites;
	}	
	
	public void writeToNBT(NBTTagCompound ntc) {
		ntc.setString("player", this.playerName);
		ntc.setString("team", this.teamID);
		ntc.setInteger("type", this.direction);
	}
	
	public void readFromNBT(NBTTagCompound ntc) {
		this.playerName = ntc.getString("player");
		this.teamID = ntc.getString("team");
		this.direction = ntc.getInteger("type");
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof PlayerTeamInvitation) {
			PlayerTeamInvitation invite = (PlayerTeamInvitation)o;
			if (this.direction == invite.direction) {
				if (this.direction == TO && invite.playerName.toLowerCase().equals(this.playerName.toLowerCase()))
					return true;
				else if (this.direction == FROM && invite.teamID.equals(this.teamID))
					return true;
			}
			else if (invite.playerName.toLowerCase().equals(this.playerName.toLowerCase()) && invite.teamID.equals(this.teamID))
				return true;
		}
		return false;
	}
	
}

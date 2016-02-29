package com.contained.game.commands;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.contained.game.Contained;
import com.contained.game.user.PlayerTeam;
import com.contained.game.user.PlayerTeamIndividual;
import com.contained.game.user.PlayerTeamInvitation;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;

/**
 * Sends an invitation to join a team.
 */
public class CommandInvitationSend implements ICommand {
	private final List<String> aliases;

	public CommandInvitationSend() 
	{ 
		aliases = new ArrayList<String>(); 
		aliases.add(getCommandName()); 
		aliases.add("request");
		aliases.add("requestinvite");
		aliases.add("requestinvitation");
		aliases.add("sendinvite");
		aliases.add("sendinvitation");
	} 	

	@Override 
	public String getCommandName() { return "invite"; } 

	@Override 
	public void processCommand(ICommandSender sender, String[] argString)
	{ 				
		//Future TODO:
		//  Add spam protection, so if an invitation is rejected, then that
		//  same invitation cannot be sent again for a period of time.

		if (!sender.getEntityWorld().isRemote) {
			String out = "";

			if (sender instanceof EntityPlayer) {
				if (argString.length == 0) {
					sender.addChatMessage(new ChatComponentText(getCommandUsage(sender)));
					return;
				}

				EntityPlayer p = (EntityPlayer)sender;
				PlayerTeamIndividual pdata = PlayerTeamIndividual.get(p);
				String inviteName = StringUtils.join(argString, " ").toLowerCase();
				PlayerTeamInvitation newInvite = null;

				if (pdata.isLeader) {
					//Leaders can only send invitations to players not in teams.
					PlayerTeam leadersTeam = PlayerTeam.get(pdata.teamID);
					if (leadersTeam.numMembers() >= PlayerTeam.MAX_TEAM_SIZE)
						out = "Your team already has the max number of members.";
					else {
						PlayerTeamIndividual recipient = PlayerTeamIndividual.get(inviteName);
						if (recipient == null)
							out = "No player found by that name.";
						else {
							if (recipient.teamID != null)
								out = "That player is already in a team.";
							else
								newInvite = new PlayerTeamInvitation(recipient.playerName, leadersTeam.id, PlayerTeamInvitation.FROM);
						}
					}
				} else {
					// Players can send invitations to teams (or team leaders -- 
					// equivalent to sending it to the team), but only if they're
					// not already in a team.
					if (pdata.teamID != null)
						out = "You can't request a team invitation, because you're already in a team.";
					else {
						PlayerTeamIndividual leaderData = PlayerTeamIndividual.get(inviteName);
						PlayerTeam teamData = null;
						for (PlayerTeam t : Contained.teamData) {
							if (t.displayName.toLowerCase().equals(inviteName.toLowerCase())) {
								teamData = t;
								break;
							}
						}

						if (leaderData == null && teamData == null)
							out = "No player or team found by that name.";
						else {
							if (teamData == null)
								teamData = PlayerTeam.get(leaderData.teamID);
							if (teamData.numMembers() >= PlayerTeam.MAX_TEAM_SIZE)
								out = "Can't send invitation, beacuse the requested team is already full.";
							else
								newInvite = new PlayerTeamInvitation(pdata.playerName, teamData.id, PlayerTeamInvitation.TO);
						}
					}
				}

				if (newInvite != null) {
					if (Contained.teamInvitations.indexOf(newInvite) == -1) {
						Contained.teamInvitations.add(newInvite);
						out = "Invitation has been sent.";

						//Send message to invited player to let them know they got an invitation.
						@SuppressWarnings("rawtypes")
						List onlinePlayers = MinecraftServer.getServer().getConfigurationManager().playerEntityList;
						for (Object o : onlinePlayers) {
							if (o instanceof EntityPlayer) {
								EntityPlayer onlinePlayer = (EntityPlayer)o;
								PlayerTeamIndividual onlineData = PlayerTeamIndividual.get(onlinePlayer);
								
								if (newInvite.direction == PlayerTeamInvitation.TO) {
									if (onlineData.isLeader && onlineData.teamID.equals(newInvite.teamID))
										onlinePlayer.addChatComponentMessage(new ChatComponentText("[*] "+newInvite.playerName+" would like to join your team."));
								}
								else {
									if (onlinePlayer.getDisplayName().toLowerCase().equals(newInvite.playerName.toLowerCase())) {
										PlayerTeam teamData = PlayerTeam.get(newInvite.teamID);
										onlinePlayer.addChatComponentMessage(new ChatComponentText("[*] "+teamData.getFormatCode()+"§l"+teamData.displayName+"§r would like you to join their group."));
									}
								}
							}
						}						
					} else
						out = "This invitation has already been sent.";
				}
			} else
				return;

			if (!out.equals(""))
				sender.addChatMessage(new ChatComponentText(out));
		}
	} 

	@Override         
	public String getCommandUsage(ICommandSender var1) { 
		return "/"+getCommandName()+" <player or team name>"; 
	} 

	@Override 
	public int compareTo(Object o) { return 0; } 

	@Override 
	public List<String> getCommandAliases() { return this.aliases; } 

	@Override 
	public boolean canCommandSenderUseCommand(ICommandSender var1) { 
		return true;
	} 

	@Override  
	public List<String> addTabCompletionOptions(ICommandSender var1, String[] var2) { 
		return null; 
	} 

	@Override 
	public boolean isUsernameIndex(String[] var1, int var2) { 
		return false;
	} 
}

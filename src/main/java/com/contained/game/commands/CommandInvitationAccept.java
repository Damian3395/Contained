package com.contained.game.commands;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.contained.game.Contained;
import com.contained.game.user.PlayerTeam;
import com.contained.game.user.PlayerTeamIndividual;
import com.contained.game.user.PlayerTeamInvitation;
import com.contained.game.util.ErrorCase;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;

/**
 * Accepts a pending invitation to join a team.
 */
public class CommandInvitationAccept implements ICommand {
	private final List<String> aliases;

	public CommandInvitationAccept() 
	{ 
		aliases = new ArrayList<String>(); 
		aliases.add(getCommandName()); 
		aliases.add("acceptinvite");
		aliases.add("acceptinvitation");
		aliases.add("join");
	} 	
	
	@Override 
	public String getCommandName() { return "accept"; } 

	@Override 
	public void processCommand(ICommandSender sender, String[] argString)
	{ 				
		if (!sender.getEntityWorld().isRemote) {
			String out = "";
			
			if (sender instanceof EntityPlayer) {
				if (argString.length == 0) {
					//TODO: Using accept with no arguments should work if you have
					//      only a single pending invitation.
					sender.addChatMessage(new ChatComponentText(getCommandUsage(sender)));
					return;
				}
				
				EntityPlayer p = (EntityPlayer)sender;
				PlayerTeamIndividual pdata = PlayerTeamIndividual.get(p);
				String inviteName = StringUtils.join(argString, " ").toLowerCase();
				
				//Find all invites that are relevant to you.
				ArrayList<PlayerTeamInvitation> myInvites
								= PlayerTeamInvitation.getInvitations(pdata);
				
				if (myInvites.size() == 0)
					out = "You have no invites.";
				else {
					//Find the team & player of the invitation that matches the given command.
					PlayerTeam matchedTeam = null;
					PlayerTeamIndividual matchedPlayer = null;
					
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
					if (matchedTeam == null || matchedPlayer == null)
						out = "The given invitation name does not match any of your invites.";
					else {
						ErrorCase.Error result = matchedPlayer.joinTeam(matchedTeam.id);
						if (result == ErrorCase.Error.NOT_EXISTS)
							out = "Accepting the invitation failed, because that team no longer exists.";
						else if (result == ErrorCase.Error.IND_ONLY) {
							if (pdata.isLeader)
								out = "Accepting the invitation failed, as the player has already joined another group.";
							else
								out = "You can't accept invitations, because you're already in a team.";
						}
						else if (result == ErrorCase.Error.TEAM_FULL)
							out = "The team has reached full member capacity, so the invitation can't be accepted.";
						else if (result == ErrorCase.Error.NONE) {
							matchedTeam.sendMessageToTeam(matchedTeam.getFormatCode()
									+"[NOTICE] §l"+matchedPlayer.playerName+"§r"
									+matchedTeam.getFormatCode()+" has joined the team!");
							
							//Accept successful, remove invitation.
							ArrayList<PlayerTeamInvitation> toRemove = new ArrayList<PlayerTeamInvitation>();
							toRemove.add(new PlayerTeamInvitation(matchedPlayer.playerName, matchedTeam.id, PlayerTeamInvitation.NEITHER));
							Contained.teamInvitations.removeAll(toRemove);
						}
					}
				}
			} else
				return;
			
			if (!out.equals(""))
				sender.addChatMessage(new ChatComponentText(out));
		}
	} 
	
	@Override         
	public String getCommandUsage(ICommandSender var1) { 
		return "/"+getCommandName()+" <invitation name> (see /invites)"; 
	} 
	
	@Override 
	public int compareTo(Object o) { 
		if (o instanceof ICommand)
			return this.getCommandName().compareTo(((ICommand)o).getCommandName());
		return 0; 
	}  

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

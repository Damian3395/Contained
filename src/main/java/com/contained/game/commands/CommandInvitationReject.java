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
import net.minecraft.util.ChatComponentText;

/**
 * Rejects a pending invitation to join a team.
 */
public class CommandInvitationReject implements ICommand {
	private final List<String> aliases;

	public CommandInvitationReject() 
	{ 
		aliases = new ArrayList<String>(); 
		aliases.add(getCommandName()); 
		aliases.add("rejectinvite");
		aliases.add("rejectinvitation");
	} 	
	
	@Override 
	public String getCommandName() { return "reject"; } 

	@Override 
	public void processCommand(ICommandSender sender, String[] argString)
	{ 				
		//Future TODO:
		//  Should the person who sent the invitation be informed when
		//  the recipient rejects their invite?
		
		if (!sender.getEntityWorld().isRemote) {
			String out = "";
			
			if (sender instanceof EntityPlayer) {
				if (argString.length == 0) {
					//TODO: Using reject with no arguments should work if you have
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
					//Find the invitation to remove.
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
					if (probe == null)
						out = "No team/player seems to exist with that name.";
					else {
						ArrayList<PlayerTeamInvitation> toRemove = new ArrayList<PlayerTeamInvitation>();
						toRemove.add(probe);
						int beforeSize = Contained.teamInvitations.size();
						Contained.teamInvitations.removeAll(toRemove);
						int afterSize = Contained.teamInvitations.size();
						
						if (beforeSize != afterSize)
							out = "Invitation has been removed.";
						else
							out = "No invitation found with that name.";
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

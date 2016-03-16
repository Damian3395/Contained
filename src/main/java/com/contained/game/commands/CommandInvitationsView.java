package com.contained.game.commands;

import java.util.ArrayList;
import java.util.List;

import com.contained.game.user.PlayerTeam;
import com.contained.game.user.PlayerTeamIndividual;
import com.contained.game.user.PlayerTeamInvitation;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;

/**
 * Outputs a list of all pending invitations (either requesting for you to join a team,
 * or requests from people asking to join your team).
 */
public class CommandInvitationsView implements ICommand {
	private final List<String> aliases;

	public CommandInvitationsView() 
	{ 
		aliases = new ArrayList<String>(); 
		aliases.add(getCommandName()); 
		aliases.add("invitations");
		aliases.add("viewinvitations");
		aliases.add("viewinvites");
	} 	
	
	@Override 
	public String getCommandName() { return "invites"; } 

	@Override 
	public void processCommand(ICommandSender sender, String[] argString)
	{ 				
		if (!sender.getEntityWorld().isRemote) {
			String out = "";
			
			if (sender instanceof EntityPlayer) {
				EntityPlayer p = (EntityPlayer)sender;
				PlayerTeamIndividual pdata = PlayerTeamIndividual.get(p);
				
				//Find all invites that are relevant to you.
				ArrayList<PlayerTeamInvitation> myInvites
								= PlayerTeamInvitation.getInvitations(pdata);
				
				if (myInvites.size() == 0)
					out = "You have no invites.";
				else {
					if (pdata.isLeader) {
						//You are a team leader. Output player names that want to join your team.
						out = "Requests to join your team: ";
						for(int i=0; i<myInvites.size(); i++) {
							if (i > 0)
								out += ", ";
							out += myInvites.get(i).playerName;
						}
					} else {
						//You are not in a team. Output teams names that want you to join them.
						out = "Teams that want you to join: ";
						int ind = 0;
						for(int i=0; i<myInvites.size(); i++) {
							PlayerTeam teamData = PlayerTeam.get(myInvites.get(i).teamID);
							if (teamData != null) {
								if (ind > 0)
									out += ", ";
								out += teamData.displayName;
								ind++;
							}
						}
					}
					out += " (use /accept or /reject)";
				}
			} else
				return;
			
			if (!out.equals(""))
				sender.addChatMessage(new ChatComponentText(out));
		}
	} 
	
	@Override         
	public String getCommandUsage(ICommandSender var1) { 
		return "/"+getCommandName(); 
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

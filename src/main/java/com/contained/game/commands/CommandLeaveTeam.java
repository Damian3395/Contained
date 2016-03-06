package com.contained.game.commands;

import java.util.ArrayList;
import java.util.List;

import com.contained.game.user.PlayerTeam;
import com.contained.game.user.PlayerTeamIndividual;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;

/**
 * Makes the sender of the command leave their current team.
 */
public class CommandLeaveTeam implements ICommand {
	private final List<String> aliases; 

	@Override 
	public String getCommandName() { return "leaveteam"; } 
	
	public CommandLeaveTeam() 
	{ 
		aliases = new ArrayList<String>(); 
		aliases.add(getCommandName()); 
		aliases.add("leave");
	} 	

	@Override 
	public void processCommand(ICommandSender sender, String[] argString)
	{ 		
		if (!sender.getEntityWorld().isRemote) {
			String out = "";
			
			if (sender instanceof EntityPlayer) {
				EntityPlayer p = (EntityPlayer)sender;
				PlayerTeamIndividual pdata = PlayerTeamIndividual.get(p);
					
				if (pdata.teamID == null)
					out = "You aren't in a team.";
				else {
					PlayerTeam team = PlayerTeam.get(pdata.teamID);
					pdata.leaveTeam();
					
					team.sendMessageToTeam(team.getFormatCode()+"[NOTICE] "+pdata.playerName+" has left the team.");
					out = "You have left the team.";
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

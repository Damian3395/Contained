package com.contained.game.commands;

import java.util.ArrayList;
import java.util.List;

import com.contained.game.Contained;
import com.contained.game.user.PlayerTeam;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;

/**
 * Outputs a list of all teams.
 */
public class CommandViewTeams implements ICommand {
	private final List<String> aliases; 

	public CommandViewTeams() 
	{ 
		aliases = new ArrayList<String>(); 
		aliases.add(getCommandName()); 
		aliases.add("viewteams");
		aliases.add("allteams");
	} 	
	
	@Override 
	public String getCommandName() { return "teams"; } 

	@Override 
	public void processCommand(ICommandSender sender, String[] argString)
	{ 		
		String out = "";
		if (Contained.teamData.size() == 0)
			out = "There are no teams.";
		else {
			out = "Teams: ";
			for(int i=0; i<Contained.teamData.size(); i++) {
				PlayerTeam team = Contained.teamData.get(i);
				if (i > 0)
					out += "§r, ";
				out += team.getFormatCode();
				out += team.displayName;
			}
		}
		
		sender.addChatMessage(new ChatComponentText(out));
	} 
	
	@Override         
	public String getCommandUsage(ICommandSender var1) { 
		return "/"+getCommandName(); 
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

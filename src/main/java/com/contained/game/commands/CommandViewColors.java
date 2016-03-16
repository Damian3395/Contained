package com.contained.game.commands;

import java.util.ArrayList;
import java.util.List;

import com.contained.game.user.PlayerTeam;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;

/**
 * Outputs a list of all teams.
 */
public class CommandViewColors implements ICommand {
	private final List<String> aliases;

	public CommandViewColors() 
	{ 
		aliases = new ArrayList<String>(); 
		aliases.add(getCommandName()); 
		aliases.add("viewcolors");
		aliases.add("allcolors");
	} 	
	
	@Override 
	public String getCommandName() { return "colors"; } 

	@Override 
	public void processCommand(ICommandSender sender, String[] argString)
	{ 		
		String out = "Colors:";
		for (int i=0; i<PlayerTeam.formatCodes.length; i++) {
			out += " "+PlayerTeam.formatCodes[i]+i;				
		}
		
		sender.addChatMessage(new ChatComponentText(out));
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

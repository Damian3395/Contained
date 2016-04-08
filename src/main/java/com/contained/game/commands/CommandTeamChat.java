package com.contained.game.commands;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.contained.game.data.DataLogger;
import com.contained.game.user.PlayerTeam;
import com.contained.game.user.PlayerTeamIndividual;
import com.contained.game.util.Util;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;

/**
 * Sends a chat message that's only visible to the other people in your team.
 */
public class CommandTeamChat implements ICommand {
	private final List<String> aliases;

	public CommandTeamChat() 
	{ 
		aliases = new ArrayList<String>(); 
		aliases.add(getCommandName()); 
	} 	

	@Override 
	public String getCommandName() { return "t"; } 

	@Override 
	public void processCommand(ICommandSender sender, String[] argString)
	{ 		
		if (!sender.getEntityWorld().isRemote) {
			String out = "";

			if (argString.length == 0)
				out = getCommandUsage(sender);
			else {
				if (sender instanceof EntityPlayer) {
					EntityPlayer p = (EntityPlayer)sender;
					PlayerTeamIndividual pdata = PlayerTeamIndividual.get(p);

					if (pdata.teamID != null) {
						String message = StringUtils.join(argString, " ");
						PlayerTeam team = PlayerTeam.get(pdata.teamID);						
						team.sendMessageToTeam(team.getFormatCode()+p.getDisplayName()+":"+message);
						String world = Util.getDimensionString(p.dimension);
						DataLogger.insertGuildChat(Util.getServerID(), 
								p.getDisplayName(), 
								pdata.teamID, 
								world, 
								message, 
								Util.getDate());
					} else
						out = "You aren't in a team.";
				} else
					return;
			}

			if (!out.equals(""))
				sender.addChatMessage(new ChatComponentText(out));
		}
	} 

	@Override         
	public String getCommandUsage(ICommandSender var1) { 
		return "/"+getCommandName()+" <message>"; 
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

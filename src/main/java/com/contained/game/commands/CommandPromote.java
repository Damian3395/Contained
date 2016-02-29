package com.contained.game.commands;

import java.util.ArrayList;
import java.util.List;

import com.contained.game.user.PlayerTeamIndividual;
import com.contained.game.util.ErrorCase;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;

/**
 * Promotes a specified member in this team to leadership position. (leader only)
 */
public class CommandPromote implements ICommand {
	private final List<String> aliases; 

	@Override 
	public String getCommandName() { return "promote"; } 

	public CommandPromote() 
	{ 
		aliases = new ArrayList<String>(); 
		aliases.add(getCommandName());
		aliases.add("makeleader");
		aliases.add("leadership");
		aliases.add("addleader");
		aliases.add("newleader");
	} 	

	@Override 
	public void processCommand(ICommandSender sender, String[] argString)
	{ 		
		if (!sender.getEntityWorld().isRemote) {
			String out = "";

			if (sender instanceof EntityPlayer) {
				if (argString.length != 1)
					out = getCommandUsage(sender);
				else {
					EntityPlayer p = (EntityPlayer)sender;
					PlayerTeamIndividual pdata = PlayerTeamIndividual.get(p);

					if (pdata.teamID == null)
						out = "You aren't in a team.";
					else if (!pdata.isLeader)
						out = "Only team leaders can elect new leaders.";
					else {
						PlayerTeamIndividual toPromote = PlayerTeamIndividual.get(argString[0]);
						if (toPromote == null)
							out = "No player was found by that name.";
						else if (toPromote.teamID == null)
							out = "That player is not in a team.";
						else if (!toPromote.teamID.equals(pdata.teamID))
							out = "That player is not part of your team.";
						else {
							ErrorCase.Error result = toPromote.promote();
							if (result == ErrorCase.Error.ALREADY_LEADER)
								out = "That player is already a leader of this team.";
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
		return "/"+getCommandName()+" <player name>"; 
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

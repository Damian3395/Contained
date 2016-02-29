package com.contained.game.commands;

import java.util.ArrayList;
import java.util.List;

import com.contained.game.user.PlayerTeam;
import com.contained.game.user.PlayerTeamIndividual;
import com.contained.game.util.ErrorCase;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;

/**
 * Makes the sender of the command leave their leadership position.
 */
public class CommandDemote implements ICommand {
	private final List<String> aliases; 

	@Override 
	public String getCommandName() { return "demote"; } 
	
	public CommandDemote() 
	{ 
		aliases = new ArrayList<String>(); 
		aliases.add(getCommandName());
		aliases.add("dethrone");
		aliases.add("stepdown");
		aliases.add("leaveleader");
	} 	

	@Override 
	public void processCommand(ICommandSender sender, String[] argString)
	{ 		
		if (!sender.getEntityWorld().isRemote) {
			String out = "";
			
			if (sender instanceof EntityPlayer) {
				if (argString.length > 0)
					out = getCommandUsage(sender);
				else {
					EntityPlayer p = (EntityPlayer)sender;
					PlayerTeamIndividual pdata = PlayerTeamIndividual.get(p);
						
					if (pdata.teamID == null)
						out = "You aren't in a team.";
					else {
						PlayerTeam team = PlayerTeam.get(pdata.teamID);
						ErrorCase.Error result = pdata.demote();
						if (result == ErrorCase.Error.LEADER_ONLY)
							out = "You aren't a leader of this team.";
						else if (result == ErrorCase.Error.CANNOT_DEMOTE)
							out = "You're the only member of this group... you must stay the leader.";
						else
							team.sendMessageToTeam(team.getFormatCode()+"[NOTICE] "+pdata.playerName+" is no longer a team leader.");
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

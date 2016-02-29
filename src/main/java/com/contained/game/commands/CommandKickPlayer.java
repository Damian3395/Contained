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
 * Kicks a player out of the team. (leader only)
 */
public class CommandKickPlayer implements ICommand {
	private final List<String> aliases; 

	@Override 
	public String getCommandName() { return "kick"; } 

	public CommandKickPlayer() 
	{ 
		aliases = new ArrayList<String>(); 
		aliases.add(getCommandName()); 
		aliases.add("banish");
		aliases.add("removeplayer");
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
						out = "Only team leaders can kick players.";
					else {
						PlayerTeamIndividual toKick = PlayerTeamIndividual.get(argString[0]);
						if (toKick == null)
							out = "No player was found by that name.";
						else if (toKick.teamID == null)
							out = "That player is not in a team.";
						else if (!toKick.teamID.equals(pdata.teamID))
							out = "That player is not part of your team.";
						else {
							PlayerTeam team = PlayerTeam.get(pdata.teamID);
							team.sendMessageToTeam(team.getFormatCode()+"[NOTICE] "+toKick.playerName+" has been kicked from the team.");
							toKick.leaveTeam();
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

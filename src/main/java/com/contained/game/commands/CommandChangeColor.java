package com.contained.game.commands;

import java.util.ArrayList;
import java.util.List;

import com.contained.game.Contained;
import com.contained.game.network.ClientPacketHandler;
import com.contained.game.user.PlayerTeam;
import com.contained.game.user.PlayerTeamIndividual;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;

/**
 * Changes the color of your team. (Leader only)
 */
public class CommandChangeColor implements ICommand {
	private final List<String> aliases; 

	@Override 
	public String getCommandName() { return "teamcolor"; } 
	
	public CommandChangeColor() 
	{ 
		aliases = new ArrayList<String>(); 
		aliases.add(getCommandName()); 
	} 	

	@Override 
	public void processCommand(ICommandSender sender, String[] argString)
	{ 		
		if (!sender.getEntityWorld().isRemote) {
			String out = "";
			
			if (argString.length != 1)
				out = getCommandUsage(sender);
			else {
				if (sender instanceof EntityPlayer) {
					EntityPlayer p = (EntityPlayer)sender;
					PlayerTeamIndividual pdata = PlayerTeamIndividual.get(p);
						
					if (pdata.teamID != null) {
						if (PlayerTeamIndividual.isLeader(p)) {
							PlayerTeam team = PlayerTeam.get(pdata.teamID);
							boolean success = team.setColor(argString[0]);
							
							if (!success)
								out = getCommandUsage(sender);
							else {
								team.sendMessageToTeam(team.getFormatCode()+"[NOTICE] The team color has been changed.");
								Contained.channel.sendToAll(ClientPacketHandler.packetSyncTeams(Contained.teamData).toPacket());
							}
						} else
							out = "Only team leaders can change the team's color.";
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
		return "/"+getCommandName()+" <color> (color can be 'random', or a value [0-"+(PlayerTeam.formatColors.length-1)+"]... see /viewcolors)"; 
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

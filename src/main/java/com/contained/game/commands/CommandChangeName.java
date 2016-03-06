package com.contained.game.commands;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.contained.game.Contained;
import com.contained.game.network.ClientPacketHandler;
import com.contained.game.user.PlayerTeam;
import com.contained.game.user.PlayerTeamIndividual;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;

/**
 * Changes the name of your team. (Leader only)
 */
public class CommandChangeName implements ICommand {
	private final List<String> aliases;

	@Override 
	public String getCommandName() { return "teamname"; } 
	
	public CommandChangeName() 
	{ 
		aliases = new ArrayList<String>(); 
		aliases.add(getCommandName()); 
		aliases.add("renameteam");
	} 	

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
						if (PlayerTeamIndividual.isLeader(p)) {
							String name = StringUtils.join(argString, " ");
							boolean allowedName = true;
							for(PlayerTeam t : Contained.teamData) {
								if (t.displayName.toLowerCase().equals(name.toLowerCase())) {
									allowedName = false;
									break;
								}
							}
							
							if (allowedName) {
								PlayerTeam team = PlayerTeam.get(pdata.teamID);
								team.displayName = name;
								team.sendMessageToTeam(team.getFormatCode()+"[NOTICE] The team has been renamed to "+team.getFormatCode()+"§l"+team.displayName+team.getFormatCode()+".");
								Contained.channel.sendToAll(ClientPacketHandler.packetSyncTeams(Contained.teamData).toPacket());
							} else
								out = "Another team is already using that name.";
						} else
							out = "Only team leaders can change the team's name.";
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
		return "/"+getCommandName()+" <name>"; 
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

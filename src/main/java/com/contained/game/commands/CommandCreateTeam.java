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
 * Creates a new team and sets the creator of the team as its leader.
 */
public class CommandCreateTeam implements ICommand {
	private final List<String> aliases; 

	@Override 
	public String getCommandName() { return "createteam"; } 
	
	public CommandCreateTeam() 
	{ 
		aliases = new ArrayList<String>(); 
		aliases.add(getCommandName()); 
		aliases.add("maketeam");
		aliases.add("newteam");
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
						
					if (pdata.teamID == null) {
						String name = StringUtils.join(argString, " ");
						boolean allowedName = true;
						for(PlayerTeam t : Contained.teamData) {
							if (t.displayName.toLowerCase().equals(name.toLowerCase())) {
								allowedName = false;
								break;
							}
						}
						
						if (allowedName) {
							PlayerTeam newTeam = new PlayerTeam(name, 0);
							newTeam.randomColor();
							Contained.teamData.add(newTeam);
							
							System.out.println(pdata.joinTeam(newTeam.id).toString());
							pdata.isLeader = true;
							out = "You are now the leader of "+newTeam.getFormatCode()+"§l"+newTeam.displayName+".";
							ClientPacketHandler.packetSyncTeams(Contained.teamData).sendToClients();
						} else
							out = "Another team is already using that name.";
					} else
						out = "You can't create a team, because you're already in one.";
				} else
					return;
			}
			
			sender.addChatMessage(new ChatComponentText(out));
		}
	} 
	
	@Override         
	public String getCommandUsage(ICommandSender var1) { 
		return "/"+getCommandName()+" <name>"; 
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

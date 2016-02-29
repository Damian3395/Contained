package com.contained.game.commands;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.contained.game.Contained;
import com.contained.game.user.PlayerTeam;
import com.contained.game.user.PlayerTeamIndividual;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;

/**
 * Outputs information about a specific team (or your team, if no arguments specified).
 */
public class CommandViewTeamInfo implements ICommand {
	private final List<String> aliases; 

	public CommandViewTeamInfo() 
	{ 
		aliases = new ArrayList<String>(); 
		aliases.add(getCommandName()); 
		aliases.add("team");
		aliases.add("viewteam");
	} 	
	
	@Override 
	public String getCommandName() { return "teaminfo"; } 

	@Override 
	public void processCommand(ICommandSender sender, String[] argString)
	{ 		
		if (!sender.getEntityWorld().isRemote) {			
			if (sender instanceof EntityPlayer) {
				PlayerTeam toView = null;
				
				if (argString.length == 0) {
					//No argument case: Show information about the player's current team.
					EntityPlayer p = (EntityPlayer)sender;
					PlayerTeamIndividual pdata = PlayerTeamIndividual.get(p);
					
					if (pdata.teamID == null) {
						sender.addChatMessage(new ChatComponentText("You are not in a team. (To view a specific team, use "+getCommandUsage(sender)+")"));
						return;
					}
					toView = PlayerTeam.get(pdata.teamID);
				}
				else {
					//Argument case: Show information about the specified team.
					String teamName = StringUtils.join(argString, " ").toLowerCase();
					for(PlayerTeam team : Contained.teamData) {
						if (team.displayName.toLowerCase().equals(teamName)) {
							toView = team;
							break;
						}
					}
				}
				
				if (toView == null)
					sender.addChatMessage(new ChatComponentText("No team found with that name."));
				else {
					sender.addChatMessage(new ChatComponentText("Team: "+toView.getFormatCode()+"§l"
								+toView.displayName+"§r ("+toView.numMembers()+"/"+PlayerTeam.MAX_TEAM_SIZE+")"));
					
					String leaderList = "";
					for (PlayerTeamIndividual player : Contained.teamMemberData) {
						if (player.isLeader && player.teamID.equals(toView.id)) {
							if (!leaderList.equals(""))
								leaderList += ", ";
							leaderList += player.playerName;
						}
					}
					sender.addChatMessage(new ChatComponentText("Leader(s): "+leaderList));
					
					String onlineList = "";
					for (EntityPlayer player : toView.getOnlinePlayers()) {
						if (!onlineList.equals(""))
							onlineList += ", ";
						onlineList += player.getDisplayName();
					}
					if (onlineList.equals(""))
						onlineList = "None";
					sender.addChatMessage(new ChatComponentText("Members Online: "+onlineList));
				}
			}
		}
	} 
	
	@Override         
	public String getCommandUsage(ICommandSender var1) { 
		return "/"+getCommandName()+" [team name]"; 
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

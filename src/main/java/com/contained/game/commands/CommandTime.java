package com.contained.game.commands;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.contained.game.data.DataLogger;
import com.contained.game.entity.ExtendedPlayer;
import com.contained.game.user.PlayerTeam;
import com.contained.game.user.PlayerTeamIndividual;
import com.contained.game.util.Util;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;

public class CommandTime implements ICommand {
	private final List<String> aliases;

	public CommandTime() { 
		aliases = new ArrayList<String>(); 
		aliases.add(getCommandName()); 
	} 	

	@Override 
	public String getCommandName() { return "time"; } 

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
					ExtendedPlayer properties = ExtendedPlayer.get(p);
					
					if(!properties.isAdmin()){
						out = "You are not an admin!";
					}else{
						WorldServer w = DimensionManager.getWorld(p.dimension);
						if(w != null){
							long time = Long.parseLong(argString[0]);
							w.setWorldTime(time);
						}
					}
				} else
					return;
			}

			if (!out.equals(""))
				sender.addChatMessage(new ChatComponentText(out));
		}
	} 

	@Override         
	public String getCommandUsage(ICommandSender var1) { 
		return "/"+getCommandName()+" <0-24000>"; 
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

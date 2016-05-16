package com.contained.game.commands;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import codechicken.lib.packet.PacketCustom;

import com.contained.game.Contained;
import com.contained.game.data.DataLogger;
import com.contained.game.network.ClientPacketHandlerUtil;
import com.contained.game.user.PlayerTeam;
import com.contained.game.user.PlayerTeamIndividual;
import com.contained.game.util.Resources;
import com.contained.game.util.Util;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;

/**
 * Switch between global chat mode and team chat mode
 */
public class CommandSwitchChatMode implements ICommand {
	private final List<String> aliases;

	public CommandSwitchChatMode() 
	{ 
		aliases = new ArrayList<String>();
		aliases.add(getCommandName()); 
		aliases.add("cm");
	} 	

	@Override 
	public String getCommandName() { return "chatmode"; } 

	@Override 
	public void processCommand(ICommandSender sender, String[] argString)
	{ 		
		if (!sender.getEntityWorld().isRemote && sender instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer)sender;
			PacketCustom packet = new PacketCustom(Resources.MOD_ID, ClientPacketHandlerUtil.SWITCH_CHAT_MODE);
			Contained.channel.sendTo(packet.toPacket(), (EntityPlayerMP)player);
		}
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

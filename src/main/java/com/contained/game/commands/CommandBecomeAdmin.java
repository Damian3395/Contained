package com.contained.game.commands;

import java.util.ArrayList;
import java.util.List;

import com.contained.game.Contained;
import com.contained.game.entity.ExtendedPlayer;
import com.contained.game.network.ClientPacketHandlerUtil;
import com.contained.game.util.Resources;

import codechicken.lib.packet.PacketCustom;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;

public class CommandBecomeAdmin implements ICommand{
	
	private final List<String> aliases; 

	@Override 
	public String getCommandName() { return "becomeadmin"; } 
	
	public CommandBecomeAdmin() 
	{ 
		aliases = new ArrayList<String>(); 
		aliases.add(getCommandName()); 
	} 	

	@Override 
	public void processCommand(ICommandSender sender, String[] argString)
	{ 		
		if (!sender.getEntityWorld().isRemote && sender instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) sender;
			String out = ""; 
		
			if (argString.length != 1){	// must have a parameter as the password
				out = getCommandUsage(sender);
			} else if (argString[0].equals("password")){
				player.setInvisible(true);
				player.capabilities.disableDamage = true;
				player.capabilities.allowFlying = true;
				ExtendedPlayer.get(player).setAdminRights(true);
				PacketCustom adminPacket = new PacketCustom(Resources.MOD_ID, ClientPacketHandlerUtil.PLAYER_ADMIN);
				Contained.channel.sendTo(adminPacket.toPacket(), (EntityPlayerMP)player);
			}
			if (!out.equals("")){
				sender.addChatMessage(new ChatComponentText(out));
			}
		}
	} 
	
	@Override         
	public String getCommandUsage(ICommandSender var1) { 
		return "/"+getCommandName()+" <password>"; 
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

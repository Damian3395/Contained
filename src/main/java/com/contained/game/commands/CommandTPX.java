package com.contained.game.commands;

import java.util.ArrayList;
import java.util.List;

import com.contained.game.entity.ExtendedPlayer;
import com.contained.game.util.Util;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;

/**
 * Teleport to the world spawn of a specific dimension.
 */
public class CommandTPX implements ICommand {
	private final List<String> aliases;

	protected String fullEntityName; 
	protected Entity conjuredEntity; 

	public CommandTPX() 
	{ 
		aliases = new ArrayList<String>(); 
		aliases.add("tpx"); 
		aliases.add("tpd"); 
	} 

	@Override 
	public int compareTo(Object o) { 
		if (o instanceof ICommand)
			return this.getCommandName().compareTo(((ICommand)o).getCommandName());
		return 0; 
	} 

	@Override 
	public String getCommandName() 
	{ 
		return "tpx"; 
	} 

	@Override         
	public String getCommandUsage(ICommandSender var1) 
	{ 
		return "/tpx <dim>"; 
	} 

	@Override 
	public List<String> getCommandAliases() 
	{ 
		return this.aliases;
	} 

	@Override 
	public void processCommand(ICommandSender sender, String[] argString)
	{ 
		World world = sender.getEntityWorld(); 

		if (!world.isRemote && sender instanceof EntityPlayerMP) 
		{ 
			if(argString.length == 0) 
			{ 
				sender.addChatMessage(new ChatComponentText("Usage /tpx <dimensionID>")); 
				return; 
			} 

			try {
				int dimID = Integer.parseInt(argString[0]);
				EntityPlayerMP player = (EntityPlayerMP)sender;

				if (player.ridingEntity == null && player.riddenByEntity == null) {
					if(world.provider.dimensionId != dimID)
						Util.travelToDimension(dimID, player, false);
				} else
					sender.addChatMessage(new ChatComponentText("Teleport failed."));
			} catch (NumberFormatException e) {
				sender.addChatMessage(new ChatComponentText("Usage /tpx <dimensionID>")); 
				return; 
			}
		} else {
			sender.addChatMessage(new ChatComponentText("Unable to teleport non-player entity.")); 
		}
	} 

	@Override 
	public boolean canCommandSenderUseCommand(ICommandSender sender) 
	{ 
		if(!ExtendedPlayer.get((EntityPlayer)sender).isAdmin())
			return false;
		return true;
	} 

	@Override  
	public List<String> addTabCompletionOptions(ICommandSender var1, String[] var2) 
	{ 
		return null; 
	} 

	@Override 
	public boolean isUsernameIndex(String[] var1, int var2) 
	{ 
		return false;
	} 
}

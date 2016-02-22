package com.contained.game.util;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import com.contained.game.Contained;
import com.contained.game.world.GenerateWorld;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.util.ChatComponentText;

public class Debug implements ICommand{
	private final List<String> aliases;

	protected String fullEntityName; 
	protected Entity conjuredEntity; 

	public Debug() 
	{ 
		aliases = new ArrayList<String>(); 
		aliases.add("fwgdebug"); 
	} 

	@Override 
	public int compareTo(Object o)
	{ 
		return 0; 
	} 

	@Override 
	public String getCommandName() 
	{ 
		return "fwgdebug"; 
	} 

	@Override         
	public String getCommandUsage(ICommandSender var1) 
	{ 
		return "/fwgdebug"; 
	} 

	@Override 
	public List<String> getCommandAliases() { 
		return this.aliases;
	} 

	@Override 
	public void processCommand(ICommandSender sender, String[] argString)
	{ 
		Point p = new Point(sender.getPlayerCoordinates().posX/16, 
							sender.getPlayerCoordinates().posZ/16);
		
		String out = "";
		for(int i=0; i<GenerateWorld.oreSpawnProperties.length; i++) {
			if (GenerateWorld.oreSpawnProperties[i].spawnChunks.contains(p)) {
				if (out.equals(""))
					out += GenerateWorld.oreSpawnProperties[i].type.getLocalizedName();
				else
					out += ", "+GenerateWorld.oreSpawnProperties[i].type.getLocalizedName();
			}
		}
		
		if (out.equals(""))
			sender.addChatMessage(new ChatComponentText("None"));
		else
			sender.addChatMessage(new ChatComponentText(out));
	} 

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

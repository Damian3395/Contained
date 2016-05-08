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
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;

/**
 * Teleport to a specific coordinate in a specific dimension.
 */
public class CommandTPXD implements ICommand {
    private final List<String> aliases;
    
    protected String fullEntityName; 
    protected Entity conjuredEntity; 
  
    public CommandTPXD() 
    { 
        aliases = new ArrayList<String>(); 
        aliases.add("tpxd"); 
        aliases.add("tpdd"); 
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
        return "tpxd"; 
    } 

    @Override         
    public String getCommandUsage(ICommandSender var1) 
    { 
        return "/tpxd [player] <dim> <x> <y> <z>"; 
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
    
        if (!world.isRemote && (sender instanceof EntityPlayerMP || argString.length >= 5)) 
        { 
            if(argString.length < 4) 
            { 
                sender.addChatMessage(new ChatComponentText("Usage "+getCommandUsage(sender))); 
                return; 
            } 
            
            try {
	            EntityPlayerMP player = null;
	            if (argString.length >= 5) {
	            	if (argString[0].equals("@p")) {
	            		ChunkCoordinates pos = sender.getPlayerCoordinates();
	            		player = (EntityPlayerMP)world.getClosestPlayer(pos.posX, pos.posY, pos.posZ, -1);
	            	}
	            	else {
	            		player = (EntityPlayerMP)world.getPlayerEntityByName(argString[0]);
	            	}
	            } else
	            	player = (EntityPlayerMP)sender;
	            
	            int dimID = Integer.parseInt(argString[0+(1-(5-Math.min(5,argString.length)))]);
	            int x = Integer.parseInt(argString[1+(1-(5-Math.min(5,argString.length)))]);
	            int y = Integer.parseInt(argString[2+(1-(5-Math.min(5,argString.length)))]);
	            int z = Integer.parseInt(argString[3+(1-(5-Math.min(5,argString.length)))]);
	     
				if (player != null && player.ridingEntity == null && player.riddenByEntity == null) {
					if(player.dimension != dimID)
						Util.travelToDimension(dimID, player, false);
					player.setPositionAndUpdate(x, y, z);
				} else
					sender.addChatMessage(new ChatComponentText("Teleport failed.")); 
            } catch (NumberFormatException e) {
            	sender.addChatMessage(new ChatComponentText("Usage "+getCommandUsage(sender))); 
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
    public List<String> addTabCompletionOptions(ICommandSender var1, String[] var2) { 
        return null; 
    } 

    @Override 
    public boolean isUsernameIndex(String[] var1, int var2) { 
        return false;
    } 
}

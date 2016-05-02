package com.contained.game.commands;

import java.util.ArrayList;
import java.util.List;

import com.contained.game.entity.ExtendedPlayer;
import com.contained.game.util.MiniGameUtil;
import com.contained.game.util.Resources;
import com.contained.game.util.Util;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;

public class CommandStartTreasureHunt implements ICommand {
	private final List<String> aliases;

	@Override
	public String getCommandName() {
		return "starttreasurehunt";
	}

	public CommandStartTreasureHunt() {
		aliases = new ArrayList<String>();
		aliases.add(getCommandName());
	}

	@Override
	public void processCommand(ICommandSender sender, String[] argString) {
		if (!sender.getEntityWorld().isRemote) {
			String out = "";
			if(!ExtendedPlayer.get((EntityPlayer)sender).isAdmin()){
				out = "You are not an Admin.";
			}else{
				if(argString.length != 1){
					out = this.getCommandUsage(sender);
				}else{
					try{
						//Check Valid Treasure Dimension
						int dim = Integer.parseInt(argString[0]);
						if(!MiniGameUtil.isTreasure(dim)){
							Util.displayMessage((EntityPlayer)sender, Util.errorCode + "Invalid Dimension Value. Please Enter Number Between [" + Resources.MIN_TREASURE_DIMID + "-" + Resources.MAX_TREASURE_DIMID + "]");
							return;
						}
						
						//Check If Game Exists In That Dimension
						if(!MiniGameUtil.isDimensionInactive(dim)){
							Util.displayMessage((EntityPlayer)sender, Util.errorCode + "Active Mini Game In That Dimension");
							return;
						}
						
						Util.displayMessage((EntityPlayer)sender, Util.successCode + "Creating Treasure Hunt Game in Dimension " + dim);
						
						//Create & Sync MiniGame
						MiniGameUtil.testStartGame(dim, (EntityPlayerMP)sender);
					} catch (Exception e){
						e.printStackTrace();
						out = this.getCommandUsage(sender);
					}
				}
			}
			
			if (!out.equals("")) {
				sender.addChatMessage(new ChatComponentText(out));
			}
		}
	}

	@Override
	public String getCommandUsage(ICommandSender var1) {
		return "/" + getCommandName() + " <dimension>";
	}

	@Override 
	public int compareTo(Object o) { 
		if (o instanceof ICommand)
			return this.getCommandName().compareTo(((ICommand)o).getCommandName());
		return 0; 
	} 

	@Override
	public List<String> getCommandAliases() {
		return this.aliases;
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

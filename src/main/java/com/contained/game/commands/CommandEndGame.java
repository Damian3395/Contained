package com.contained.game.commands;

import java.util.ArrayList;
import java.util.List;

import com.contained.game.entity.ExtendedPlayer;
import com.contained.game.user.PlayerMiniGame;
import com.contained.game.util.MiniGameUtil;
import com.contained.game.util.Util;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;

public class CommandEndGame implements ICommand{
	private final List<String> aliases;

	@Override
	public String getCommandName() {
		return "endgame";
	}

	public CommandEndGame() {
		aliases = new ArrayList<String>();
		aliases.add(getCommandName());
	}

	@Override
	public void processCommand(ICommandSender sender, String[] argString) {
		if (!sender.getEntityWorld().isRemote) {
			String out = "";
			if(!ExtendedPlayer.get((EntityPlayer)sender).isAdmin())
				out = "You are not an Admin.";
			else{
				if(argString.length != 0)
					out = this.getCommandUsage(sender);
				else{
					try{
						EntityPlayer player = (EntityPlayer) sender;
						ExtendedPlayer properties = ExtendedPlayer.get(player);
						
						//Check If Player Is In MiniGame
						int dim = player.dimension;
						if(!MiniGameUtil.isPvP(dim) && !MiniGameUtil.isTreasure(dim)
								&& !properties.inGame()){
							Util.displayMessage((EntityPlayer)sender, Util.errorCode + "You Are Not In A Mini Game!");
							return;
						}
						
						Util.displayMessage((EntityPlayer)sender, Util.infoCode + "Returning Player To Lobby");
						
						PlayerMiniGame.get(dim).endGame();
					} catch (Exception e){
						e.printStackTrace();
						out = this.getCommandUsage(sender);
					}
					
				}
			}
			
			if (!out.equals(""))
				sender.addChatMessage(new ChatComponentText(out));
		}
	}

	@Override
	public String getCommandUsage(ICommandSender var1) {
		return "/" + getCommandName();
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

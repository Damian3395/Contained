package com.contained.game.commands;

import java.util.ArrayList;
import java.util.List;

import com.contained.game.entity.ExtendedPlayer;
import com.contained.game.handler.games.TreasureEvents;
import com.contained.game.minigames.TreasureChestGenerator;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.common.MinecraftForge;

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
						TreasureChestGenerator tcg = new TreasureChestGenerator(sender.getEntityWorld());
						tcg.generateChest(Integer.parseInt(argString[0]));
						MinecraftForge.EVENT_BUS.register(new TreasureEvents());
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
		return "/" + getCommandName() + " <chest_amount>";
	}

	@Override
	public int compareTo(Object o) {
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

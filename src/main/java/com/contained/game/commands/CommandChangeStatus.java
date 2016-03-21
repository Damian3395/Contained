package com.contained.game.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.ChatComponentText;

public class CommandChangeStatus implements ICommand {
	private final List<String> aliases;

	@Override
	public String getCommandName() {
		return "changestatus";
	}

	public CommandChangeStatus() {
		aliases = new ArrayList<String>();
		aliases.add(getCommandName());
	}

	@Override
	public void processCommand(ICommandSender sender, String[] argString) {
		if (!sender.getEntityWorld().isRemote) {
			String out = "";

			if (argString.length != 3) { // <player name> <-option> <percentage>
				out = getCommandUsage(sender);
			} else {
				switch (argString[1]) {
				case "-health": { // case1: change health

					try {
						EntityPlayer np = sender.getEntityWorld().getPlayerEntityByName(argString[0]);
						float percentage = Float.parseFloat(argString[2])/100;
						if(percentage>1 || percentage <=0){
							throw new NumberFormatException();
						}else{
							np.setHealth(np.getMaxHealth()*percentage);
						}
					} catch (NullPointerException e) {
						out = "Player: " + argString[0] + " does not exist.";
					} catch (NumberFormatException e) {
						out = "System: The last parameter for command changestatus must be a number from 1 to 100";
					}

				}
					break;
				case "-hunger": { // case2: change hunger

					try {
						
						EntityPlayer np = sender.getEntityWorld().getPlayerEntityByName(argString[0]);
						System.out.println("food level: "+np.getFoodStats().getFoodLevel());
						float percentage = Float.parseFloat(argString[2])/100;
						if(percentage>1 || percentage <=0){
							throw new NumberFormatException();
						}else{
							np.getFoodStats().setFoodLevel((int)(percentage*20));
							System.out.println("food level: "+np.getFoodStats().getFoodLevel());
						}
					} catch (NullPointerException e) {
						out = "Player: " + argString[0] + " does not exist.";
					} catch (NumberFormatException e) {
						out = "System: The last parameter for command changestatus must be a number from 1 to 100";
					}

				}
					break;
				
				default:
					out = this.getCommandUsage(sender);
				}

			}
			if (!out.equals("")) {
				sender.addChatMessage(new ChatComponentText(out));
			}
		}
	}

	@Override
	public String getCommandUsage(ICommandSender var1) {
		return "/" + getCommandName() + " <playername> <-option> <percentage(1-100)>";
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

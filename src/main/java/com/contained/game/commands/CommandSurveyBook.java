package com.contained.game.commands;

import java.util.ArrayList;
import java.util.List;

import com.contained.game.ContainedRegistry;
import com.contained.game.util.Util;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;

public class CommandSurveyBook implements ICommand{
	private List aliases;
	public CommandSurveyBook(){
		this.aliases = new ArrayList();
		this.aliases.add("survey");
		this.aliases.add("surveybook");
	}
	
	@Override
	public String getCommandName(){
		return "survey";
	}
	
	@Override
	public String getCommandUsage(ICommandSender sender){
		return "/survey";
	}
	
	@Override
	public List getCommandAliases(){
		return this.aliases;
	}
	
	@Override
	public void processCommand(ICommandSender sender, String[] astring){
		if (!sender.getEntityWorld().isRemote && sender instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) sender;
			int emptySlot = player.inventory.getFirstEmptyStack();
			if(emptySlot >= 0 && !player.inventory.hasItemStack(new ItemStack(ContainedRegistry.surveyItem.instance)))
				player.inventory.addItemStackToInventory(new ItemStack(ContainedRegistry.surveyItem.instance, 1));
			else
				sender.addChatMessage(new ChatComponentText(Util.errorCode + "Error: Already Have Item Or No Inventory Space Available"));
		}
	}

	@Override
	public int compareTo(Object o) {
		return 0;
	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender) {
		return true;
	}

	@Override
	public List addTabCompletionOptions(ICommandSender sender, String[] astring) {
		return null;
	}

	@Override
	public boolean isUsernameIndex(String[] astring, int i) {
		return false;
	}
}

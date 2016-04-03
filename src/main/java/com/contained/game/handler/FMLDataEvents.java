package com.contained.game.handler;

import com.contained.game.data.DataLogger;
import com.contained.game.util.Util;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.ItemCraftedEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.ItemSmeltedEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;

public class FMLDataEvents {
	/*
	 * Not Logging Data
	 */
	@SubscribeEvent
	public void onLeftServer(PlayerLoggedOutEvent event){
		if(!event.player.worldObj.isRemote){
			DataLogger.insertLogOut(Util.getServerID(), 
					event.player.getDisplayName(), 
					event.player.worldObj.provider.getDimensionName(),
					Util.getDate());
			System.out.println("Inserting Logout: " + event.player.getDisplayName());
		}
	}

	@SubscribeEvent
	public void onSmeltItem(ItemSmeltedEvent event){
		if(!event.player.worldObj.isRemote){
			DataLogger.insertSmelt(Util.getServerID(), 
					event.player.getDisplayName(), 
					event.player.worldObj.provider.getDimensionName(),
					event.smelting.getDisplayName(), 
					Util.getDate());
			System.out.println("Inserting Smelt " + event.smelting.getDisplayName());
		}
	}

	@SubscribeEvent
	public void onCraftItem(ItemCraftedEvent event){
		if(!event.player.worldObj.isRemote){
			DataLogger.insertCraft(Util.getServerID(), 
					event.player.getDisplayName(), 
					event.player.worldObj.provider.getDimensionName(),
					event.crafting.getDisplayName(), 
					Util.getDate());
			System.out.println("Inserting Craft " + event.crafting.getDisplayName());
		}
	}
}

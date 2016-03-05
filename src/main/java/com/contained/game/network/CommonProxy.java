package com.contained.game.network;

import java.util.HashMap;
import java.util.Map;

import com.contained.game.Contained;
import com.contained.game.ui.GuiHandler;
import com.contained.game.util.Resources;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

/*
 * Server Side Handlers
 */
public class CommonProxy {
	private static final Map<String, NBTTagCompound> extendedEntityData = new HashMap<String, NBTTagCompound>();
	
	public void registerRenderers(Contained ins) {
		
	}
	
	public void init(FMLInitializationEvent event) {
		Contained.channel.register(new ServerPacketHandler());
	}

	/**
	 * Adds an entity's custom data to the map for temporary storage
	 * @param compound An NBT Tag Compound that stores the IExtendedEntityProperties data only
	 */
	public static void storeEntityData(String name, NBTTagCompound compound)
	{
		extendedEntityData.put(name, compound);
	}

	/**
	 * Removes the compound from the map and returns the NBT tag stored for name or null if none exists
	 */
	public static NBTTagCompound getEntityData(String name)
	{
		return extendedEntityData.remove(name);
	}
}

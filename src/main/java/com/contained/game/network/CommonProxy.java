package com.contained.game.network;

import java.util.HashMap;
import java.util.Map;

import com.contained.game.Contained;
import com.contained.game.util.Resources;

import codechicken.lib.packet.PacketCustom;
import net.minecraft.nbt.NBTTagCompound;

/*
 * Server Side Handlers
 */
public class CommonProxy {
	private static final Map<String, NBTTagCompound> extendedEntityData = new HashMap<String, NBTTagCompound>();

	public void registerRenderers(Contained ins) {
		PacketCustom.assignHandler(Resources.MOD_ID, new ServerPacketHandler());
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

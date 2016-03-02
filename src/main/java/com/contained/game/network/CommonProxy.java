package com.contained.game.network;

import java.util.HashMap;
import java.util.Map;

import com.contained.game.Contained;
import com.contained.game.util.Resources;

import codechicken.lib.packet.PacketCustom;
import cpw.mods.fml.common.network.IGuiHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

/*
 * Server Side Handlers
 */
public class CommonProxy implements IGuiHandler {
	private static final Map<String, NBTTagCompound> extendedEntityData = new HashMap<String, NBTTagCompound>();
	private static HashMap<Integer, IGuiHandler> serverGuiHandlers = new HashMap<Integer, IGuiHandler>();
	private static HashMap<Integer, IGuiHandler> clientGuiHandlers = new HashMap<Integer, IGuiHandler>();
	
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

	@Override
	public Object getClientGuiElement(int id, EntityPlayer p, World w, int x, int y, int z) {
        IGuiHandler handler = clientGuiHandlers.get(id);
        if (handler != null)
            return handler.getClientGuiElement(id, p, w, x, y, z);
        return null;
	}

	@Override
	public Object getServerGuiElement(int id, EntityPlayer p, World w, int x, int y, int z) {
        IGuiHandler handler = serverGuiHandlers.get(id);
        if (handler != null)
            return handler.getServerGuiElement(id, p, w, x, y, z);
        return null;
	}
}

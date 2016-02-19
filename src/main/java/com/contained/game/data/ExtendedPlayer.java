package com.contained.game.data;

import com.contained.game.util.Data;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;

public class ExtendedPlayer implements IExtendedEntityProperties {
	private final static String EXT_PROP_NAME = "ExtendedPlayer";
	private final EntityPlayer entity;
	private int[] occupationValues = null;
	public int usedOwnItems = 0;	//# of times player used an item they owned themselves.
	public int usedOthersItems = 0; //# of times player used an item owned by someone else.
	public int usedByOthers = 0;    //# of times another player used an item owned by this player.
	
	public ExtendedPlayer(EntityPlayer entity) {
		this.entity = entity;
	}
	
	public static final void register(EntityPlayer entity) {
		entity.registerExtendedProperties(ExtendedPlayer.EXT_PROP_NAME, new ExtendedPlayer(entity));
	}
	
	public static final ExtendedPlayer get(EntityPlayer entity) {
		return (ExtendedPlayer)entity.getExtendedProperties(EXT_PROP_NAME);
	}
	
	public void increaseOccupation(int occID, int amount) {
		getOccupationValues()[occID] += amount;
	}
	
	public void setOccupation(int occID, int val) {
		getOccupationValues()[occID] = val;
	}
	
	public int getOccupationValue(int occID) {
		return getOccupationValues()[occID];
	}
	
	public int[] getOccupationValues() {
		if (this.occupationValues == null) {
			this.occupationValues = new int[Data.occupationNames.length];
			Data.zero(this.occupationValues);
		}
		return this.occupationValues;
	}
	
	@Override
	public void init(Entity arg0, World arg1) {	
	}

	@Override
	public void loadNBTData(NBTTagCompound arg0) {
		this.occupationValues = arg0.getIntArray("occupationValues");
		this.usedOwnItems = arg0.getInteger("usedOwnItems");
		this.usedOthersItems = arg0.getInteger("usedOthersItems");
		this.usedByOthers = arg0.getInteger("usedByOthers");
	}

	@Override
	public void saveNBTData(NBTTagCompound arg0) {
		arg0.setIntArray("occupationValues", getOccupationValues());
		arg0.setInteger("usedOwnItems", this.usedOwnItems);
		arg0.setInteger("usedOthersItems", this.usedOthersItems);
		arg0.setInteger("usedByOthers", this.usedByOthers);
	}
}

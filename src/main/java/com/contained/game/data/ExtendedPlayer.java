package com.contained.game.data;

import com.contained.game.util.Data;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;

public class ExtendedPlayer implements IExtendedEntityProperties {
	private final static String EXT_PROP_NAME = "ExtendedPlayer";
	private final EntityPlayer entity;
	private int[] occupationValues = null;
	private int occupationClass = -1;
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
	
	public int getOccupationClass(){
		return this.occupationClass;
	}
	
	public void setOccupationClass(int occupation){
		this.occupationClass = occupation;
	}
	
	@Override
	public void init(Entity entity, World w) {	
		if(this.getOccupationClass() == 9)
			this.entity.capabilities.setPlayerWalkSpeed(0.2f);
	}

	@Override
	public void loadNBTData(NBTTagCompound load) {
		this.occupationValues = load.getIntArray("occupationValues");
		this.occupationClass = load.getInteger("occupationClass");
		this.usedOwnItems = load.getInteger("usedOwnItems");
		this.usedOthersItems = load.getInteger("usedOthersItems");
		this.usedByOthers = load.getInteger("usedByOthers");
	}

	@Override
	public void saveNBTData(NBTTagCompound save) {
		save.setIntArray("occupationValues", getOccupationValues());
		save.setInteger("occupationClass", getOccupationClass());
		save.setInteger("usedOwnItems", this.usedOwnItems);
		save.setInteger("usedOthersItems", this.usedOthersItems);
		save.setInteger("usedByOthers", this.usedByOthers);
	}
}

package com.contained.game.entity;

import java.util.ArrayList;

import com.contained.game.data.Data;
import com.contained.game.ui.ClassPerks;
import com.contained.game.ui.GuiGuild;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;
import net.minecraftforge.common.util.Constants;

public class ExtendedPlayer implements IExtendedEntityProperties {
	private final static String EXT_PROP_NAME = "ExtendedPlayer";
	private final EntityPlayer entity;
	private int[] occupationValues = null;
	public int occupationClass = ClassPerks.NONE;
	public int occupationLevel = 0;
	public ArrayList<String> perks = new ArrayList<String>();
	public ArrayList<String> achievements = new ArrayList<String>();
	public int usedOwnItems = 0;	//# of times player used an item they owned themselves.
	public int usedOthersItems = 0; //# of times player used an item owned by someone else.
	public int usedByOthers = 0;    //# of times another player used an item owned by this player.
	public int posX = 0;
	public int posY = 0;
	public int posZ = 0;
	public String world = "";
	public int guild = GuiGuild.LONER;
	
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
	
	public ArrayList<String> getAchievement(){
		return this.achievements;
	}
	
	public void setAchievement(int index, String val){
		this.achievements.set(index, val);
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
		this.occupationLevel = load.getInteger("occupationLevel");
		
		this.usedOwnItems = load.getInteger("usedOwnItems");
		this.usedOthersItems = load.getInteger("usedOthersItems");
		this.usedByOthers = load.getInteger("usedByOthers");
		
		this.achievements.clear();
		NBTTagList list = load.getTagList("achievements", Constants.NBT.TAG_COMPOUND);
		for(int i = 0; i < list.tagCount(); i++){
			NBTTagCompound nbt = list.getCompoundTagAt(i);
			this.achievements.add(nbt.getString(Integer.toString(i)));
		}
		
		this.perks.clear();
		list = load.getTagList("perks", Constants.NBT.TAG_COMPOUND);
		for(int i = 0; i < list.tagCount(); i++){
			NBTTagCompound nbt = list.getCompoundTagAt(i);
			this.perks.add(nbt.getString(Integer.toString(i)));
		}
		
		this.posX = load.getInteger("posX");
		this.posY = load.getInteger("posY");
		this.posZ = load.getInteger("posZ");
		
		this.guild = load.getInteger("guild");
		System.out.println("Load Guild Status " + this.guild);
	}

	@Override
	public void saveNBTData(NBTTagCompound save) {
		save.setIntArray("occupationValues", getOccupationValues());
		save.setInteger("occupationClass", getOccupationClass());
		save.setInteger("occupationLevel", this.occupationLevel);
		
		save.setInteger("usedOwnItems", this.usedOwnItems);
		save.setInteger("usedOthersItems", this.usedOthersItems);
		save.setInteger("usedByOthers", this.usedByOthers);
		
		NBTTagList list = new NBTTagList();
		for(int i = 0;i < this.achievements.size(); i++){
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setString(Integer.toString(i), this.achievements.get(i));
			list.appendTag(nbt);
		}
		save.setTag("achievements", list);
		
		list = new NBTTagList();
		for(int i = 0; i < this.perks.size(); i++){
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setString(Integer.toString(i), this.perks.get(i));
			list.appendTag(nbt);
		}
		save.setTag("perks", list);
		
		save.setInteger("posX", this.posX);
		save.setInteger("posY", this.posY);
		save.setInteger("posZ", this.posZ);
		
		save.setInteger("guild", this.guild);
		System.out.println("Save Guild Status " + this.guild);
	}
}

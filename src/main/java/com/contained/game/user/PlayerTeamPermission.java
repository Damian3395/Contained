package com.contained.game.user;

import net.minecraft.nbt.NBTTagCompound;

/**
 * Defines aspects of interaction within a team territory that should be
 * enabled/disabled for members of a different team.
 */
public class PlayerTeamPermission {

	public boolean buildDisable;     //Cannot place blocks.
	public boolean breakDisable;     //Cannot break blocks.
	public boolean bucketDisable;    //Cannot scoop liquids.
	public boolean animalDisable;    //Cannot kill passive entities.
	public boolean mobDisable;       //Cannot kill hostile entities.
	public boolean chestDisable;     //Cannot open chests.
	public boolean containerDisable; //Cannot open other containers. (furnace, dispenser, anvil, etc)
	public boolean harvestDisable;   //Cannot right-click harvest crops.
	public boolean itemDisable;      //Cannot pick up items on the ground.
	public boolean interactDisable;  //Cannot interact with entities.
	
	public PlayerTeamPermission() {
		setDefault();
	}
	
	public PlayerTeamPermission(NBTTagCompound data) {
		readFromNBT(data);
	}
	
	//Default permissions.
	public void setDefault() {
		this.buildDisable = true;
		this.breakDisable = true;
		this.bucketDisable = true;
		this.animalDisable = true;
		this.mobDisable = false;
		this.chestDisable = true;
		this.containerDisable = true;
		this.harvestDisable = true;
		this.itemDisable = true;
		this.interactDisable = true;		
	}
	
	//Enable all interactions.
	public void setAllowAll() {
		this.buildDisable = false;
		this.breakDisable = false;
		this.bucketDisable = false;
		this.animalDisable = false;
		this.mobDisable = false;
		this.chestDisable = false;
		this.containerDisable = false;
		this.harvestDisable = false;
		this.itemDisable = false;
		this.interactDisable = false;
	}
	
	public void writeToNBT(NBTTagCompound ntc) {
		ntc.setBoolean("buildDisable", this.buildDisable);
		ntc.setBoolean("breakDisable", this.breakDisable);
		ntc.setBoolean("bucketDisable", this.bucketDisable);
		ntc.setBoolean("animalDisable", this.animalDisable);
		ntc.setBoolean("mobDisable", this.mobDisable);
		ntc.setBoolean("chestDisable", this.chestDisable);
		ntc.setBoolean("containerDisable", this.containerDisable);
		ntc.setBoolean("harvestDisable", this.harvestDisable);
		ntc.setBoolean("itemDisable", this.itemDisable);
		ntc.setBoolean("interactDisable", this.interactDisable);
	}
	
	public void readFromNBT(NBTTagCompound ntc) {
		this.buildDisable = ntc.getBoolean("buildDisable");
		this.breakDisable = ntc.getBoolean("breakDisable");
		this.bucketDisable = ntc.getBoolean("bucketDisable");
		this.animalDisable = ntc.getBoolean("animalDisable");
		this.mobDisable = ntc.getBoolean("mobDisable");
		this.chestDisable = ntc.getBoolean("chestDisable");
		this.containerDisable = ntc.getBoolean("containerDisable");
		this.harvestDisable = ntc.getBoolean("harvestDisable");
		this.itemDisable = ntc.getBoolean("itemDisable");
		this.interactDisable = ntc.getBoolean("interactDisable");
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof PlayerTeamPermission) {
			PlayerTeamPermission perm = (PlayerTeamPermission)o;
			if (this.buildDisable == perm.buildDisable
					&& this.breakDisable == perm.breakDisable
					&& this.bucketDisable == perm.bucketDisable
					&& this.animalDisable == perm.animalDisable
					&& this.mobDisable == perm.mobDisable
					&& this.chestDisable == perm.chestDisable
					&& this.containerDisable == perm.containerDisable
					&& this.harvestDisable == perm.harvestDisable
					&& this.itemDisable == perm.itemDisable
					&& this.interactDisable == perm.interactDisable) 
			{
				return true;
			}
		}
		return false;
	}
	
}
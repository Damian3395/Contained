package com.contained.main.entity;

import net.minecraft.entity.Entity;

public interface CEntity<T extends Entity> {
	/*
	 * @return the entity precise location x,y,z
	 */
	public double getXPos();
	public double getYPos();
	public double getZPos();
	
	/*
	 * @param set the entity precise location x,y,z 
	 */
	public void setXPos(double x);
	public void setYPos(double y);
	public void setZPos(double z);
	public void setPosition(double x, double y, double z);
	
	/*
	 * @return the entity block location x,y,z
	 */
	public int getBlockX();
	public int getBlockY();
	public int getBlockZ();
	
	/*
	 * @return the rotation of the entity
	 */
	public float getRotation();
	
	/*
	 * @param rotation Rotate the entity
	 */
	public void setRotaton(float rotation);
	
	/*
	 * Get and Set NPC mount
	 */
	public CEntity getMount();
	public CEntity getRider();
	public void setMount(CEntity entity);
	public void setRider(CEntity entity);
	
	/*
	 * @return Current State of entity
	 */
	public boolean isSneaking();
	public boolean isSprinting();
	public boolean isMounted();
	public boolean inWater();
	public boolean inFire();
	public boolean inLava();
	public boolean isAlive();
	public boolean isBurning();
	
	/*
	 * @return Age of entity in ticks
	 */
	public long getAge();
	
	/*
	 * Removes entity from world
	 */
	public void despawn();
	
	/*
	 * @param ticks Amount of world ticks entity will burn
	 * 20 ticks = 1 second
	 */
	public void setBurning(int ticks);
	
	/*
	 * Removes fire from entity
	 */
	public void extinguish();
	
	public String getTypeName();
	public int getType();
	public boolean typeOf(int type);
	
	public T getMCEntity();
	
	public String getUUID();
}

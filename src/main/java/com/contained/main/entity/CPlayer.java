package com.contained.main.entity;

import net.minecraft.entity.player.EntityPlayerMP;

public interface CPlayer<T extends EntityPlayerMP> extends CEntityLivingBase<T>{
	public String getName();
	public String getDisplayName();
	
	public void message(String message);
	
	public int getGamemode();
	public void setGamemode(int mode);
	
	public void setSpawnPoint(int x, int y, int z);
	public void resetSpawnPoint();
	
	public boolean hasAchievement(String achievement);
	public int getExpLevel();
	public void setExpLevel();
	public boolean hasPermission(String permission);
	
	@Override
	public T getMCEntity();
}

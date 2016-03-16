package com.contained.main.entity;

import net.minecraft.entity.EntityLivingBase;

public interface CEntityLivingBase<T extends EntityLivingBase> extends CEntity<T>{
	/*
	 * @return is the npc a child
	 */
	public boolean isChild();
	
	/*
	 * @return health stats of npc
	 */
	public float getHealth();
	public float getMaxHealth();
	
	/*
	 * @param health set the health stats of npc
	 */
	public void setHealth(float health);
	public void setMaxHealth(float health);
	
	/*
	 * npc combat stats
	 */
	public boolean isAttacking();
	public void setTarget(CEntityLivingBase entity);
	public CEntityLivingBase getTartget();
	public CEntityLivingBase getLastAttacked();
	
	/*
	 * @param entity
	 * @return Is the entity visible to npc 
	 */
	public boolean visibleEntity(CEntity entity);
	
	public void swingHand();
	
	@Override
	public T getMCEntity();
}

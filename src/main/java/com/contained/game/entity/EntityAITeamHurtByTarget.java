package com.contained.game.entity;

import java.util.ArrayList;
import java.util.HashMap;

import com.contained.game.util.EntityUtil;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAITarget;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Variant of EntityAIOwnerHurtByTarget for summoned monsters. Considers any player
 * of the same team to be the monster's owner.
 */
public class EntityAITeamHurtByTarget extends EntityAITarget {
	EntityCreature theDefendingTameable;
	EntityLivingBase theOwnerAttacker;
	private HashMap<String, Integer> lastRevenges;

	public EntityAITeamHurtByTarget(EntityCreature thePet)
	{
		super(thePet, false);
		this.theDefendingTameable = thePet;
		this.setMutexBits(1);
		lastRevenges = new HashMap<String, Integer>();
	}

	/**
	 * Returns whether the EntityAIBase should begin execution.
	 */
	public boolean shouldExecute()
	{
		ArrayList<EntityPlayer> playersToDefend = EntityUtil.getAllTeamPlayersInRange(this.theDefendingTameable, 15.0);
		String myTeam = ExtendedLivingBase.get(this.theDefendingTameable).getTeam();
		
		for(EntityPlayer p : playersToDefend) {
			EntityLivingBase possibleAttacker = p.getAITarget();
			boolean foundAttack = 
					( this.lastRevenges.containsKey(p.getDisplayName())
							&& p.func_142015_aE() != this.lastRevenges.get(p.getDisplayName()) 
							&& this.isSuitableTarget(possibleAttacker, false) 
							&& EntityUtil.shouldAttack(possibleAttacker, myTeam));
			if (foundAttack) {
				this.theOwnerAttacker = possibleAttacker;
				return true;
			}
		}
		return false;
	}

	/**
	 * Execute a one shot task or start executing a continuous task
	 */
	public void startExecuting()
	{
		this.taskOwner.setAttackTarget(this.theOwnerAttacker);
		ArrayList<EntityPlayer> playersToDefend = EntityUtil.getAllTeamPlayersInRange(this.theDefendingTameable, 15.0);

		for(EntityPlayer player : playersToDefend)
			this.lastRevenges.put(player.getDisplayName(), player.func_142015_aE());

		super.startExecuting();
	}
}

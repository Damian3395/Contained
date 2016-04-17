package com.contained.game.entity;

import java.util.ArrayList;
import java.util.HashMap;

import com.contained.game.util.EntityUtil;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAITarget;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Variant of EntityAIOwnerHurtTarget for summoned monsters. Considers any player
 * of the same team to be the monster's owner.
 */
public class EntityAITeamHurtTarget extends EntityAITarget {
    EntityCreature theEntityTameable;
    EntityLivingBase theTarget;
    private HashMap<String, Integer> lastAttackerTimes;

    public EntityAITeamHurtTarget(EntityCreature thePet)
    {
        super(thePet, false);
        this.theEntityTameable = thePet;
        this.lastAttackerTimes = new HashMap<String, Integer>();
        this.setMutexBits(1);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        ArrayList<EntityPlayer> playersToDefend = EntityUtil.getAllTeamPlayersInRange(this.theEntityTameable, 15.0);
        String myTeam = ExtendedLivingBase.get(this.theEntityTameable).getTeam();
        
        for (EntityPlayer p : playersToDefend) {
        	EntityLivingBase possibleTarget = p.getLastAttacker();
			boolean foundAttack = 
					( this.lastAttackerTimes.containsKey(p.getDisplayName())
							&& p.getLastAttackerTime() != this.lastAttackerTimes.get(p.getDisplayName()) 
							&& this.isSuitableTarget(possibleTarget, false) 
							&& EntityUtil.shouldAttack(possibleTarget, myTeam));
			if (foundAttack) {
				this.theTarget = possibleTarget;
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
        this.taskOwner.setAttackTarget(this.theTarget);
        ArrayList<EntityPlayer> playersToDefend = EntityUtil.getAllTeamPlayersInRange(this.theEntityTameable, 15.0);

        for(EntityPlayer player : playersToDefend)
			this.lastAttackerTimes.put(player.getDisplayName(), player.getLastAttackerTime());

        super.startExecuting();
    }
}

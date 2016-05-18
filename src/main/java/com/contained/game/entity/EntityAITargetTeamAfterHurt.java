package com.contained.game.entity;

import java.util.Iterator;
import java.util.List;

import com.contained.game.util.EntityUtil;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAITarget;
import net.minecraft.util.AxisAlignedBB;

/**
 * Variant of EntityAIHurtByTarget for summoned monsters. Don't target players
 * or monsters of the same team.
 */
public class EntityAITargetTeamAfterHurt extends EntityAITarget {
    boolean entityCallsForHelp;
    private int lastRevenge;

    public EntityAITargetTeamAfterHurt(EntityCreature src, boolean callsForHelp)
    {
        super(src, false);
        this.entityCallsForHelp = callsForHelp;
        this.setMutexBits(1);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        int i = this.taskOwner.func_142015_aE();
        EntityLivingBase attacker = this.taskOwner.getAITarget();
        String myTeam = ExtendedLivingBase.get(this.taskOwner).getTeam();
        
        return i != this.lastRevenge 
        		&& this.isSuitableTarget(attacker, false)
        		&& EntityUtil.shouldAttack(attacker, myTeam);
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
        this.taskOwner.setAttackTarget(this.taskOwner.getAITarget());
        this.lastRevenge = this.taskOwner.func_142015_aE();

        if (this.entityCallsForHelp)
        {
            double d0 = this.getTargetDistance();
            List<EntityCreature> list = this.taskOwner.worldObj.getEntitiesWithinAABB(EntityCreature.class, AxisAlignedBB.getBoundingBox(this.taskOwner.posX, this.taskOwner.posY, this.taskOwner.posZ, this.taskOwner.posX + 1.0D, this.taskOwner.posY + 1.0D, this.taskOwner.posZ + 1.0D).expand(d0, 10.0D, d0));
            Iterator<EntityCreature> iterator = list.iterator();
            String myTeam = ExtendedLivingBase.get(this.taskOwner).getTeam();
            
            while (iterator.hasNext()) {
                EntityCreature entitycreature = (EntityCreature)iterator.next();
                if (this.taskOwner != entitycreature 
                		&& entitycreature.getAttackTarget() == null 
                		&& !entitycreature.isOnSameTeam(this.taskOwner.getAITarget())
                		&& EntityUtil.shouldAttack(this.taskOwner.getAITarget(), myTeam))
                    entitycreature.setAttackTarget(this.taskOwner.getAITarget());
            }
        }

        super.startExecuting();
    }
}

package com.contained.game.entity;

import java.util.Collections;
import java.util.List;

import com.contained.game.util.EntityUtil;

import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAITarget;

/**
 * Variant of EntityAINearestAttackableTarget for summoned monsters. Only finds
 * nearest attackable target that is not in the same team as the monster.
 */
public class EntityAINearestTeamTarget extends EntityAITarget {
    private final Class targetClass;
    private final int targetChance;
    private final EntityAINearestAttackableTarget.Sorter theNearestAttackableTargetSorter;
    private final IEntitySelector targetEntitySelector;
    private EntityLivingBase targetEntity;

    public EntityAINearestTeamTarget(EntityCreature src, Class target, int chance, boolean lineOfSight)
    {
        this(src, target, chance, lineOfSight, false);
    }

    public EntityAINearestTeamTarget(EntityCreature src, Class target, int chance, boolean lineOfSight, boolean nearbyOnly)
    {
        this(src, target, chance, lineOfSight, nearbyOnly, (IEntitySelector)null);
    }

    public EntityAINearestTeamTarget(EntityCreature src, Class target, int chance, boolean lineOfSight, boolean nearbyOnly, final IEntitySelector filter)
    {
        super(src, lineOfSight, nearbyOnly);
        this.targetClass = target;
        this.targetChance = chance;
        this.theNearestAttackableTargetSorter = new EntityAINearestAttackableTarget.Sorter(src);
        this.setMutexBits(1);
        this.targetEntitySelector = new IEntitySelector() {
            public boolean isEntityApplicable(Entity filterSrc) {
                return !(filterSrc instanceof EntityLivingBase) ? false : (filter != null && !filter.isEntityApplicable(filterSrc) ? false : EntityAINearestTeamTarget.this.isSuitableTarget((EntityLivingBase)filterSrc, false));
            }
        };
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
    	String myTeam = ExtendedLivingBase.get(this.taskOwner).getTeam();
    	
        if (this.targetChance > 0 && this.taskOwner.getRNG().nextInt(this.targetChance) != 0) {
            return false;
        } else {
            double d0 = this.getTargetDistance();
            List<EntityLivingBase> nearbyEnts = this.taskOwner.worldObj.selectEntitiesWithinAABB(this.targetClass, this.taskOwner.boundingBox.expand(d0, 4.0D, d0), this.targetEntitySelector);
            Collections.sort(nearbyEnts, this.theNearestAttackableTargetSorter);

            if (nearbyEnts.isEmpty()) {
                return false;
            } else {
            	if (myTeam == null) {
            		this.targetEntity = nearbyEnts.get(0);
            		return true;
            	} else {
            		for(EntityLivingBase ent : nearbyEnts) {
            			if (!EntityUtil.isSameTeam(this.taskOwner, ent) && EntityUtil.isAttackableClass(ent)) {
            				this.targetEntity = ent;
            				return true;
            			}
            		}
            	}
            }
        }
        return false;
    }

    public void startExecuting() {
        this.taskOwner.setAttackTarget(this.targetEntity);
        super.startExecuting();
    }
}

package com.contained.game.entity;

import com.contained.game.util.EntityUtil;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

/**
 * Variant of EntityAIFollowOwner for summoned monsters. Considers any player
 * of the same team to be the monster's owner.
 */
public class EntityAIFollowTeam extends EntityAIBase {
	private EntityCreature thePet;
    private EntityLivingBase theOwner;
    World theWorld;
    private double followSpeed;
    private PathNavigate petPathfinder;
    private int updateTimer;
    float maxDist;
    float minDist;
    private boolean doesAvoidWater;
    private final EntityAINearestAttackableTarget.Sorter theNearestTargetSorter;
    
    public EntityAIFollowTeam(EntityCreature thePet, double followSpeed, float minFollowDist, float maxFollowDist)
    {
        this.thePet = thePet;
        this.theWorld = thePet.worldObj;
        this.followSpeed = followSpeed;
        this.petPathfinder = thePet.getNavigator();
        this.minDist = minFollowDist;
        this.maxDist = maxFollowDist;
        this.setMutexBits(3);
        this.theNearestTargetSorter = new EntityAINearestAttackableTarget.Sorter(thePet);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        EntityLivingBase entitylivingbase = EntityUtil.getNearestTeamPlayer(this.thePet, this.minDist*2, theNearestTargetSorter);

        if (entitylivingbase == null)
            return false;
        else if (isSitting())
            return false;
        else if (this.thePet.getDistanceSqToEntity(entitylivingbase) < (double)(this.minDist * this.minDist))
            return false;
        else {
            this.theOwner = entitylivingbase;
            return true;
        }
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean continueExecuting() {
        return !this.petPathfinder.noPath() && this.thePet.getDistanceSqToEntity(this.theOwner) > (double)(this.maxDist * this.maxDist) && !isSitting();
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting() {
        this.updateTimer = 0;
        this.doesAvoidWater = this.thePet.getNavigator().getAvoidsWater();
        this.thePet.getNavigator().setAvoidsWater(false);
    }

    /**
     * Resets the task
     */
    public void resetTask()
    {
        this.theOwner = null;
        this.petPathfinder.clearPathEntity();
        this.thePet.getNavigator().setAvoidsWater(this.doesAvoidWater);
    }

    /**
     * Updates the task
     */
    public void updateTask()
    {
        this.thePet.getLookHelper().setLookPositionWithEntity(this.theOwner, 10.0F, (float)this.thePet.getVerticalFaceSpeed());

        if (!isSitting())
        {
            if (--this.updateTimer <= 0)
            {
                this.updateTimer = 10;

                if (!this.petPathfinder.tryMoveToEntityLiving(this.theOwner, this.followSpeed))
                {
                    if (!this.thePet.getLeashed())
                    {
                        if (this.thePet.getDistanceSqToEntity(this.theOwner) >= 144.0D)
                        {
                            int i = MathHelper.floor_double(this.theOwner.posX) - 2;
                            int j = MathHelper.floor_double(this.theOwner.posZ) - 2;
                            int k = MathHelper.floor_double(this.theOwner.boundingBox.minY);

                            for (int l = 0; l <= 4; ++l)
                            {
                                for (int i1 = 0; i1 <= 4; ++i1)
                                {
                                    if ((l < 1 || i1 < 1 || l > 3 || i1 > 3) && World.doesBlockHaveSolidTopSurface(this.theWorld, i + l, k - 1, j + i1) && !this.theWorld.getBlock(i + l, k, j + i1).isNormalCube() && !this.theWorld.getBlock(i + l, k + 1, j + i1).isNormalCube())
                                    {
                                        this.thePet.setLocationAndAngles((double)((float)(i + l) + 0.5F), (double)k, (double)((float)(j + i1) + 0.5F), this.thePet.rotationYaw, this.thePet.rotationPitch);
                                        this.petPathfinder.clearPathEntity();
                                        return;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    private boolean isSitting() {
    	return this.thePet instanceof EntityTameable && ((EntityTameable)this.thePet).isSitting();
    }
}

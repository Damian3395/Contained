package com.contained.game.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;

import com.contained.game.entity.EntityAIFollowTeam;
import com.contained.game.entity.EntityAINearestTeamTarget;
import com.contained.game.entity.EntityAITargetTeamAfterHurt;
import com.contained.game.entity.EntityAITeamHurtByTarget;
import com.contained.game.entity.EntityAITeamHurtTarget;
import com.contained.game.entity.ExtendedLivingBase;
import com.contained.game.user.PlayerTeamIndividual;

public class EntityUtil {
	public static EntityPlayer getNearestTeamPlayer(EntityLivingBase src, double range, Comparator sortMethod) {
		return getNearestTeamPlayer(src, range, sortMethod, null);
	}
	
	public static EntityPlayer getNearestTeamPlayer(EntityLivingBase src, double range, Comparator sortMethod, IEntitySelector filter) {
		List<EntityPlayer> nearbyPlayers = src.worldObj.selectEntitiesWithinAABB(EntityPlayer.class, src.boundingBox.expand(range, 4.0D, range), filter);
        Collections.sort(nearbyPlayers, sortMethod);
        
        String myTeam = ExtendedLivingBase.get(src).getTeam();
        if (nearbyPlayers.isEmpty() || myTeam == null)
            return null;
        else {
    		for(EntityPlayer player : nearbyPlayers) {
				PlayerTeamIndividual pdata = PlayerTeamIndividual.get(player);
				if (pdata.teamID != null && pdata.teamID.equals(myTeam))
					return player;
    		}
        }        
        return null;
	}
	
	public static ArrayList<EntityPlayer> getAllTeamPlayersInRange(EntityLivingBase src, double range) {
		List<EntityPlayer> nearbyPlayers = src.worldObj.getEntitiesWithinAABB(EntityPlayer.class, src.boundingBox.expand(range, 4.0D, range));
		ArrayList<EntityPlayer> returnPlayers = new ArrayList<EntityPlayer>();
		
		String myTeam = ExtendedLivingBase.get(src).getTeam();
		if (myTeam == null)
			return returnPlayers;
		
		for(EntityPlayer player : nearbyPlayers) {
			PlayerTeamIndividual pdata = PlayerTeamIndividual.get(player);
			if (pdata.teamID != null && pdata.teamID.equals(myTeam))
				returnPlayers.add(player);
		}
		return returnPlayers;
	}
	
	//src - EntityCreature, target - EntityCreature or Player
	public static boolean isSameTeam(EntityLivingBase src, EntityLivingBase target) {
		String myTeam = ExtendedLivingBase.get(src).getTeam();
		return isSameTeam(myTeam, target);
	}
	
	public static boolean isSameTeam(String teamID, EntityLivingBase target) {
		if (teamID == null)
			return false;
		else {
			if (target instanceof EntityPlayer) {
				PlayerTeamIndividual pdata = PlayerTeamIndividual.get((EntityPlayer)target);
				if (pdata.teamID != null && pdata.teamID.equals(teamID))
					return true;
			} else {
				String otherTeam = ExtendedLivingBase.get(target).getTeam();
				if (otherTeam != null && otherTeam.equals(teamID))
					return true;
			}
		}
		return false;		
	}
	
	public static boolean shouldAttack(EntityLivingBase victim, String myTeam) {
		if (!(victim instanceof EntityCreeper) && !(victim instanceof EntityGhast)
				&& isAttackableClass(victim))
		{
			if (EntityUtil.isSameTeam(myTeam, victim))
				return false;
			return true;
		}
		return false;
	}
	
	public static boolean isAttackableClass(EntityLivingBase victim) {
		if ((victim instanceof EntityPlayer) || (victim instanceof EntityMob))
			return true;
		return false;
	}
	
	public static void applyTeamAI(EntityCreature ent) {
		ent.targetTasks.taskEntries.clear();
		if (ent instanceof EntityZombie) {
			ent.tasks.taskEntries.clear();
			ent.tasks.addTask(0, new EntityAISwimming(ent));
			ent.tasks.addTask(2, new EntityAIAttackOnCollide(ent, EntityLivingBase.class, 1.0D, false));
			ent.tasks.addTask(5, new EntityAIMoveTowardsRestriction(ent, 1.0D));
			ent.tasks.addTask(7, new EntityAIWander(ent, 1.0D));
			ent.tasks.addTask(8, new EntityAIWatchClosest(ent, EntityPlayer.class, 8.0F));
			ent.tasks.addTask(8, new EntityAILookIdle(ent));
		}
		else if (ent instanceof EntitySkeleton) {
			ent.tasks.taskEntries.clear();
			ent.tasks.addTask(1, new EntityAISwimming(ent));
			ent.tasks.addTask(7, new EntityAIWander(ent, 1.0D));
			ent.tasks.addTask(9, new EntityAIWatchClosest(ent, EntityPlayer.class, 8.0F));
			ent.tasks.addTask(9, new EntityAILookIdle(ent));	
			if (((EntitySkeleton)ent).getSkeletonType() == 0)
				ent.tasks.addTask(4, new EntityAIArrowAttack((EntitySkeleton)ent, 1.0D, 20, 60, 15.0F));
			else
				ent.tasks.addTask(4, new EntityAIAttackOnCollide(ent, EntityLivingBase.class, 1.2D, false));
		}
		
		//Default team tasks
		ent.tasks.addTask(5, new EntityAIFollowTeam(ent, 1.0D, 10.0F, 2.0F));
		ent.targetTasks.addTask(1, new EntityAITeamHurtByTarget(ent));
		ent.targetTasks.addTask(2, new EntityAITeamHurtTarget(ent));
		ent.targetTasks.addTask(2, new EntityAINearestTeamTarget(ent, EntityLivingBase.class, 0, true));
		ent.targetTasks.addTask(3, new EntityAITargetTeamAfterHurt(ent, true));
		
		ent.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.3D);
		if (!(ent instanceof EntityCreeper))
			ent.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(30D);
	}
}

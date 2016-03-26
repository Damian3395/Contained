package com.contained.game.entity;

import com.contained.game.util.Util;

import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.EntityRegistry;

/**
 * Wither Skeleton monster that spawns in the overworld at y:24 and deeper.
 */
public class DeepWitherSkeleton {

	public static int mobid = 0;
	public Object instance;

	public void preInit(FMLPreInitializationEvent event){
		int entityID = EntityRegistry.findGlobalUniqueEntityId();
		mobid = entityID;
		EntityRegistry.registerGlobalEntityID(DeepWitherSkeleton.EntityDeepWitherSkeleton.class, "deepWitherSkeleton", entityID);
		EntityRegistry.registerModEntity(DeepWitherSkeleton.EntityDeepWitherSkeleton.class, "deepWitherSkeleton", entityID, instance, 64, 1, true);
		EntityRegistry.addSpawn(DeepWitherSkeleton.EntityDeepWitherSkeleton.class, 100, 2, 4, EnumCreatureType.monster, Util.getBiomesArray());
	}

	public static class EntityDeepWitherSkeleton extends EntitySkeleton
	{
		public EntityDeepWitherSkeleton(World w) {
			super(w);
			setSkeletonType(1);
			this.setCurrentItemOrArmor(0, new ItemStack(Items.stone_sword));
		}
		
		@Override
		public float getBrightness(float f) {
			return 0.0F;
		}
		
		@Override
		public boolean getCanSpawnHere() {
			return this.posY <= 24;
		}
	}
}

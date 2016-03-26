package com.contained.game.entity;

import com.contained.game.util.Util;

import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.world.World;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.EntityRegistry;

/**
 * Blaze monster that spawns in the overworld at y:24 and deeper.
 */
public class DeepBlaze {

	public static int mobid = 0;
	public Object instance;

	public void preInit(FMLPreInitializationEvent event){
		int entityID = EntityRegistry.findGlobalUniqueEntityId();
		mobid = entityID;
		EntityRegistry.registerGlobalEntityID(DeepBlaze.EntityDeepBlaze.class, "deepBlaze", entityID);
		EntityRegistry.registerModEntity(DeepBlaze.EntityDeepBlaze.class, "deepBlaze", entityID, instance, 64, 1, true);
		EntityRegistry.addSpawn(DeepBlaze.EntityDeepBlaze.class, 100, 2, 4, EnumCreatureType.monster, Util.getBiomesArray());
	}

	public static class EntityDeepBlaze extends EntityBlaze
	{
		public EntityDeepBlaze(World w) {
			super(w);
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

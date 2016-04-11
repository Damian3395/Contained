package com.contained.game.entity;

import com.contained.game.util.Util;

import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.monster.EntityMagmaCube;
import net.minecraft.world.World;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.EntityRegistry;

/**
 * Magma Slime monster that spawns in the overworld at y:24 and deeper.
 */
public class DeepLavaSlime {

	public static int mobid = 0;
	public Object instance;

	public void preInit(FMLPreInitializationEvent event){
		int entityID = EntityRegistry.findGlobalUniqueEntityId();
		mobid = entityID;
		EntityRegistry.registerGlobalEntityID(DeepLavaSlime.EntityDeepLavaSlime.class, "deepLavaSlime", entityID);
		EntityRegistry.registerModEntity(DeepLavaSlime.EntityDeepLavaSlime.class, "deepLavaSlime", entityID, instance, 64, 1, true);
		EntityRegistry.addSpawn(DeepLavaSlime.EntityDeepLavaSlime.class, 50, 2, 4, EnumCreatureType.monster, Util.getBiomesArray());
	}

	public static class EntityDeepLavaSlime extends EntityMagmaCube
	{
		public EntityDeepLavaSlime(World w) {
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

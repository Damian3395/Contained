package com.contained.game.handler.perks;

import java.util.ArrayList;

import com.contained.game.entity.ExtendedPlayer;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class BuilderEvents {
	private final int GRASS = 1;
	private final int SAND = 19;
	private final int DIRT = 20;
	private final int SANDSTONE = 21;
	private final int SANDSTONECURVED = 22;
	private final int COBBLE = 23;
	private final int STONE = 24;
	private final int IRON = 25;
	private final int BRICK = 26;
	private final int STONEBRICK = 27;
	private final int OBSIDIAN = 28;
	private final int FURNACE = 29;
	private final int GOLD = 30;
	private final int GLASS = 31;
	private final int DIAMOND = 32;
	
	@SubscribeEvent
	public void entityDamage(LivingHurtEvent event){
		DamageSource source = event.source;
		float damage = event.ammount;
		
		if(event.entityLiving instanceof EntityPlayer){
			EntityPlayer player = (EntityPlayer) event.entityLiving;
			ExtendedPlayer properties = ExtendedPlayer.get(player);
			ArrayList<Integer> perks = properties.perks;
			
			if(source.isFireDamage() && perks.contains(SAND))
				damage -= damage * 0.1f;
			if(source.isMagicDamage() && perks.contains(SANDSTONE))
				damage -= damage * 0.1f;
			if(source.isProjectile() && perks.contains(IRON))
				damage -= damage * 0.1f;
			if(source.isExplosion() && perks.contains(STONEBRICK))
				damage -= damage * 0.1f;
		}
		
		event.ammount = damage;
	}
	
	@SubscribeEvent
	public void entityUpdate(LivingUpdateEvent event){
		if(event.entityLiving instanceof EntityPlayer){
			EntityPlayer player = (EntityPlayer) event.entityLiving;
			ExtendedPlayer properties = ExtendedPlayer.get(player);
			ArrayList<Integer> perks = properties.perks;
			
			if(perks.contains(GRASS))
				player.setAIMoveSpeed(0.3f);
		}
	}
	
	@SubscribeEvent
	public void entityFall(LivingFallEvent event){
		if(event.entityLiving instanceof EntityPlayer){
			EntityPlayer player = (EntityPlayer) event.entityLiving;
			ExtendedPlayer properties = ExtendedPlayer.get(player);
			ArrayList<Integer> perks = properties.perks;
			
			if(perks.contains(COBBLE))
				event.distance -= event.distance * 0.25f;
				
		}
	}
}

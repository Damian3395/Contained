package com.contained.game.handler.perks;

import java.util.ArrayList;

import com.contained.game.entity.ExtendedPlayer;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntityCaveSpider;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

public class WizardEvents {
	//Perks
	private final int BONE = 3;
	private final int BOOK = 47;
	private final int POTION = 48;
	private final int GUNPOWDER = 49;
	private final int CAULDRON = 50;
	private final int NEITHER_BRICK = 51;
	private final int POTION_STAND = 52;
	private final int MAGMA = 53;
	private final int CRYSTAL = 54;
	private final int GHAST = 55;
	private final int SPIDER = 56;
	private final int SLIME = 57;
	private final int FIRE = 58;
	private final int ENDER_EYE = 59;
	private final int ENDER_PEARL = 60;
	
	@SubscribeEvent
	public void entityDamage(LivingHurtEvent event){
		DamageSource source = event.source;
		float damage = event.ammount;
		
		if(event.entityLiving instanceof EntityPlayer){
			EntityPlayer player = (EntityPlayer) event.entityLiving;
			ExtendedPlayer properties = ExtendedPlayer.get(player);
			ArrayList<Integer> perks = properties.perks;
			
			if(perks.contains(BOOK) && source.isMagicDamage())
				damage -= (damage * 0.1f);
			if(source.isExplosion() && perks.contains(GUNPOWDER))
				damage -= (damage * 0.1f);
			if(source.isFireDamage() && perks.contains(MAGMA))
				damage -= (damage * 0.1f);
			if(source.getEntity() instanceof EntityPlayer){
				EntityLivingBase attacker = (EntityLivingBase) source.getEntity();
				if(attacker instanceof EntityGhast && perks.contains(GHAST))
					damage -= (damage * 0.1f);
				if(attacker instanceof EntitySkeleton && perks.contains(BONE))
					damage -= (damage * 0.1f);
				if(perks.contains(NEITHER_BRICK) && !player.isPotionActive(8195)) //Potion of Fire Resistance
					player.addPotionEffect(new PotionEffect(8195, 50));
				if(perks.contains(SLIME) && !player.isPotionActive(8203)) //Potion of Leaping
					player.addPotionEffect(new PotionEffect(8203, 50));
				if(perks.contains(ENDER_EYE) && !player.isPotionActive(8193)) //Potion of Luck
					player.addPotionEffect(new PotionEffect(8193, 50));
			}
		}else if(source.getEntity() instanceof EntityPlayer){
			EntityPlayer player = (EntityPlayer) source.getEntity();
			ExtendedPlayer properties = ExtendedPlayer.get(player);
			ArrayList<Integer> perks = properties.perks;
			
			if(event.entityLiving instanceof EntityWitch && perks.contains(CAULDRON))
				damage += (damage * 0.1f);
			if(event.entityLiving instanceof EntityBlaze && perks.contains(CRYSTAL))
				damage += (damage * 01.f);
			if((event.entityLiving instanceof EntityCaveSpider || event.entityLiving instanceof EntitySpider) && perks.contains(SPIDER))
				damage += (damage * 0.1f);
			if(event.entityLiving instanceof EntityPlayer){
				if(perks.contains(POTION) && !player.isPotionActive(8270)) //Potion of Swiftness
					player.addPotionEffect(new PotionEffect(8270, 50));
				if(perks.contains(POTION_STAND) && !player.isPotionActive(8201)) //Potion of Stength
					player.addPotionEffect(new PotionEffect(8201, 50));
				if(perks.contains(FIRE) && !player.isPotionActive(8206)) //Potion of Invisibilitiy
					player.addPotionEffect(new PotionEffect(8206, 50));
				if(perks.contains(ENDER_PEARL) && !player.isPotionActive(8198)) //Potion of Night Vision
					player.addPotionEffect(new PotionEffect(8198, 50));
			}
		}
		
		event.ammount = damage;
	}
}

package com.contained.game.handler.perks;

import java.util.ArrayList;

import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;

import com.contained.game.entity.ExtendedPlayer;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class CollectorEvents {
	private final int WOODEN_SHOVEL = 0;
	private final int STONE_PAXE = 5;
	private final int STONE_AXE = 6;
	private final int IRON_PAXE = 7;
	private final int IRON_AXE = 8;
	private final int GOLD_PAXE = 9;
	private final int GOLD_AXE = 10;
	private final int DIAMOND_PAXE = 11;
	private final int DIAMOND_SHOVEL = 12;
	private final int BUCKET = 13;
	private final int LADDER = 14;
	private final int DIAMOND_AXE = 15;
	private final int GOLD_SHOVEL = 16;
	private final int LAMP = 17;
	private final int TREE = 18;
	
	private final int FOREST_HILLS = 18;
	private final int FOREST = 4;
	
	@SubscribeEvent
	public void entityDamage(LivingHurtEvent event){
		DamageSource source = event.source;
		float damage = event.ammount;
		
		if(event.source.getSourceOfDamage() instanceof EntityPlayer){	
			EntityPlayer attacker = (EntityPlayer) source.getSourceOfDamage();
			ExtendedPlayer propertiesAttacker = ExtendedPlayer.get(attacker);
			ArrayList<Integer> attackerPerks = propertiesAttacker.perks;
			ItemStack weapon = attacker.getHeldItem();
			
			if(weapon != null && !attackerPerks.isEmpty()){
				if(attackerPerks.contains(DIAMOND_AXE) && weapon.getDisplayName().equals("Diamond Axe") && event.entityLiving instanceof EntityPlayer)
					damage += (damage * 0.1f);
				if(attackerPerks.contains(GOLD_SHOVEL) && weapon.getDisplayName().equals("Golden Shovel") && event.entityLiving instanceof EntityPlayer)
					damage += (damage * 0.1f);
				if(attackerPerks.contains(STONE_AXE) && weapon.getDisplayName().equals("Stone Axe") && event.entityLiving instanceof EntityMob)
					damage += (damage * 0.1f);
				if(attackerPerks.contains(GOLD_AXE) && weapon.getDisplayName().equals("Golden Axe") && event.entityLiving instanceof EntityMob)
					damage += (damage * 0.1f);
			}
				
			if(event.entityLiving instanceof EntityPlayer){
				EntityPlayer victim = (EntityPlayer) event.entityLiving;
				ExtendedPlayer propertiesVictim = ExtendedPlayer.get(victim);
				ArrayList<Integer> victimPerks =  propertiesVictim.perks;
				ItemStack item = victim.getHeldItem();
				
				if(item != null && !victimPerks.isEmpty()){
					if(victimPerks.contains(DIAMOND_SHOVEL) && item.getDisplayName().equals("Diamond Shovel"))
						damage -= (damage * 0.1f);
				}		
			}
			
			event.ammount = damage;
		}else if(event.entityLiving instanceof EntityPlayer){
			EntityPlayer victim = (EntityPlayer) event.entityLiving;
			ExtendedPlayer propertiesVictim = ExtendedPlayer.get(victim);
			BiomeGenBase biome = victim.worldObj.getWorldChunkManager().getBiomeGenAt(victim.chunkCoordX, victim.chunkCoordZ);
			ItemStack item = victim.getHeldItem();
			
			if(source == DamageSource.drown && propertiesVictim.perks.contains(BUCKET))
				damage -= (damage * 0.1f);
			else if(source == DamageSource.fall && propertiesVictim.perks.contains(LADDER))
				damage -= (damage * 0.1f);
			if((biome.biomeID == FOREST_HILLS || biome.biomeID == FOREST) && propertiesVictim.perks.contains(TREE))
				damage -= (damage * 0.1f);
			
			if(event.source.getSourceOfDamage() instanceof EntityPlayer){
				EntityPlayer attacker = (EntityPlayer) event.source.getSourceOfDamage();
				ExtendedPlayer propertiesAttacker = ExtendedPlayer.get(attacker);
				World world = attacker.worldObj;
				
				if(world.isDaytime() && propertiesAttacker.perks.contains(LAMP))
					damage += (damage * 0.1f);
			}else if(event.source.getSourceOfDamage() instanceof EntityMob && item != null){
				if(propertiesVictim.perks.contains(GOLD_PAXE) && item.getDisplayName().equals("Golden Pickaxe") &&  event.source.getSourceOfDamage() instanceof EntityMob)
					damage -= (damage * 0.1f);
			}
			
			event.ammount = damage;
		}
	}
	
	@SubscribeEvent
	public void breakEvent(PlayerEvent.BreakSpeed event){
		if(event.entityPlayer != null){
			ExtendedPlayer properties = ExtendedPlayer.get(event.entityPlayer);
			ItemStack item = event.entityPlayer.getItemInUse();
			
			if(item != null){
				if(properties.perks.contains(WOODEN_SHOVEL) && item.getDisplayName().equals("Wooden Shovel"))
					event.newSpeed = event.originalSpeed * 1.75f;
				if(properties.perks.contains(STONE_PAXE) && item.getDisplayName().equals("Stone Pickaxe"))
					event.newSpeed = event.originalSpeed * 1.75f;
				if(properties.perks.contains(IRON_PAXE) && item.getDisplayName().equals("Iron Pickaxe"))
					event.newSpeed = event.originalSpeed * 1.5f;
				if(properties.perks.contains(IRON_AXE) && item.getDisplayName().equals("Iron Axe"))
					event.newSpeed = event.originalSpeed * 1.5f;
				if(properties.perks.contains(DIAMOND_PAXE) && item.getDisplayName().equals("Diamond Pickaxe"))
					event.newSpeed = event.originalSpeed * 1.25f;
			}
		}
	}
}

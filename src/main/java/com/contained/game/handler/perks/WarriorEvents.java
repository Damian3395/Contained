package com.contained.game.handler.perks;

import java.util.ArrayList;

import com.contained.game.entity.ExtendedPlayer;

import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class WarriorEvents {
	//Perks
	private final int WOODEN_SWORD = 4;
	private final int LEATHER_TUNIC = 61;
	private final int STONE_SWORD = 62;
	private final int ARROW = 63;
	private final int IRON_HORSE = 64;
	private final int GOLD_HORSE = 65;
	private final int DIAMOND_HORSE = 66;
	private final int SADDLE = 67;
	private final int BOW = 68;
	private final int IRON_PLATE = 69;
	private final int IRON_SWORD = 70;
	private final int GOLD_PLATE = 71;
	private final int GOLD_SWORD= 72;
	private final int DIAMOND_PLATE = 73;
	private final int DIAMOND_SWORD = 74;
	
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
				if(attackerPerks.contains(WOODEN_SWORD) && weapon.getDisplayName().equals("Wooden Sword"))
					damage += (damage * 0.1f);
				if(attackerPerks.contains(STONE_SWORD) && weapon.getDisplayName().equals("Stone Sword"))
					damage += (damage * 0.1f);
				if(attackerPerks.contains(IRON_SWORD) && weapon.getDisplayName().equals("Iron Sword"))
					damage += (damage * 0.1f);
				if(attackerPerks.contains(GOLD_SWORD) && weapon.getDisplayName().equals("Golden Sword"))
					damage += (damage * 0.1f);
				if(attackerPerks.contains(DIAMOND_SWORD) && weapon.getDisplayName().equals("Diamond Sword"))
					damage += (damage * 0.1f);
			}
				
			if(event.entityLiving instanceof EntityPlayer){
				EntityPlayer victim = (EntityPlayer) event.entityLiving;
				ExtendedPlayer propertiesVictim = ExtendedPlayer.get(victim);
				ArrayList<Integer> victimPerks =  propertiesVictim.perks;
				ItemStack armor = victim.inventory.armorInventory[2];
				
				if(armor != null && !victimPerks.isEmpty()){
					if(victimPerks.contains(LEATHER_TUNIC) && armor.getDisplayName().equals("Leather Tunic"))
						damage -= (damage * 0.1f);
					if(victimPerks.contains(IRON_PLATE) && armor.getDisplayName().equals("Iron Chestplate"))
						damage -= (damage * 0.1f);
					if(victimPerks.contains(GOLD_PLATE) && armor.getDisplayName().equals("Golden Chestplate"))
						damage -= (damage * 0.1f);
					if(victimPerks.contains(DIAMOND_PLATE) && armor.getDisplayName().equals("Diamond Chestplate"))
						damage -= (damage * 0.1f);
				}		
			}else if(event.entityLiving instanceof EntityHorse){
				EntityHorse horse = (EntityHorse) event.entityLiving;
				ItemStack horseArmor = horse.getEquipmentInSlot(3);
				if(horse.isTame() && horse.riddenByEntity instanceof EntityPlayer && horseArmor != null){
					EntityPlayer player = (EntityPlayer) event.entityLiving;
					ExtendedPlayer properties = ExtendedPlayer.get(player);
					ArrayList<Integer> perks =  properties.perks;
					
					if(!perks.isEmpty()){
						if(perks.contains(IRON_HORSE) && horseArmor.getDisplayName().equals("Iron Horse Armor"))
							damage -= (damage * 0.2f);
						if(perks.contains(GOLD_HORSE) && horseArmor.getDisplayName().equals("Gold Horse Armor"))
							damage -= (damage * 0.2f);
						if(perks.contains(DIAMOND_HORSE) && horseArmor.getDisplayName().equals("Diamond Horse Armor"))
							damage -= (damage * 0.2f);
					}
				}
			}
			
			event.ammount = damage;
		}
	}
	
	@SubscribeEvent
	public void entityUpdate(LivingUpdateEvent event){
		if(event.entityLiving instanceof EntityHorse){
			EntityHorse horse = (EntityHorse) event.entityLiving;
			if(horse.isTame() && horse.riddenByEntity instanceof EntityPlayer){
				EntityPlayer player = (EntityPlayer) horse.riddenByEntity;
				ExtendedPlayer properties = ExtendedPlayer.get(player);
				ArrayList<Integer> perks = properties.perks;
				
				if(perks.contains(SADDLE))
					horse.setAIMoveSpeed(0.4f);
			}
		}
	}
}

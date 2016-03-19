package com.contained.game.handler.perks;

import java.util.ArrayList;

import com.contained.game.entity.ExtendedPlayer;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerUseItemEvent;
import net.minecraftforge.event.entity.player.UseHoeEvent;

public class CookEvents {
	private final int WOODEN_HOE = 2;
	private final int STONE_HOE = 33;
	private final int SEEDS = 34;
	private final int IRON_HOE = 35;
	private final int FISHING_ROD = 36;
	private final int APPLE = 37;
	private final int CARROT = 38;
	private final int GOLD_HOE = 39;
	private final int DIAMOND_HOE = 40;
	private final int FISH = 41;
	private final int SALMON = 42;
	private final int CAKE = 43;
	private final int GOLD_APPLE = 44;
	private final int COOKIE = 45;
	private final int GOLD_CARROT = 46;
	
	@SubscribeEvent
	public void entityDamage(LivingHurtEvent event){
		DamageSource source = event.source;
		float damage = event.ammount;
		
		if(event.source.getSourceOfDamage() instanceof EntityPlayer){	
			EntityPlayer attacker = (EntityPlayer) source.getSourceOfDamage();
			ExtendedPlayer propertiesAttacker = ExtendedPlayer.get(attacker);
			ArrayList<Integer> attackerPerks = propertiesAttacker.perks;
			ItemStack item = attacker.getHeldItem();
	
			if(item != null && !attackerPerks.isEmpty()){
				if(attackerPerks.contains(IRON_HOE) && item.getDisplayName().equals("Iron Hoe"))
					damage += (damage * 0.1f);
				if(attackerPerks.contains(DIAMOND_HOE) && item.getDisplayName().equals("Diamond Hoe"))
					damage += (damage * 0.1f);
				if(attackerPerks.contains(CARROT) && event.entityLiving instanceof EntityAnimal)
					damage += (damage * 0.1f);
				if(attackerPerks.contains(GOLD_APPLE) && event.entityLiving instanceof EntityHorse)
					damage += (damage * 0.1f);
				if(attackerPerks.contains(GOLD_CARROT) && event.entityLiving instanceof EntityPlayer)
					damage += (damage * 0.1f);
			}
				
			if(event.entityLiving instanceof EntityPlayer){
				EntityPlayer victim = (EntityPlayer) event.entityLiving;
				ExtendedPlayer propertiesVictim = ExtendedPlayer.get(victim);
				ArrayList<Integer> victimPerks =  propertiesVictim.perks;
				item = victim.getHeldItem();
				
				if(victimPerks.contains(WOODEN_HOE) && item.getDisplayName().equals("Wooden Hoe"))
					damage -= (damage * 0.1f);
				if(victimPerks.contains(STONE_HOE) && item.getDisplayName().equals("Stone Hoe"))
					damage -= (damage * 0.1f);
				if(victimPerks.contains(GOLD_HOE) && item.getDisplayName().equals("Golden Hoe"))
					damage -= (damage * 0.1f);
				if(victimPerks.contains(FISHING_ROD) && item.getDisplayName().equals("Fishing Rod"))
					damage -= (damage * 0.1f);
			}else if(event.entityLiving instanceof EntityHorse){
				EntityHorse horse = (EntityHorse) event.entityLiving;
				if(horse.isTame() && horse.riddenByEntity instanceof EntityPlayer){
					EntityPlayer player = (EntityPlayer) horse.riddenByEntity;
					ExtendedPlayer properties = ExtendedPlayer.get(player);
					ArrayList<Integer> perks = properties.perks;
					
					if(perks.contains(APPLE))
						damage -= (damage * 0.1f);
				}
			}
		}
		
		event.ammount = damage;
	}
	
	@SubscribeEvent
	public void entityConsume(PlayerUseItemEvent.Finish event){
		EntityPlayer player = event.entityPlayer;
		ExtendedPlayer properties = ExtendedPlayer.get(player);
		ArrayList<Integer> perks = properties.perks;
		
		if(event.item.getDisplayName().equals("Cooked Fish") && !player.isPotionActive(8205) && perks.contains(FISH))
			player.addPotionEffect(new PotionEffect(8205, 50));	
		if(event.item.getDisplayName().equals("Cooked Salmon") && !player.isPotionActive(8201) && perks.contains(SALMON))
			player.addPotionEffect(new PotionEffect(8201, 50));
		if(event.item.getDisplayName().equals("Cake") && perks.contains(CAKE))
			player.setHealth(player.getMaxHealth());
		if(event.item.getDisplayName().equals("Cookie") && perks.contains(COOKIE))
			player.setHealth(player.getMaxHealth());
	}
}

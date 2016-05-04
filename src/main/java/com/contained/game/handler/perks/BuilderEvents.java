package com.contained.game.handler.perks;

import java.util.ArrayList;
import java.util.Random;

import com.contained.game.entity.ExtendedPlayer;
import com.contained.game.util.Resources;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.world.BlockEvent.PlaceEvent;
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
	
	private final int PLAINS = 1;
	private final int DESERT = 2;
	private final int TAIGA = 5;
	private final int ICE_PLAINS = 12;
	private final int BEACH = 16;
	
	int[] stoneItems = {};
	
	@SubscribeEvent
	public void entityDamage(LivingHurtEvent event){
		DamageSource source = event.source;
		float damage = event.ammount;
		
		if(event.entityLiving instanceof EntityPlayer){
			EntityPlayer victim = (EntityPlayer) event.entityLiving;
			ExtendedPlayer propertiesVictim = ExtendedPlayer.get(victim);
			BiomeGenBase biome = victim.worldObj.getWorldChunkManager().getBiomeGenAt(victim.chunkCoordX, victim.chunkCoordZ);
			World world = victim.worldObj;
			ItemStack item = victim.getItemInUse();
			
			if(!world.isDaytime() && propertiesVictim.perks.contains(COBBLE))
				damage -= (damage * 0.1f);
			if(biome.biomeID == PLAINS && propertiesVictim.perks.contains(GRASS))
				damage -= (damage * 0.1f);
			if(biome.biomeID == BEACH && propertiesVictim.perks.contains(SANDSTONE))
				damage -= (damage * 0.1f);
			if(biome.biomeID == TAIGA && propertiesVictim.perks.contains(DIAMOND))
				damage -= (damage * 0.1f);
			if(source == DamageSource.onFire && propertiesVictim.perks.contains(FURNACE))
				damage -= (damage * 0.1f);
			
			if(item != null){
				int id = Item.getIdFromItem(item.getItem());
				if((id >= 256 && id <= 258) || id == 292 && propertiesVictim.perks.contains(IRON)) //Iron Tools
					damage -= (damage * 0.1f);
			}
		}else if(source.getSourceOfDamage() instanceof EntityPlayer){
			EntityPlayer attacker = (EntityPlayer) event.entityLiving;
			ExtendedPlayer propertiesAttacker = ExtendedPlayer.get(attacker);
			BiomeGenBase biome = attacker.worldObj.getWorldChunkManager().getBiomeGenAt(attacker.chunkCoordX, attacker.chunkCoordZ);
			ItemStack item = attacker.getItemInUse();
			
			if(biome.biomeID == PLAINS && propertiesAttacker.perks.contains(DIRT))
				damage += (damage * 0.1f);
			if(biome.biomeID == DESERT && propertiesAttacker.perks.contains(SANDSTONECURVED))
				damage += (damage * 0.1f);
			if(biome.biomeID == ICE_PLAINS && propertiesAttacker.perks.contains(GLASS))
				damage += (damage * 0.1f);
			if(attacker.dimension == Resources.OVERWORLD && propertiesAttacker.perks.contains(BRICK))
				damage += (damage * 0.1f);
			if(attacker.dimension == Resources.NETHER && propertiesAttacker.perks.contains(OBSIDIAN))
				damage += (damage * 0.1f);
			if(item != null){
				int id = Item.getIdFromItem(item.getItem());
				if((id >= 272 && id <= 275) || id == 291 && propertiesAttacker.perks.contains(STONE)) //Stone Items
					damage += (damage * 0.1f);
				if((id >= 283 && id <= 286) || id == 294 && propertiesAttacker.perks.contains(GOLD)) //Gold Items
					damage += (damage * 0.1f);
			}
		}
		
		event.ammount = damage;
	}
	
	@SubscribeEvent
	public void entityUpdate(LivingUpdateEvent event){
		if(event.entityLiving instanceof EntityPlayer){
			EntityPlayer player = (EntityPlayer) event.entityLiving;
			ExtendedPlayer properties = ExtendedPlayer.get(player);
			ArrayList<Integer> perks = properties.perks;
			
			if(perks.contains(SAND) && player.getAIMoveSpeed() != 0.3f)
				player.setAIMoveSpeed(0.3f);
		}
	}
	
	@SubscribeEvent
	public void placeBlockEvent(PlaceEvent event){
		if(event.player != null && event.placedBlock != null){
			ExtendedPlayer properties = ExtendedPlayer.get(event.player);
			if(properties.perks.contains(STONEBRICK) && event.player.inventory.getFirstEmptyStack() != -1){
				Random rand = new Random();
				if(rand.nextBoolean())
					event.player.inventory.addItemStackToInventory(new ItemStack(event.placedBlock));
			}
		}
	}
}

package com.contained.game.handler;

import java.awt.Point;
import java.util.List;

import com.contained.game.Contained;
import com.contained.game.user.PlayerTeamIndividual;
import com.contained.game.util.Util;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.monster.EntityGolem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerBrewingStand;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.ContainerDispenser;
import net.minecraft.inventory.ContainerEnchantment;
import net.minecraft.inventory.ContainerFurnace;
import net.minecraft.inventory.ContainerHorseInventory;
import net.minecraft.inventory.ContainerRepair;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.minecart.MinecartUpdateEvent;
import net.minecraftforge.event.entity.player.EntityInteractEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;
import net.minecraftforge.event.entity.player.PlayerOpenContainerEvent;
import net.minecraftforge.event.entity.player.PlayerPickupXpEvent;
import net.minecraftforge.event.world.BlockEvent;
import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

/**
 * Event handlers
 */
public class ProtectionEvents {	
	
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	//Break Protection
    public void onPlayerBreaksBlock(BlockEvent.BreakEvent ev) {
		if (isExempt(ev.world, ev.getPlayer()))
			return;
		if (inProtectRange(ev.getPlayer(), ev.x, ev.y, ev.z) && Contained.configs.breakProtect) {
			Util.debugMessage(ev.getPlayer(), "breakProtect");
			ev.setCanceled(true);		
		}
	}
	
	@SubscribeEvent
	public void onMinecartTick(MinecartUpdateEvent ev) {
		EntityMinecart m = ev.minecart;
		if (!m.worldObj.isRemote) {
			// Make minecarts invulnerable to damage unless a trusted player is nearby.
			@SuppressWarnings("unchecked")
			List<EntityPlayer> nearbyPlayers = m.worldObj.getEntitiesWithinAABB(
					EntityPlayer.class, 
					AxisAlignedBB.getBoundingBox(m.posX-4,m.posY-4,m.posZ-4,
												 m.posX+4,m.posY+4,m.posZ+4));
			if (nearbyPlayers.size() == 0)
				Util.setEntityInvulnerability(m, true);
			else {
				boolean foundAllowedPlayer = false;
				for(EntityPlayer p : nearbyPlayers) {
					if (!isExempt(m.worldObj, p) && inProtectRange(p, m.posX, m.posY, m.posZ)
							&& Contained.configs.breakProtect) 
					{
						continue;
					}
					foundAllowedPlayer = true;
					break;					
				}
				
				if (!foundAllowedPlayer)
					Util.setEntityInvulnerability(m, true);
				else
					Util.setEntityInvulnerability(m, false);
			}
		}
	}
	
    @SubscribeEvent
    //Place Protection
    public void onBlockPlacement(BlockEvent.PlaceEvent ev) {
    	placeProtection(ev.player, ev);
    }

    @SubscribeEvent
    public void onMultiBlockPlacement(BlockEvent.MultiPlaceEvent ev) {
    	placeProtection(ev.player, ev);
    }
	
	public void placeProtection(EntityPlayer player, BlockEvent.PlaceEvent ev) {
		if (isExempt(ev.world, player))
			return;
		if (inProtectRange(player, ev.x, ev.y, ev.z) && Contained.configs.buildProtect) {
			Util.debugMessage(player, "placeProtect");
			ev.setCanceled(true);
		}
	}
	
	@SubscribeEvent
	//Damage & Death Protection
	public void onEntityDamaged(LivingHurtEvent event) {
		Entity damageSource = event.source.getEntity();
		if (!(damageSource instanceof EntityPlayer) 
				|| isExempt(event.entity.worldObj, (EntityPlayer)damageSource))
			return;
		EntityPlayer attacker = (EntityPlayer)damageSource;
		if (inProtectRange(attacker, event.entityLiving.posX, event.entityLiving.posY, event.entityLiving.posZ))
		{
			if (event.entityLiving != null && event.entityLiving instanceof EntityMob && Contained.configs.mobProtect) {
				Util.debugMessage(attacker, "monsterProtect");
				event.setCanceled(true);
			}
			else if (event.entityLiving != null && event.entityLiving instanceof EntityPlayer && Contained.configs.playerProtect) {
				Util.debugMessage(attacker, "playerProtect");
				event.setCanceled(true);
			}
			else if (event.entityLiving != null && (event.entityLiving instanceof EntityAnimal 
					|| event.entityLiving instanceof IMerchant
					|| event.entityLiving instanceof EntityGolem)
						&& Contained.configs.animalProtect) {
				Util.debugMessage(attacker, "passiveEntityProtect");
				event.setCanceled(true);
			}
		}
	}
	
	@SubscribeEvent
	//Container Protection
	public void onContainerOpen(PlayerOpenContainerEvent ev) {
		if (isExempt(ev.entity.worldObj, ev.entityPlayer))
			return;
		if (inProtectRange(ev.entityPlayer, ev.entity.posX, ev.entity.posY, ev.entity.posZ)
				&& Contained.configs.containerProtect) {
			Container c = ev.entityPlayer.openContainer;
			if (c != null && (c instanceof ContainerDispenser
				|| c instanceof ContainerBrewingStand
				|| c instanceof ContainerHorseInventory
				|| c instanceof ContainerFurnace
				|| c instanceof ContainerChest
				|| c instanceof ContainerRepair
				|| c instanceof ContainerEnchantment)) 
			{
				Util.debugMessage(ev.entityPlayer, "containerProtect");
				ev.setResult(Event.Result.DENY);
			}
		}
	}
	
	@SubscribeEvent
	//Entity Interaction Protection
	public void onEntityInteract(EntityInteractEvent ev) {
		if (isExempt(ev.entity.worldObj, ev.entityPlayer))
			return;
		if (inProtectRange(ev.entityPlayer, ev.target.posX, ev.target.posY, ev.target.posZ)
				&& Contained.configs.interactProtect) {
			Util.debugMessage(ev.entityPlayer, "interactProtect");
			ev.setCanceled(true);
		}
	}
	
	@SubscribeEvent
	//Bucket Protection
	public void onBucketFill(FillBucketEvent ev) {
		if (isExempt(ev.world, ev.entityPlayer))
			return;
		if (inProtectRange(ev.entityPlayer, ev.target.blockX, ev.target.blockY, ev.target.blockZ)
				&& Contained.configs.bucketProtect) {
			Util.debugMessage(ev.entityPlayer, "bucketProtect");
			ev.setCanceled(true);
		}
	}
	
	@SubscribeEvent
	//Item Pickup Protection
	public void onItemCollect(EntityItemPickupEvent event) {
		event.setCanceled(pickupProtection(event));
	}
	
	@SubscribeEvent
	public void onEXPCollect(PlayerPickupXpEvent event) {
		event.setCanceled(pickupProtection(event));
	}
	
	public boolean pickupProtection(PlayerEvent ev) {
		if (isExempt(ev.entity.worldObj, ev.entityPlayer))
			return false;
		if (inProtectRange(ev.entityPlayer, ev.entity.posX, ev.entity.posY, ev.entity.posZ) 
				&& Contained.configs.itemProtect) {
			Util.debugMessage(ev.entityPlayer, "pickupProtect");
			return true;
		}
		return false;
	}
	
	@SubscribeEvent
	//Harvest handling
	public void onBlockInteract(PlayerInteractEvent ev) {
		if (ev.action == Action.RIGHT_CLICK_BLOCK && !ev.world.isRemote) {
			Block b = ev.world.getBlock(ev.x, ev.y, ev.z);
			if (b != null && b instanceof BlockCrops) {
				if (!isExempt(ev.world, ev.entityPlayer) 
						&& inProtectRange(ev.entityPlayer, ev.x, ev.y, ev.z) 
						&& Contained.configs.harvestProtect) {
					Util.debugMessage(ev.entityPlayer, "harvestProtect");
					return;
				}
				else {
					BlockCrops crop = (BlockCrops)b;
					int meta = ev.world.getBlockMetadata(ev.x, ev.y, ev.z);
					if (meta == 7) {
						//Crop is fully grown; revert to seedling & drop item
						ev.world.setBlockMetadataWithNotify(ev.x, ev.y, ev.z, 0, 2);
						ev.world.spawnEntityInWorld(new EntityItem(ev.world, ev.x, ev.y+1, ev.z, 
								new ItemStack(crop.getItemDropped(meta, ev.world.rand, 0), 1)));
					}
				}
			}
		}
	}
	
	/**
	 * Returns whether the current entity (the one performing the action) is currently
	 * in a region of protected land.
	 * 
	 * @param ent The entity performing the action
	 * @param x   The x coordinate of the instance/entity being affected by the action
	 * @param y   The y coordinate of the instance/entity being affected by the action
	 * @param z   The z coordinate of the instance/entity being affected by the action
	 * @return
	 */
	public static boolean inProtectRange(EntityPlayer ent, double x, double y, double z) {
		if (ent != null) {
			Point check = new Point((int)x, (int)z);
			if (Contained.territoryData.containsKey(check)) {
				String teamOwner = Contained.territoryData.get(check);
				PlayerTeamIndividual entData = PlayerTeamIndividual.get(ent);
				if (entData != null && teamOwner.equals(entData.teamID))
					return false; //This player is in their own team's territory.
				else
					return true;  //This player is in another team's territory.
			}
		}
		return false; //This player is not inside claimed territory.
	}
	
	/**
	 * Should this entity be exempt from the protection rules?
	 */
	public static boolean isExempt(World w, EntityPlayer ent) {
		if (w.isRemote)
			return true;
		if (ent == null)
			return true;
		if (ent.capabilities.isCreativeMode && Contained.configs.creativeOverride)
			return true;
		return false;
	}
}

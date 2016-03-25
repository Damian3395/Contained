package com.contained.game.handler;

import java.awt.Point;
import java.util.ArrayList;

import com.contained.game.Contained;
import com.contained.game.ContainedRegistry;
import com.contained.game.data.Data;
import com.contained.game.data.DataItemStack;
import com.contained.game.data.Data.OccupationRank;
import com.contained.game.entity.ExtendedPlayer;
import com.contained.game.item.BlockInteractItem;
import com.contained.game.item.ItemTerritory;
import com.contained.game.item.SurveyClipboard;
import com.contained.game.network.ClientPacketHandler;
import com.contained.game.ui.SurveyData;
import com.contained.game.user.PlayerTeamIndividual;
import com.contained.game.util.Resources;
import com.contained.game.util.Util;
import com.contained.game.world.block.AntiTerritoryMachine;
import com.contained.game.world.block.TerritoryMachine;
import com.contained.game.world.block.TerritoryMachineTE;

import codechicken.lib.packet.PacketCustom;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.event.entity.player.PlayerUseItemEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;

public class PlayerEvents {
	@SubscribeEvent
	//When a player joins the server, send their client the territory & team data.
	public void onJoin(EntityJoinWorldEvent event) {
		if (event.entity instanceof EntityPlayer && !event.world.isRemote) {
			EntityPlayer joined = (EntityPlayer)event.entity;
			
			//Future TODO:
			//  -Remind the player on join if they have pending invitations.
			//  -Notify the player on join if one of their invitations got accepted
			//   since the last time they were online.
			
			boolean completedSurvey = false;
			
			if (PlayerTeamIndividual.get(joined) == null) {
				// Server has no info about this player, this must be their first
				// time joining. Initialize their custom data.
				Contained.teamMemberData.add(new PlayerTeamIndividual(joined.getDisplayName()));
			
				//Give first time players a tutorial book.
				if (!joined.inventory.hasItem(ContainedRegistry.book))
					event.world.spawnEntityInWorld(new EntityItem(event.world, 
							joined.posX, joined.posY+1, joined.posZ, 
							new ItemStack(ContainedRegistry.book, 1)));
			}
			else {
				//If player has not completed the survey, give them a reminder.
				PlayerTeamIndividual pdata = PlayerTeamIndividual.get(joined);
				if (pdata.surveyResponses.progress <= SurveyData.getSurveyLength())
					Util.displayMessage(joined, "§a§l(Reminder: Please take a moment to fill out your §a§lsurvey)");
				else
					completedSurvey = true;
			}			
			
			//If player does not have a survey in their inventory, give them one.
			if (!completedSurvey && !joined.inventory.hasItem(SurveyClipboard.instance))
				event.world.spawnEntityInWorld(new EntityItem(event.world, 
						joined.posX, joined.posY+1, joined.posZ, 
						new ItemStack(SurveyClipboard.instance, 1)));
			
			if (joined instanceof EntityPlayerMP) {
				Contained.channel.sendTo(ClientPacketHandler.packetSyncTeams(Contained.teamData).toPacket(), (EntityPlayerMP) joined);
				Contained.channel.sendTo(ClientPacketHandler.packetSyncTerritories(Contained.territoryData).toPacket(), (EntityPlayerMP) joined);	
				Contained.channel.sendTo(ClientPacketHandler.packetSyncLocalPlayer((EntityPlayer)joined).toPacket(), (EntityPlayerMP)joined);
			}
			
			//Class Perks
			ArrayList<Integer> perks = ExtendedPlayer.get(joined).perks;
			PacketCustom perkPacket = new PacketCustom(Resources.MOD_ID, ClientPacketHandler.PERK_INFO);
			for(int i = 0; i < 5; i++){
				if(i < perks.size())
					perkPacket.writeInt(perks.get(i));
				else
					perkPacket.writeInt(-1);
			}
			perkPacket.writeInt(ExtendedPlayer.get(joined).occupationClass);
			perkPacket.writeInt(ExtendedPlayer.get(joined).occupationLevel);
			Contained.channel.sendTo(perkPacket.toPacket(), (EntityPlayerMP) joined);
		}
	}
	
	@SubscribeEvent
	public void onEntityLiving(LivingUpdateEvent event) {
		if (event.entity != null && event.entity instanceof EntityPlayer
				&& !event.entity.worldObj.isRemote) {
			EntityPlayer player = (EntityPlayer)event.entity;
			
			//Intermittently sync data logging information with the client
			//so the visualizations can be updated.
			if (player != null && Math.random() <= 1.0/20.0) {
				int[] occupationData = ExtendedPlayer.get(player).getOccupationValues();
				
				if (player instanceof EntityPlayerMP) {
					PacketCustom occPacket = new PacketCustom(Resources.MOD_ID, ClientPacketHandler.OCCUPATIONAL_DATA);
					for(int i=0; i<occupationData.length; i++)
						occPacket.writeInt(occupationData[i]);
					Contained.channel.sendTo(occPacket.toPacket(), (EntityPlayerMP)player);
					
					PacketCustom usePacket = new PacketCustom(Resources.MOD_ID, ClientPacketHandler.ITEM_USAGE_DATA);
					usePacket.writeInt(ExtendedPlayer.get(player).usedOwnItems);
					usePacket.writeInt(ExtendedPlayer.get(player).usedOthersItems);
					usePacket.writeInt(ExtendedPlayer.get(player).usedByOthers);
					Contained.channel.sendTo(usePacket.toPacket(), (EntityPlayerMP)player);
				}
			}
			
			//Update items in a player's inventory which
			//have no owner to be owned by them
			if (player != null) {
				ItemStack[] inventory = player.inventory.mainInventory;
				for(ItemStack stack : inventory)
					if (stack != null)
						processNewOwnership(player, stack);
			}
		}
	}
	
	@SubscribeEvent
	public void onEntityDeath(LivingDeathEvent event) {
		// Players in teams should drop anti-territory gems when they are killed
		// by other players.
		Entity source = event.source.getSourceOfDamage();
		
		if (event.entityLiving != null && source != null && !event.entityLiving.worldObj.isRemote
		 && event.entityLiving instanceof EntityPlayer && source instanceof EntityPlayer) {
			EntityPlayer killed = (EntityPlayer)event.entityLiving;
			PlayerTeamIndividual playerData = PlayerTeamIndividual.get(killed);
			if (playerData.teamID != null) {
				int amount = 1;
				if (playerData.isLeader) //Leaders drop more anti-gems on death.
					amount = 4;				
				ItemStack toDrop = new ItemStack(ItemTerritory.removeTerritory, amount);
				NBTTagCompound itemData = Data.getTagCompound(toDrop);
				itemData.setString("teamOwner", playerData.teamID);
				toDrop.setTagCompound(itemData);
				killed.worldObj.spawnEntityInWorld(new EntityItem(killed.worldObj, killed.posX, killed.posY+1, killed.posZ, toDrop));
			}
		}
	}
	
	@SubscribeEvent
	// For players that are in teams, only allow them to sleep in beds that are
	// within their team's territory.
	public void onBedSleep(PlayerSleepInBedEvent ev) {
		if (ev.entityPlayer != null && !ev.entityPlayer.worldObj.isRemote) {
			PlayerTeamIndividual playerData = PlayerTeamIndividual.get(ev.entityPlayer);
			if (playerData.teamID != null) {
				Point probe = new Point(ev.x, ev.z);
				if (!Contained.territoryData.containsKey(probe)
						|| !Contained.territoryData.get(probe).equals(playerData.teamID)) {
					ev.result = EntityPlayer.EnumStatus.OTHER_PROBLEM;
					Util.displayError(ev.entityPlayer, "You can only sleep within your team's territory.");
				}
			}
		}
	}
	
	@SubscribeEvent
	public void onEntityConstructing(EntityConstructing event) {
		if (event.entity instanceof EntityPlayer && ExtendedPlayer.get((EntityPlayer) event.entity) == null)
			ExtendedPlayer.register((EntityPlayer) event.entity);
	}
	
	/*
	 * =====================================================================
	 *  ITEM USAGE FUNCTIONS
	 * =====================================================================
	 */
	
	//Handle all data collection for a player using (consuming) an item.
	public void processItemUsage(EntityPlayer user, ItemStack item) {
		if (!user.worldObj.isRemote) {
			NBTTagCompound itemData = Data.getTagCompound(item);	
			String ownerName = itemData.getString("owner");
			EntityPlayer owner = user.worldObj.getPlayerEntityByName(ownerName);
			
			//Use own item
			if (ownerName.equals(user.getDisplayName())) {
				ExtendedPlayer.get(user).usedOwnItems += 1;
			}
			//Use other person's item
			else {
				ExtendedPlayer.get(user).usedOthersItems += 1;
				if (owner != null)
					//Note: Data logging here may be flawed, as I don't
					//think it will be able to update this parameter
					//if the owner of the item is not currently online.
					ExtendedPlayer.get(owner).usedByOthers += 1;
			}
		}
	}
	
	@SubscribeEvent
	//When an item is used, if it is consumed by the usage, log it.
	public void onItemUsed(PlayerUseItemEvent.Finish event) {
		if (event.entityPlayer != null && event.item != null)
			processItemUsage(event.entityPlayer, event.item);
	}
	
	@SubscribeEvent
	//Handle firing the functionality of BlockInteractItems when right-clicked on a block.
	public void onItemUse(PlayerInteractEvent event) {
		if (!event.world.isRemote) {
			ItemStack usedItem = event.entityPlayer.getHeldItem();
			if (usedItem == null)
				return;
			if (usedItem.getItem() instanceof BlockInteractItem
					&& event.action == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) {
				BlockInteractItem intItem = (BlockInteractItem)usedItem.getItem();
				intItem.onBlockInteract(event.entityPlayer, event.x, event.y, event.z, usedItem);
			}
		}
	}
	
    @SubscribeEvent
    //Transfer player's team to a territory machine when placed.
    public void onBlockPlacement(BlockEvent.PlaceEvent ev) {
    	if (ev.player != null &&
    			(ev.block instanceof TerritoryMachine.BlockClaimTerritory
    		  || ev.block instanceof AntiTerritoryMachine.BlockAntiTerritory)) {
    		TileEntity te = ev.world.getTileEntity(ev.x, ev.y, ev.z);
    		if (te != null && te instanceof TerritoryMachineTE) {
    			TerritoryMachineTE machine = (TerritoryMachineTE)te;
    			
    			if (!ev.world.isRemote && ev.block instanceof TerritoryMachine.BlockClaimTerritory) {
    				PlayerTeamIndividual playerData = PlayerTeamIndividual.get(ev.player);
    				machine.teamID = playerData.teamID;
    				machine.sendInfoPacket();
    			} 
    			if (ev.block instanceof AntiTerritoryMachine.BlockAntiTerritory && ev.itemInHand != null) {
    				NBTTagCompound itemData = Data.getTagCompound(ev.itemInHand);
    				String teamID = itemData.getString("teamOwner");
    				if (teamID == null || teamID.equals(""))
    					machine.teamID = null;
    				else {
    					machine.teamID = teamID;
    					machine.sendInfoPacket();
    				}
    			}
    		}
    	}
    }
	
	/*
	 * =====================================================================
	 *  ITEM OWNERSHIP FUNCTIONS
	 * =====================================================================
	 */
	
	//Handle all data collection procedures for when a player becomes
	//the owner of a new item.
	public void processNewOwnership(EntityPlayer newOwner, ItemStack item) {
		//First make sure this item isn't owned by someone already...
		NBTTagCompound itemData = Data.getTagCompound(item);	
		String owner = itemData.getString("owner");
		if ((owner == null || owner.equals("")) && newOwner != null) {			
			//Check if this item corresponds to an "occupation", and update
			//player's values accordingly
			OccupationRank occ = Data.occupationMap.get(new DataItemStack(item));
			if (occ != null) {
				ExtendedPlayer.get(newOwner)
					.increaseOccupation(occ.occupationID, occ.rank*item.stackSize);
			}
			
			//Set this player as the new owner of the item
			itemData.setString("owner", newOwner.getDisplayName());
			item.setTagCompound(itemData);
		}
	}
	
	@SubscribeEvent
	//When an unowned item is collected, it is owned by the collector.
	public void onItemCollected(EntityItemPickupEvent event) {
		if (event.entityPlayer != null)
			processNewOwnership(event.entityPlayer, event.item.getEntityItem());
	}
	
	@SubscribeEvent
	//When a block is harvested, the dropped items are owned by the harvester.
	public void onItemHarvested(HarvestDropsEvent event) {
		ArrayList<ItemStack> drops = event.drops;
		for (ItemStack stack : drops)
			processNewOwnership(event.harvester, stack);
	}
	
	@SubscribeEvent
	//When a creature is killed, the dropped items are owned by the killer.
	public void onCreatureDropItems(LivingDropsEvent event) {
		DamageSource ds = event.source;
		if (ds != null && ds.getEntity() != null
				&& ds.getEntity() instanceof EntityPlayer) 
		{
			EntityPlayer killer = (EntityPlayer)ds.getEntity();
			ArrayList<EntityItem> drops = event.drops;
			for (EntityItem item : drops)
				processNewOwnership(killer, item.getEntityItem());
		}
	}
	
	@SubscribeEvent
	//Show the owner of an item in the item's mouse-over tooltip.
	public void itemInformation(ItemTooltipEvent event) {
		ItemStack stack = event.itemStack;
		NBTTagCompound itemData = Data.getTagCompound(stack);
		String owner = itemData.getString("owner");
		
		if (owner == null || owner.equals(""))
			event.toolTip.add("Not Owned");
		else
			event.toolTip.add("Owner: "+owner);
	}
	
	@SubscribeEvent
	//Make a player wait a little longer to loot an item if it belongs
	//to someone else.
	public void onItemPickup(EntityItemPickupEvent event) {
		int secondsToWait = 10;
		ItemStack stack = event.item.getEntityItem();
		NBTTagCompound itemData = Data.getTagCompound(stack);		
		String owner = itemData.getString("owner");
		
		if (event.entityPlayer != null && !(owner == null || owner.equals("") 
				|| owner.equals(event.entityPlayer.getDisplayName()))) {
			if (event.item.ticksExisted < 20*secondsToWait)
				event.setCanceled(true);
		}
	}
}

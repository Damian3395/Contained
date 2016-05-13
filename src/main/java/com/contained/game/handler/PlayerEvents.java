package com.contained.game.handler;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;

import com.contained.game.Contained;
import com.contained.game.ContainedRegistry;
import com.contained.game.Settings;
import com.contained.game.data.Data;
import com.contained.game.data.DataItemStack;
import com.contained.game.data.Data.OccupationRank;
import com.contained.game.entity.ExtendedPlayer;
import com.contained.game.item.BlockInteractItem;
import com.contained.game.item.DowsingRod;
import com.contained.game.item.ItemTerritory;
import com.contained.game.item.SurveyClipboard;
import com.contained.game.network.ClientPacketHandlerUtil;
import com.contained.game.ui.survey.SurveyData;
import com.contained.game.user.PlayerMiniGame;
import com.contained.game.user.PlayerTeam;
import com.contained.game.user.PlayerTeamIndividual;
import com.contained.game.user.PlayerTeamInvitation;
import com.contained.game.util.MiniGameUtil;
import com.contained.game.util.Resources;
import com.contained.game.util.Util;
import com.contained.game.world.block.AntiTerritoryMachine;
import com.contained.game.world.block.TerritoryMachine;
import com.contained.game.world.block.TerritoryMachineTE;

import codechicken.lib.packet.PacketCustom;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
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
			boolean completedSurvey = false;
			
			if (PlayerTeamIndividual.get(joined) == null) {
				// Server has no info about this player, this must be their first
				// time joining. Initialize their custom data.
				Contained.teamMemberData.add(new PlayerTeamIndividual(joined.getDisplayName()));
				Contained.channel.sendToAll(ClientPacketHandlerUtil.packetNewPlayer(joined.getDisplayName()).toPacket());
			}
			else {
				PlayerTeamIndividual pdata = PlayerTeamIndividual.get(joined);
				
				//If player has not completed the survey, give them a reminder.
				if (pdata.surveyResponses.progress <= SurveyData.getSurveyLength())
					Util.displayMessage(joined, "§a§l(Reminder: Please take a moment to fill out §a§lyour §a§lsurvey)");
				else
					completedSurvey = true;
					
				//If the player has pending invitations, let them know.
				if (PlayerTeamInvitation.getInvitations(pdata).size() > 0)
					Util.displayMessage(joined, "§d§lYou have pending inviations in your guild §d§lmenu!");
				
				// If the player got accepted into a team since last time they 
				// were online, let them know.
				if (pdata.teamID != null && pdata.joinTime > pdata.lastOnline) {
					PlayerTeam newTeam = PlayerTeam.get(pdata.teamID, joined.dimension);
					Util.displayMessage(joined, "§d§lYou are now a member of "+newTeam.getFormatCode()+"§l"+newTeam.displayName+"§d§l!");
					pdata.lastOnline = System.currentTimeMillis();
				}
			}			
			
			//If player does not have a survey in their inventory, give them one.
			if (!completedSurvey && !joined.inventory.hasItem(SurveyClipboard.instance)
					&& joined.inventory.getFirstEmptyStack() > -1){
				joined.inventory.addItemStackToInventory(new ItemStack(SurveyClipboard.instance, 1));
				PacketCustom itemPacket = new PacketCustom(Resources.MOD_ID, ClientPacketHandlerUtil.ADD_ITEM);
				itemPacket.writeItemStack(new ItemStack(SurveyClipboard.instance, 1));
				Contained.channel.sendTo(itemPacket.toPacket(), (EntityPlayerMP) joined);
			}
			
			//If dowsing is enabled, give the player a dowsing rod.
			if (Contained.configs.enableDowsing[Settings.getDimConfig(joined.dimension)]
					&& !joined.inventory.hasItem(DowsingRod.instance)
					&& joined.inventory.getFirstEmptyStack() > -1) {
				joined.inventory.addItemStackToInventory(new ItemStack(DowsingRod.instance, 1));
				PacketCustom itemPacket = new PacketCustom(Resources.MOD_ID, ClientPacketHandlerUtil.ADD_ITEM);
				itemPacket.writeItemStack(new ItemStack(DowsingRod.instance, 1));
				Contained.channel.sendTo(itemPacket.toPacket(), (EntityPlayerMP) joined);
			}
			
			//Tutorial book
			if (!joined.inventory.hasItem(ContainedRegistry.book)
					&& joined.inventory.getFirstEmptyStack() > -1){
				joined.inventory.addItemStackToInventory(new ItemStack(ContainedRegistry.book, 1));
				PacketCustom itemPacket = new PacketCustom(Resources.MOD_ID, ClientPacketHandlerUtil.ADD_ITEM);
				itemPacket.writeItemStack(new ItemStack(ContainedRegistry.book, 1));
				Contained.channel.sendTo(itemPacket.toPacket(), (EntityPlayerMP) joined);
			}
			
			//Update PlayerMiniGame When Joining Server
			if(!ExtendedPlayer.get(joined).isAdmin() 
					&& (MiniGameUtil.isPvP(joined.dimension) || MiniGameUtil.isTreasure(joined.dimension))){
				PlayerMiniGame miniGame = PlayerMiniGame.get(joined.dimension);
				if(miniGame != null && ExtendedPlayer.get(joined).gameID == miniGame.getGameID()){
					PacketCustom miniGamePacket = new PacketCustom(Resources.MOD_ID, ClientPacketHandlerUtil.MINIGAME_STARTED);
					miniGamePacket.writeInt(miniGame.getGameMode());
					miniGamePacket.writeInt(miniGame.getGameID());
					
					NBTTagCompound miniGameTag = new NBTTagCompound();
					miniGame.writeToNBT(miniGameTag);
					miniGamePacket.writeNBTTagCompound(miniGameTag);
					
					miniGamePacket.writeInt(miniGame.getGameDimension());
					miniGamePacket.writeInt(Contained.getTeamList(miniGame.getGameDimension()).size());
					for(PlayerTeam team : Contained.getTeamList(miniGame.getGameDimension())){
						NBTTagCompound teamTag = new NBTTagCompound();
						team.writeToNBT(teamTag);
						miniGamePacket.writeNBTTagCompound(teamTag);
					}
					
					Contained.channel.sendTo(miniGamePacket.toPacket(), (EntityPlayerMP) joined);
				} else //If MiniGame Does Not Exist, Send Back To Lobby
					Util.travelToDimension(Resources.OVERWORLD, joined, true);
			}
			
			// Players should get a short period of invincibility upon respawning
			// to prevent spawn camping
			joined.addPotionEffect(new PotionEffect(Potion.resistance.id, 20*15, 5));
			
			if (joined instanceof EntityPlayerMP) {
				Contained.channel.sendTo(ClientPacketHandlerUtil.packetSyncTeams(Contained.getTeamList(joined.dimension)).toPacket(), (EntityPlayerMP) joined);
				Contained.channel.sendTo(ClientPacketHandlerUtil.packetSyncTerritories(Contained.getTerritoryMap(joined.dimension)).toPacket(), (EntityPlayerMP) joined);	
				Contained.channel.sendTo(ClientPacketHandlerUtil.packetSyncLocalPlayer((EntityPlayer)joined).toPacket(), (EntityPlayerMP)joined);
				Contained.channel.sendTo(ClientPacketHandlerUtil.packetPlayerList(Contained.teamMemberData).toPacket(), (EntityPlayerMP)joined);
				Contained.channel.sendTo(ClientPacketHandlerUtil.packetSyncRelevantInvites(joined).toPacket(), (EntityPlayerMP)joined);
				Contained.channel.sendTo(ClientPacketHandlerUtil.packetSyncTrades(Contained.getTradeList(joined.dimension)).toPacket(), (EntityPlayerMP) joined);
				ClientPacketHandlerUtil.syncPlayerStats(ExtendedPlayer.get(joined), (EntityPlayerMP) joined);
				
				if (MiniGameUtil.isTreasure(joined.dimension))
					Contained.channel.sendTo(ClientPacketHandlerUtil.packetAddTreasures(Contained.getActiveTreasures(joined.dimension), true).toPacket(), (EntityPlayerMP)joined);
				
				//Class Perks
				ArrayList<Integer> perks = ExtendedPlayer.get(joined).perks;
				PacketCustom perkPacket = new PacketCustom(Resources.MOD_ID, ClientPacketHandlerUtil.PERK_INFO);
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
	}
	
	@SubscribeEvent
	public void onLeave(PlayerEvent.PlayerLoggedOutEvent event) {
		PlayerTeamIndividual pdata = PlayerTeamIndividual.get(event.player);
		pdata.lastOnline = System.currentTimeMillis();
	}
	
	@SubscribeEvent
	public void onEntityLiving(LivingUpdateEvent event) {
		if (event.entity != null && event.entity instanceof EntityPlayer
				&& !event.entity.worldObj.isRemote) {
			EntityPlayer player = (EntityPlayer)event.entity;
			ExtendedPlayer properties = ExtendedPlayer.get(player);
			
			//Update Admin Rights When Logging Back In Or Changing Dimensions
			if(properties.isAdmin()){
				if(!player.isInvisible() ||
						!player.capabilities.allowFlying || 
						!player.capabilities.disableDamage){
					player.setInvisible(true);
					player.capabilities.allowFlying = true;
					player.capabilities.disableDamage = true;
					
					PacketCustom adminPacket = new PacketCustom(Resources.MOD_ID, ClientPacketHandlerUtil.PLAYER_ADMIN);
					Contained.channel.sendTo(adminPacket.toPacket(), (EntityPlayerMP)player);
				}
			}
			
			//Check If Player Is In A Valid MiniGame Dimension
			if(!properties.isAdmin() 
					&& (MiniGameUtil.isPvP(player.dimension) || MiniGameUtil.isTreasure(player.dimension))){
				int dim = player.dimension;
				PlayerMiniGame miniGame = PlayerMiniGame.get(player.dimension);
				
				if(miniGame == null 
						|| (miniGame != null && miniGame.getGameID() != properties.gameID)){
					PlayerTeamIndividual pdata = PlayerTeamIndividual.get(player.getDisplayName());
					Util.displayMessage(player, "The MiniGame You Were In Has Ended, We Are Sending You Back To Where You Belong!");
					Util.travelToDimension(Resources.OVERWORLD, player, true);
					
					PacketCustom miniGamePacket = new PacketCustom(Resources.MOD_ID, ClientPacketHandlerUtil.MINIGAME_ENDED);
					miniGamePacket.writeInt(dim);
					Contained.channel.sendTo(miniGamePacket.toPacket(), (EntityPlayerMP) player);
					
					//Update Player Stats
					if(MiniGameUtil.isPvP(dim)){
						PacketCustom syncScore = new PacketCustom(Resources.MOD_ID, ClientPacketHandlerUtil.SYNC_PVP_STATS);
						syncScore.writeInt(properties.pvpWon);
						syncScore.writeInt(properties.pvpLost);
						syncScore.writeInt(properties.kills);
						syncScore.writeInt(properties.deaths);
						Contained.channel.sendTo(syncScore.toPacket(), (EntityPlayerMP) player);
					}else if(MiniGameUtil.isTreasure(dim)){
						PacketCustom syncScore = new PacketCustom(Resources.MOD_ID, ClientPacketHandlerUtil.SYNC_TEASURE_STATS);
						syncScore.writeInt(properties.treasureWon);
						syncScore.writeInt(properties.treasureLost);
						syncScore.writeInt(properties.treasuresOpened);
						Contained.channel.sendTo(syncScore.toPacket(), (EntityPlayerMP) player);
					}
				}
			}
			
			//Intermittently sync data logging information with the client
			//so the visualizations can be updated.
			if (player != null && Math.random() <= 1.0/20.0) {
				int[] occupationData = ExtendedPlayer.get(player).getOccupationValues();
				
				if (player instanceof EntityPlayerMP) {
					PacketCustom occPacket = new PacketCustom(Resources.MOD_ID, ClientPacketHandlerUtil.OCCUPATIONAL_DATA);
					for(int i=0; i<occupationData.length; i++)
						occPacket.writeInt(occupationData[i]);
					Contained.channel.sendTo(occPacket.toPacket(), (EntityPlayerMP)player);
					
					PacketCustom usePacket = new PacketCustom(Resources.MOD_ID, ClientPacketHandlerUtil.ITEM_USAGE_DATA);
					usePacket.writeInt(ExtendedPlayer.get(player).usedOwnItems);
					usePacket.writeInt(ExtendedPlayer.get(player).usedOthersItems);
					usePacket.writeInt(ExtendedPlayer.get(player).usedByOthers);
					Contained.channel.sendTo(usePacket.toPacket(), (EntityPlayerMP)player);
					
					PlayerTeamIndividual pdata = PlayerTeamIndividual.get(player);
					if(pdata.surveyResponses.progress < SurveyData.getSurveyLength() && Resources.MANDATORY_SURVEY){
						if(!player.isInvisible() || !player.capabilities.disableDamage){
							player.setInvisible(true);
							player.capabilities.disableDamage = true;
						}
						
						PacketCustom perkPacket = new PacketCustom(Resources.MOD_ID, ClientPacketHandlerUtil.START_SURVEY);
						Contained.channel.sendTo(perkPacket.toPacket(), (EntityPlayerMP) player);
					} else if(!ExtendedPlayer.get(player).isAdmin() 
							&& (player.isInvisible() || player.capabilities.disableDamage)){
						player.setInvisible(false);
						player.capabilities.disableDamage = false;
						
						PacketCustom perkPacket = new PacketCustom(Resources.MOD_ID, ClientPacketHandlerUtil.PLAYER_NORMAL);
						Contained.channel.sendTo(perkPacket.toPacket(), (EntityPlayerMP) player);
					}
				}
			}
			
			//Update items in a player's inventory which
			//have no owner to be owned by them
			if (player != null) {
				ItemStack[] inventory = player.inventory.mainInventory;
				for(int i=0; i<inventory.length; i++)
					if (inventory[i] != null) {
						if (processNewOwnership(player, inventory[i], false)) {
							event.entity.worldObj.spawnEntityInWorld(new EntityItem(event.entity.worldObj, 
									event.entity.posX, event.entity.posY+1, event.entity.posZ, 
									inventory[i]));
							inventory[i] = null;
						}
					}
			}
		}
	}
	
	@SubscribeEvent
	public void onEntityDeath(LivingDeathEvent event) {
		Entity source = event.source.getSourceOfDamage();
		if (event.entityLiving == null || source == null || event.entityLiving.worldObj.isRemote)
			return;
		
		// Players in teams should drop anti-territory gems when they are killed by other players.
		if (event.entityLiving instanceof EntityPlayer && source instanceof EntityPlayer) {
			EntityPlayer killed = (EntityPlayer)event.entityLiving;
			EntityPlayer killer = (EntityPlayer)source;
			PlayerTeamIndividual playerData = PlayerTeamIndividual.get(killed);
			if (playerData.teamID != null) {
				int amount = 1;
				if (MiniGameUtil.isPvP(event.entityLiving.dimension)) {
					int territoryCount = (int)Math.pow(Contained.configs.pvpTerritorySize*2+1, 2);
					amount = Util.randomRange(
							(int)Math.ceil(Math.sqrt(territoryCount/2D)), 
							(int)Math.sqrt(territoryCount));
				}
				else {
					if (playerData.isLeader) //Leaders drop more anti-gems on death.
						amount = 4;
				}
				ItemStack toDrop = new ItemStack(ItemTerritory.removeTerritory, amount);
				NBTTagCompound itemData = Data.getTagCompound(toDrop);
				itemData.setString("teamOwner", playerData.teamID);
				toDrop.setTagCompound(itemData);
				processNewOwnership(killer, toDrop, true);
				killed.worldObj.spawnEntityInWorld(new EntityItem(killed.worldObj, killed.posX, killed.posY+1, killed.posZ, toDrop));
			}
		}
		
		//Players should drop only a small portion of their inventory on death.
		if (event.entityLiving instanceof EntityPlayer && source instanceof EntityLivingBase) {
			EntityPlayer killed = (EntityPlayer)event.entityLiving;
			ArrayList<Integer> definedSlots = new ArrayList<Integer>();
			for(int i=0; i<killed.inventory.mainInventory.length; i++) {
				if (killed.inventory.mainInventory[i] != null)
					definedSlots.add(i);
			}
			Collections.shuffle(definedSlots);
			int numStacksToRemove = (int)Math.ceil((float)definedSlots.size()/8F);
			for (int i=0; i<numStacksToRemove; i++) {
				ItemStack toDrop = killed.inventory.mainInventory[definedSlots.get(i)];
				if (source instanceof EntityPlayer)
					processNewOwnership((EntityPlayer)source, toDrop, true);
				killed.worldObj.spawnEntityInWorld(new EntityItem(killed.worldObj, killed.posX, killed.posY+1, killed.posZ, toDrop));
				killed.inventory.mainInventory[definedSlots.get(i)] = null;
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
				int dimID = ev.entityPlayer.worldObj.provider.dimensionId;
				if (!Contained.getTerritoryMap(dimID).containsKey(probe)
						|| !Contained.getTerritoryMap(dimID).get(probe).equals(playerData.teamID)) {
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
	public boolean processNewOwnership(EntityPlayer newOwner, ItemStack item, boolean forceOwner) {
		//First make sure this item isn't owned by someone already...
		NBTTagCompound itemData = Data.getTagCompound(item);	
		String owner = itemData.getString("owner");
		if ((owner == null || owner.equals("") || forceOwner) && newOwner != null) {			
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
			
			//If the item is a spawn egg, set its display name to match the owner.
			if (item.getItem() instanceof ItemMonsterPlacer)
				item.setStackDisplayName(newOwner.getDisplayName());
			
			return true;
		}
		return false;
	}
	
	@SubscribeEvent
	//When an unowned item is collected, it is owned by the collector.
	public void onItemCollected(EntityItemPickupEvent event) {
		if (event.entityPlayer != null)
			processNewOwnership(event.entityPlayer, event.item.getEntityItem(), false);
	}
	
	@SubscribeEvent
	//When a block is harvested, the dropped items are owned by the harvester.
	public void onItemHarvested(HarvestDropsEvent event) {
		ArrayList<ItemStack> drops = event.drops;
		for (ItemStack stack : drops)
			processNewOwnership(event.harvester, stack, false);
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
				processNewOwnership(killer, item.getEntityItem(), false);
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

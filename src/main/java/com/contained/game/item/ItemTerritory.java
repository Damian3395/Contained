package com.contained.game.item;

import java.awt.Point;
import java.util.List;

import com.contained.game.Contained;
import com.contained.game.data.Data;
import com.contained.game.network.ClientPacketHandlerUtil;
import com.contained.game.user.PlayerTeam;
import com.contained.game.user.PlayerTeamIndividual;
import com.contained.game.util.ErrorCase;
import com.contained.game.util.Resources;
import com.contained.game.util.Util;

import net.minecraft.block.*;
import net.minecraft.creativetab.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.*;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Items for claiming territory.
 */
public class ItemTerritory {
	public ItemTerritory(){}
	public static Item addTerritory;
	public static final String addTerritoryName = "ItemClaimTerritory";
	public static Item removeTerritory;
	public static final String removeTerritoryName = "ItemRemoveTerritory";

	static {
		addTerritory = (new TerritoryGem());
		Item.itemRegistry.addObject(471, addTerritoryName, addTerritory);
		removeTerritory = (new AntiTerritoryGem());
		Item.itemRegistry.addObject(472, removeTerritoryName, removeTerritory);
	}

	/**
	 * Single-use item, claims a single block of territory... can only claim territory
	 * adjacent to the team's currently owned territory.
	 */
	public static class TerritoryGem extends Item implements BlockInteractItem {
		public TerritoryGem(){
			setMaxDamage(0);
			maxStackSize = 64;
			setUnlocalizedName(addTerritoryName);
			setTextureName(Resources.MOD_ID+":territory_gem");
			setCreativeTab(CreativeTabs.tabTools);
		}

		public float getStrVsBlock(ItemStack par1ItemStack, Block par2Block) {
			return 1.0F;
		}
		
		public void onBlockInteract(EntityPlayer p, int x, int y, int z, ItemStack data) {
			if (!p.worldObj.isRemote) {
				PlayerTeamIndividual playerData = PlayerTeamIndividual.get(p);
				Point toClaim = new Point(x,z);
				Point probe = new Point();
				
				ErrorCase.Error testClaim = 
						canClaim(playerData.teamID, toClaim, probe, p.dimension);
				
				if (testClaim == ErrorCase.Error.TEAM_ONLY) {
					Util.displayError(p, "You must be in a team to use this item.");
					return;
				} 
				if (testClaim == ErrorCase.Error.ALREADY_OWNED) {
					String ownedBy = Contained.getTerritoryMap(p.dimension).get(toClaim);
					if (!ownedBy.equals(playerData.teamID)) {
						Util.displayError(p, "You can't claim this area, it already belongs to a team.");
					}
					return;
				}
				if (testClaim == ErrorCase.Error.ADJACENT_ONLY) 
				{
					Util.displayError(p, "This item can only claim territory blocks directly adjacent to your team's current territory.");
					return;
				}
				
				if (!p.capabilities.isCreativeMode)
					p.inventory.consumeInventoryItem(ItemTerritory.addTerritory);
				Contained.getTerritoryMap(p.dimension).put(toClaim, playerData.teamID);
				Contained.channel.sendToAll(ClientPacketHandlerUtil.packetAddTerrBlock(playerData.teamID, x, z).toPacket());
			}
		}
	}
	
	/**
	 * Single-use item, removes a single block of territory from another team.
	 * Can only remove blocks on the very edge of their territory. 
	 */
	public static class AntiTerritoryGem extends Item implements BlockInteractItem  {
		public AntiTerritoryGem(){
			setMaxDamage(0);
			maxStackSize = 64;
			setUnlocalizedName(removeTerritoryName);
			setTextureName(Resources.MOD_ID+":antiterritory_gem");
			setCreativeTab(CreativeTabs.tabTools);
		}

		public float getStrVsBlock(ItemStack par1ItemStack, Block par2Block) {
			return 1.0F;
		}
		
		public void onBlockInteract(EntityPlayer p, int x, int y, int z, ItemStack data) {
			if (!p.worldObj.isRemote) {
				Point blockToRemove = new Point(x,z);
				Point probe = new Point();
				
				if (Contained.getTerritoryMap(p.dimension).containsKey(blockToRemove)) {					
					//The team this item is allowed to remove the land of.
					NBTTagCompound itemData = Data.getTagCompound(data);	
					String teamToRemove = itemData.getString("teamOwner");
					ErrorCase.Error testRemove = 
							canRemove(teamToRemove, blockToRemove, probe, p.dimension);				
					
					if (testRemove == ErrorCase.Error.WRONG_TEAM) {
						Util.displayError(p, "This item can only remove territory from the team it's linked to.");
						return;
					}
					if (testRemove == ErrorCase.Error.ADJACENT_ONLY) {
						Util.displayError(p, "This item can only remove territory at the edge of the team's claimed land.");
						return;
					}
					
					PlayerTeamIndividual playerData = PlayerTeamIndividual.get(p);
					if (playerData.teamID.equals(teamToRemove)) {
						//If removing your own team's territory, refund 1 experience level.
						p.experienceLevel += 1;
					}
					if (!p.capabilities.isCreativeMode)
						p.inventory.consumeInventoryItem(ItemTerritory.removeTerritory);
					Contained.getTerritoryMap(p.dimension).remove(blockToRemove);
					Contained.channel.sendToAll(ClientPacketHandlerUtil.packetRemoveTerrBlock(x, z).toPacket());
				}
			}
		}
		
	    @Override
	    // Display the linked team in the hover tooltip.
		public void addInformation(ItemStack itemStack, EntityPlayer player, List list, boolean bool)
		{
			super.addInformation(itemStack, player, list, bool);
			
			String teamName = "[Disbanded Team]";
			String formatCode = "";
			
			NBTTagCompound itemData = Data.getTagCompound(itemStack);	
			String teamToRemove = itemData.getString("teamOwner");
			if (teamToRemove != null && !teamToRemove.equals("")) {
				PlayerTeam team = PlayerTeam.get(teamToRemove, player.dimension);
				if (team != null) {
					teamName = team.displayName;
					formatCode = team.getFormatCode();
				}
			}
			
			list.add("Linked Team: "+formatCode+"§l"+teamName);
		}
	}
	
	/**
	 * Does this area of land satisfy all the rules required to be claimed
	 * by a territory gem item? (Possible Errors: TEAM_ONLY, ALREADY_OWNED, ADJACENT_ONLY)
	 */
	public static ErrorCase.Error canClaim(String teamID, Point toClaim, Point probe, int dimID) {
		if (teamID == null)
			return ErrorCase.Error.TEAM_ONLY;
		
		if (Contained.getTerritoryMap(dimID).containsKey(toClaim))
			return ErrorCase.Error.ALREADY_OWNED;
		
		boolean foundAdj = false;
		for(int i=-1; i<=1; i+=2) {
			for(int j=0; j<=1; j++) {
				if (j == 0) {
					probe.x = toClaim.x+i;
					probe.y = toClaim.y;
				} else {
					probe.x = toClaim.x;
					probe.y = toClaim.y+i;
				}
				if (Contained.getTerritoryMap(dimID).containsKey(probe)) {
					foundAdj = true;
					break;
				}
			}
			if (foundAdj)
				break;
		}
		if (!foundAdj)
			return ErrorCase.Error.ADJACENT_ONLY;
		
		return ErrorCase.Error.NONE;
	}
	
	
	/**
	 * Does this area of land satisfy all the rules required to be removed
	 * by a territory gem item? (Possible Errors: TEAM_ONLY, WRONG_TEAM, ADJACENT_ONLY)
	 */
	public static ErrorCase.Error canRemove(String teamID, Point toRemove, Point probe, int dimID) {
		if (Contained.getTerritoryMap(dimID).containsKey(toRemove)) {
			if (teamID != null && !Contained.getTerritoryMap(dimID).get(toRemove).equals(teamID))
				return ErrorCase.Error.WRONG_TEAM;
		}
		else
			return ErrorCase.Error.TEAM_ONLY;
		
		String teamOwnedBy = teamID;
		if (teamOwnedBy == null)
			teamOwnedBy = Contained.getTerritoryMap(dimID).get(toRemove);
		
		boolean foundAdj = false;
		for(int i=-1; i<=1; i+=2) {
			for(int j=0; j<=1; j++) {
				if (j == 0) {
					probe.x = toRemove.x+i;
					probe.y = toRemove.y;
				} else {
					probe.x = toRemove.x;
					probe.y = toRemove.y+i;
				}
				String teamProbed = Contained.getTerritoryMap(dimID).get(probe);
				if (teamProbed == null || !teamProbed.equals(teamOwnedBy)) {
					foundAdj = true;
					break;
				}
			}
			if (foundAdj)
				break;
		}
		if (!foundAdj)
			return ErrorCase.Error.ADJACENT_ONLY;
		
		return ErrorCase.Error.NONE;
	}
}

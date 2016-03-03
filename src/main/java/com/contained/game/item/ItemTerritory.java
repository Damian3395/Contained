package com.contained.game.item;

import java.awt.Point;
import java.util.List;

import com.contained.game.Contained;
import com.contained.game.data.Data;
import com.contained.game.network.ClientPacketHandler;
import com.contained.game.user.PlayerTeam;
import com.contained.game.user.PlayerTeamIndividual;
import com.contained.game.util.Resources;

import net.minecraft.block.*;
import net.minecraft.creativetab.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;

/**
 * Items for claiming territory.
 */
public class ItemTerritory {
	public ItemTerritory(){}
	public static Item addTerritory;
	public static Item removeTerritory;

	static {
		addTerritory = (new TerritoryGem());
		Item.itemRegistry.addObject(471, "ItemClaimTerritory", addTerritory);
		removeTerritory = (new AntiTerritoryGem());
		Item.itemRegistry.addObject(472, "ItemRemoveTerritory", removeTerritory);
	}

	/**
	 * Single-use item, claims a single block of territory... can only claim territory
	 * adjacent to the team's currently owned territory.
	 */
	public static class TerritoryGem extends Item implements BlockInteractItem {
		public TerritoryGem(){
			setMaxDamage(0);
			maxStackSize = 64;
			setUnlocalizedName("ItemClaimTerritory");
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
				
				if (playerData.teamID == null) {
					p.addChatMessage(new ChatComponentText(
							"§cYou must be in a team to use this item."));
					return;
				} 
				if (Contained.territoryData.containsKey(toClaim)) {
					p.addChatMessage(new ChatComponentText(
							"§cYou can't claim this area, it already belongs to a team."));
					return;
				}
				if (!(Contained.territoryData.containsKey(new Point(toClaim.x+1, toClaim.y))
				   || Contained.territoryData.containsKey(new Point(toClaim.x-1, toClaim.y))
				   || Contained.territoryData.containsKey(new Point(toClaim.x,   toClaim.y+1))
				   || Contained.territoryData.containsKey(new Point(toClaim.x,   toClaim.y-1)))) 
				{
					p.addChatMessage(new ChatComponentText(
							"§cThis item can only claim territory blocks directly adjacent to your team's current territory."));
					return;
				}
				
				if (!p.capabilities.isCreativeMode)
					p.inventory.consumeInventoryItem(ItemTerritory.addTerritory);
				Contained.territoryData.put(toClaim, playerData.teamID);
				ClientPacketHandler.packetAddTerrBlock(playerData.teamID, x, z).sendToClients();
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
			setUnlocalizedName("ItemRemoveTerritory");
			setTextureName(Resources.MOD_ID+":antiterritory_gem");
			setCreativeTab(CreativeTabs.tabTools);
		}

		public float getStrVsBlock(ItemStack par1ItemStack, Block par2Block) {
			return 1.0F;
		}
		
		public void onBlockInteract(EntityPlayer p, int x, int y, int z, ItemStack data) {
			if (!p.worldObj.isRemote) {
				Point blockToRemove = new Point(x,z);
				if (Contained.territoryData.containsKey(blockToRemove)) {
					//The team that owns this unit of land.
					String teamOwnedBy = Contained.territoryData.get(blockToRemove);
					
					//The team this item is allowed to remove the land of.
					NBTTagCompound itemData = Data.getTagCompound(data);	
					String teamToRemove = itemData.getString("teamOwner");
					if (teamToRemove == null || teamToRemove.equals(""))
						teamToRemove = "";
					
					if (!teamToRemove.equals(teamOwnedBy)) {
						p.addChatMessage(new ChatComponentText(
								"§cThis item can only remove territory from the team it's linked to."));
						return;
					}
					
					String ownedNorth = Contained.territoryData.get(new Point(blockToRemove.x, blockToRemove.y+1));
					String ownedSouth = Contained.territoryData.get(new Point(blockToRemove.x, blockToRemove.y-1));
					String ownedEast  = Contained.territoryData.get(new Point(blockToRemove.x-1, blockToRemove.y));
					String ownedWest  = Contained.territoryData.get(new Point(blockToRemove.x+1, blockToRemove.y));
					if (ownedNorth != null && ownedNorth.equals(teamToRemove)
							&& ownedSouth != null && ownedSouth.equals(teamToRemove)
							&& ownedEast  != null && ownedEast.equals(teamToRemove)
							&& ownedWest  != null && ownedWest.equals(teamToRemove)) 
						{
							p.addChatMessage(new ChatComponentText(
									"§cThis item can only remove territory at the edge of the team's claimed land."));
							return;
						}
					
					if (!p.capabilities.isCreativeMode)
						p.inventory.consumeInventoryItem(ItemTerritory.removeTerritory);
					Contained.territoryData.remove(blockToRemove);
					ClientPacketHandler.packetRemoveTerrBlock(x, z).sendToClients();
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
				PlayerTeam team = PlayerTeam.get(teamToRemove);
				if (team != null) {
					teamName = team.displayName;
					formatCode = team.getFormatCode();
				}
			}
			
			list.add("Linked Team: "+formatCode+"§l"+teamName);
		}
	}
}

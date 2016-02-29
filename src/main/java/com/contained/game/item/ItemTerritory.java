package com.contained.game.item;

import java.awt.Point;

import com.contained.game.Contained;
import com.contained.game.network.ClientPacketHandler;
import com.contained.game.user.PlayerTeamIndividual;

import net.minecraft.block.*;
import net.minecraft.creativetab.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.*;

/**
 * Items for claiming territory.
 */
public class ItemTerritory {
	public ItemTerritory(){}
	public static Item addTerritory;
	public static Item removeTerritory;

	static {
		addTerritory = (new TerritoryRod());
		Item.itemRegistry.addObject(471, "ItemClaimTerritory", addTerritory);
		removeTerritory = (new AntiTerritoryRod());
		Item.itemRegistry.addObject(472, "ItemRemoveTerritory", removeTerritory);
	}

	public static class TerritoryRod extends Item {
		public TerritoryRod(){
			setMaxDamage(0);
			maxStackSize = 64;
			setUnlocalizedName("ItemClaimTerritory");
			setTextureName("minecraft:stick");
			setCreativeTab(CreativeTabs.tabTools);
		}

		public float getStrVsBlock(ItemStack par1ItemStack, Block par2Block) {
			return 1.0F;
		}
		
		public void claimTerritory(EntityPlayer p, int x, int z) {
			if (!p.worldObj.isRemote) {
				PlayerTeamIndividual playerData = PlayerTeamIndividual.get(p);
				Point toClaim = new Point(x,z);
				
				if (playerData.teamID == null)
					return; //This player isn't in a team, so they can't claim territory.
				if (Contained.territoryData.containsKey(toClaim))
					return; //This territory has already been claimed... can't claim it.
				
				Contained.territoryData.put(toClaim, playerData.teamID);
				ClientPacketHandler.packetAddTerrBlock(playerData.teamID, x, z).sendToClients();
			}
		}
	}
	
	public static class AntiTerritoryRod extends Item {
		public AntiTerritoryRod(){
			setMaxDamage(0);
			maxStackSize = 64;
			setUnlocalizedName("ItemRemoveTerritory");
			setTextureName("minecraft:stick");
			setCreativeTab(CreativeTabs.tabTools);
		}

		public float getStrVsBlock(ItemStack par1ItemStack, Block par2Block) {
			return 1.0F;
		}
		
		public void removeTerritory(EntityPlayer p, int x, int z) {
			if (!p.worldObj.isRemote) {
				Point toRemove = new Point(x,z);
				if (Contained.territoryData.containsKey(toRemove)) {
					Contained.territoryData.remove(toRemove);
					ClientPacketHandler.packetRemoveTerrBlock(x, z).sendToClients();
				}
			}
		}
	}
}

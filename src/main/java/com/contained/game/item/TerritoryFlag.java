package com.contained.game.item;

import java.awt.Point;

import com.contained.game.Contained;
import com.contained.game.network.ClientPacketHandler;
import com.contained.game.user.PlayerTeam;
import com.contained.game.user.PlayerTeamIndividual;
import com.contained.game.util.Resources;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import mantle.lib.client.MantleClientRegistry;
import net.minecraft.block.*;
import net.minecraft.creativetab.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.*;
import net.minecraft.util.ChatComponentText;

/**
 * Item for claiming a new sector of territory.
 */
public class TerritoryFlag {
	public TerritoryFlag(){}
	public static Item instance;
	public static final String unlocName = "TerritoryFlag";
	public static final String texName = Resources.MOD_ID+":flag";

	static {
		instance = new ItemTerritoryFlag();
		Item.itemRegistry.addObject(471, unlocName, instance);
	}
	
	public static void defineRecipe(){
		ItemStack output = new ItemStack(instance, 1);
		ItemStack inputWool = new ItemStack(Blocks.wool, 1);
		ItemStack inputStick = new ItemStack(Items.stick, 1);
		
		GameRegistry.addRecipe(output, new Object[]{
			"100", 
			"100", 
			"1XX", 
			Character.valueOf('0'), inputWool,
			Character.valueOf('1'), inputStick 
		});
		GameRegistry.addRecipe(output, new Object[]{
			"001", 
			"001", 
			"XX1", 
			Character.valueOf('0'), inputWool,
			Character.valueOf('1'), inputStick 
		});
		
		// Data for use in the Mantle books.
		if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
			MantleClientRegistry.registerManualLargeRecipe(unlocName, output,
					inputWool, inputWool, inputStick,
					inputWool, inputWool, inputStick,
					null     , null     , inputStick);
			MantleClientRegistry.registerManualIcon(unlocName, output);
		}
	}

	public static class ItemTerritoryFlag extends Item implements BlockInteractItem {
		public ItemTerritoryFlag(){
			setMaxDamage(0);
			maxStackSize = 1;
			setUnlocalizedName(unlocName);
			setTextureName(texName);
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
				if (!playerData.isLeader) {
					p.addChatMessage(new ChatComponentText(
							"§cOnly team leaders can use this item to claim territory."));
					return;
				}
				if (Contained.territoryData.containsKey(toClaim)) {
					p.addChatMessage(new ChatComponentText(
							"§cYou can't claim this area, it already belongs to a team."));
					return;
				}
				
				int flagXPCost = Contained.configs.flagXPCost;
				PlayerTeam team = PlayerTeam.get(playerData.teamID);
				if (team.territoryCount() == 0)
					flagXPCost = 0; //First usage of the flag should be free.
				
				if (p.experienceLevel < flagXPCost) {
					p.addChatMessage(new ChatComponentText(
							"§cYou need at least "+Contained.configs.flagXPCost+" XP levels to claim the territory."));
					return;
				}
				
				p.addExperienceLevel(-Contained.configs.flagXPCost);
				Contained.territoryData.put(toClaim, playerData.teamID);
				Contained.channel.sendToAll(ClientPacketHandler.packetAddTerrBlock(playerData.teamID, x, z).toPacket());
				team.sendMessageToTeam(team.getFormatCode()+"[NOTICE] "+playerData.playerName+" started a new territory sector at ("+x+","+z+").");
			}
		}
	}
}

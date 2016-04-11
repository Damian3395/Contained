package com.contained.game.item;

import java.awt.Point;

import com.contained.game.util.Resources;
import com.contained.game.util.Util;
import com.contained.game.world.GenerateWorld;

import net.minecraft.block.*;
import net.minecraft.creativetab.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.*;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;

/**
 * Item for detecting the generated resource clusters in the current chunk.
 */
public class DowsingRod {
	public DowsingRod(){}
	public static Item instance;
	public static final String unlocName = "DowsingRod";
	public static final String texName = Resources.MOD_ID+":dowser";

	static {
		instance = new ItemDousingRod();
		Item.itemRegistry.addObject(473, unlocName, instance);
	}

	public static class ItemDousingRod extends Item {
		public ItemDousingRod(){
			setMaxDamage(0);
			maxStackSize = 1;
			setUnlocalizedName(unlocName);
			setTextureName(texName);
			setCreativeTab(CreativeTabs.tabTools);
		}

		public float getStrVsBlock(ItemStack par1ItemStack, Block par2Block) {
			return 1.0F;
		}
		
	     public ItemStack onItemRightClick(ItemStack stack, World w, EntityPlayer player)
	     {
	    	if (!w.isRemote) {
				int dimID = player.dimension;
				String out = "";
				Point p = new Point(player.getPlayerCoordinates().posX/16, 
									player.getPlayerCoordinates().posZ/16);
				
				if (Util.isOverworld(dimID)) {
					for(int i=0; i<GenerateWorld.defaultOreProperties.length; i++) {
						if (GenerateWorld.getOreProperties(dimID, i).spawnChunks.contains(p)) {
							String locName = GenerateWorld.getOreProperties(dimID, i).type.getLocalizedName();
							if (locName.contains("quartz"))
								locName="Quartz";
							if (out.equals(""))
								out += locName;
							else
								out += ", "+locName;
						}
					}
				}
				
				if (out.equals(""))
					player.addChatMessage(new ChatComponentText("Veins found in this chunk: None"));
				else
					player.addChatMessage(new ChatComponentText("Veins found in this chunk: "+out));
	    	} 
			return stack;
	     }
	}
}

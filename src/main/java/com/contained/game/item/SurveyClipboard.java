package com.contained.game.item;

import com.contained.game.Contained;
import com.contained.game.ui.GuiHandler;
import com.contained.game.util.Resources;

import net.minecraft.block.*;
import net.minecraft.creativetab.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.*;
import net.minecraft.world.World;

/**
 * Item for accessing the personality survey GUI.
 */
public class SurveyClipboard {
	public SurveyClipboard(){}
	public static Item instance;
	public static final String unlocName = "SurveyClipboard";
	public static final String texName = Resources.MOD_ID+":clipboard";

	static {
		instance = new ItemSurveyClipboard();
		Item.itemRegistry.addObject(472, unlocName, instance);
	}

	public static class ItemSurveyClipboard extends Item {
		public ItemSurveyClipboard(){
			setMaxDamage(0);
			maxStackSize = 64;
			setUnlocalizedName(unlocName);
			setTextureName(texName);
			setCreativeTab(CreativeTabs.tabTools);
		}

		public float getStrVsBlock(ItemStack par1ItemStack, Block par2Block) {
			return 1.0F;
		}
		
	     public ItemStack onItemRightClick(ItemStack stack, World w, EntityPlayer p)
	     {
	    	 p.openGui(Contained.instance, GuiHandler.GUI_SURVEY_ID, w, (int)p.posX, (int)p.posY, (int)p.posZ);
	         return stack;
	     }
	}
}

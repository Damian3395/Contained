package com.contained.game.item;

import com.contained.game.util.Resources;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import mantle.lib.client.MantleClientRegistry;
import net.minecraft.block.*;
import net.minecraft.creativetab.*;
import net.minecraft.item.*;

/**
 * Collectible items needed for winning the Treasure Hunt mini-game.
 */
public class TreasureGem {
	public TreasureGem(){}

	public static final int GREEN = 1;
	public static final int BLUE = 2;
	public static final int RED = 3;
	public static final int WHITE = 4;
	
	public static final int FULL = 1;
	public static final int CENTER = 2;
	public static final int LEFT = 3;
	public static final int TOP = 4;
	public static final int RIGHT = 5;
	public static final int BOTTOM = 6;
	
	public void preInit(FMLPreInitializationEvent event){
		for(int i=2;i<=6;i+=1) {
			for(int j=1;j<=4;j+=1)
			GameRegistry.registerItem(new ItemTreasureFragment(j,i), getUnlocalizedName(j,i));
		}
		
		for(int i=1;i<=4;i+=1)
			GameRegistry.registerItem(new ItemTreasureGem(i), getUnlocalizedName(i,FULL));
	}
	
	public static void defineRecipe(int color) {
		GameRegistry.addRecipe(
				new ItemStack((Item)Item.itemRegistry.getObject(Resources.MOD_ID+":"+getUnlocalizedName(color,FULL)), 1), new Object[]{
				"X1X", 
				"234", 
				"X5X", 
				Character.valueOf('1'), 
				new ItemStack((Item)Item.itemRegistry.getObject(Resources.MOD_ID+":"+getUnlocalizedName(color,TOP)), 1),
				Character.valueOf('2'), 
				new ItemStack((Item)Item.itemRegistry.getObject(Resources.MOD_ID+":"+getUnlocalizedName(color,LEFT)), 1),
				Character.valueOf('3'), 
				new ItemStack((Item)Item.itemRegistry.getObject(Resources.MOD_ID+":"+getUnlocalizedName(color,CENTER)), 1),
				Character.valueOf('4'), 
				new ItemStack((Item)Item.itemRegistry.getObject(Resources.MOD_ID+":"+getUnlocalizedName(color,RIGHT)), 1),
				Character.valueOf('5'), 
				new ItemStack((Item)Item.itemRegistry.getObject(Resources.MOD_ID+":"+getUnlocalizedName(color,BOTTOM)), 1)
			});
		
		ItemStack top = new ItemStack((Item)Item.itemRegistry.getObject(Resources.MOD_ID+":"+getUnlocalizedName(color,TOP)), 1);
		ItemStack left = new ItemStack((Item)Item.itemRegistry.getObject(Resources.MOD_ID+":"+getUnlocalizedName(color,LEFT)), 1);
		ItemStack center = new ItemStack((Item)Item.itemRegistry.getObject(Resources.MOD_ID+":"+getUnlocalizedName(color,CENTER)), 1);
		ItemStack right = new ItemStack((Item)Item.itemRegistry.getObject(Resources.MOD_ID+":"+getUnlocalizedName(color,RIGHT)), 1);
		ItemStack bottom = new ItemStack((Item)Item.itemRegistry.getObject(Resources.MOD_ID+":"+getUnlocalizedName(color,BOTTOM)), 1);
		ItemStack full = new ItemStack((Item)Item.itemRegistry.getObject(Resources.MOD_ID+":"+getUnlocalizedName(color,FULL)), 1);
		
		if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
			MantleClientRegistry.registerManualLargeRecipe(Resources.MOD_ID+":"+getUnlocalizedName(color,FULL), full,
					null, top, null,
					left, center, right,
					null, bottom, null);
			MantleClientRegistry.registerManualIcon(Resources.MOD_ID+":"+getUnlocalizedName(color,FULL), full);
		}
	}

	/**
	 * Single-use item, claims a single block of territory... can only claim territory
	 * adjacent to the team's currently owned territory.
	 */
	public static class ItemTreasureGem extends Item {
		public ItemTreasureGem(int color){
			setMaxDamage(0);
			maxStackSize = 64;
			setUnlocalizedName(TreasureGem.getUnlocalizedName(color, FULL));
			setTextureName(Resources.MOD_ID+":"+TreasureGem.getUnlocalizedName(color, FULL));
			setCreativeTab(CreativeTabs.tabTools);
		}

		public float getStrVsBlock(ItemStack par1ItemStack, Block par2Block) {
			return 1.0F;
		}
	}
	
	public static class ItemTreasureFragment extends Item {
		public ItemTreasureFragment(int color, int fragment){
			setMaxDamage(0);
			maxStackSize = 64;
			setUnlocalizedName(TreasureGem.getUnlocalizedName(color, fragment));
			setTextureName(Resources.MOD_ID+":"+TreasureGem.getUnlocalizedName(color, fragment));
			setCreativeTab(CreativeTabs.tabMaterials);
		}

		public float getStrVsBlock(ItemStack par1ItemStack, Block par2Block) {
			return 1.0F;
		}
	}
	
	public static String getUnlocalizedName(int color, int fragment) {
		return ("treasureGems"+fragment)+color;
	}
}

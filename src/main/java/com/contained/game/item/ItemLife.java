package com.contained.game.item;

import com.contained.game.Contained;
import com.contained.game.ui.GuiHandler;
import com.contained.game.util.Resources;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import mantle.lib.client.MantleClientRegistry;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerUseItemEvent;

public class ItemLife{
	public ItemLife(){}
	public static Item addLife, reviveLife;
	public static final String unlocNameApple = "AppleofLife";
	public static final String unlocNameStick = "StickofLife";
	public static final String texApple = Resources.MOD_ID+":lifeApple";
	public static final String texStick = Resources.MOD_ID+":reviveStick";

	static {
		addLife = new AppleLife(8, 0.2f, false);
		Item.itemRegistry.addObject(474, unlocNameApple, addLife);
		reviveLife = new StickLife();
		Item.itemRegistry.addObject(475, unlocNameStick, reviveLife);
	}
	
	//Server Side Check in PVPEvents PlayerUseItemEvent.Finish
	//Adds One Life Point in PVP
	public static class AppleLife extends ItemFood{
		public AppleLife(int healAmount, float saturationModifier, boolean wolvesFavorite) {
	        super(healAmount, saturationModifier, wolvesFavorite);
	        this.maxStackSize = 1;
	        this.setUnlocalizedName(unlocNameApple);
	        this.setTextureName(texApple);
	        this.setCreativeTab(CreativeTabs.tabFood);
	    }
	}
	
	//Allows Player To Select A Player In Their Team Who is Dead and Revive Them
	public static class StickLife extends Item{
		public StickLife(){
			this.maxStackSize = 1;
			this.setUnlocalizedName(unlocNameStick);
			this.setTextureName(texStick);
			this.setCreativeTab(CreativeTabs.tabTools);
		}
		
		public ItemStack onItemRightClick(ItemStack stack, World w, EntityPlayer p){
			p.openGui(Contained.instance, GuiHandler.GUI_REVIVE_PLAYER, w, (int) p.posX, (int) p.posY, (int) p.posZ); 
	        return stack;
	    }
	}
	
	public static void defineRecipe(){
		ItemStack outputApple = new ItemStack(addLife, 1);
		ItemStack outputStick = new ItemStack(reviveLife, 1);
		ItemStack inputApple = new ItemStack(Items.apple, 1);
		ItemStack inputDiamond = new ItemStack(Items.diamond, 1);
		ItemStack inputStick = new ItemStack(Items.stick, 1);
		ItemStack inputGold = new ItemStack(Items.gold_ingot, 2);
		
		GameRegistry.addRecipe(outputApple, new Object[]{
			"X2X", 
			"101", 
			"X2X", 
			Character.valueOf('0'), inputApple,
			Character.valueOf('1'), inputDiamond,
			Character.valueOf('2'), inputGold 
		});
		
		GameRegistry.addRecipe(outputStick, new Object[]{
				"202",
				"101",
				"202",
				Character.valueOf('0'), inputStick,
				Character.valueOf('1'), inputDiamond,
				Character.valueOf('2'), inputGold
		});
		
		// Data for use in the Mantle books.
		if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
			MantleClientRegistry.registerManualLargeRecipe(unlocNameApple, outputApple,
					null, 		inputGold,     null,
					inputDiamond, inputApple, inputDiamond,
					null        , inputGold,   null);
			MantleClientRegistry.registerManualIcon(unlocNameApple, outputApple);
			MantleClientRegistry.registerManualLargeRecipe(unlocNameStick, outputStick,
					inputGold, inputStick, inputGold,
					inputDiamond, inputStick, inputDiamond,
					inputGold, inputStick, inputGold);
		}
	}
}

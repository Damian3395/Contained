package com.contained.game.minigames;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;

import codechicken.lib.vec.BlockCoord;

import com.contained.game.item.ItemTerritory;
import com.contained.game.network.ClientPacketHandlerUtil;
import com.contained.game.util.Util;
import com.contained.game.world.block.TerritoryMachine;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.World;
import net.minecraftforge.common.ChestGenHooks;

public final class TreasureChestGenerator {	
	
	public static void definePossibleChestLoot(ChestGenHooks hook) {
		hook.setMin(4);
		hook.setMax(8);
		
		hook.addItem(new WeightedRandomChestContent(
				  new ItemStack(Items.cooked_beef, 1, 0), 1, 3, 100));
		hook.addItem(new WeightedRandomChestContent(
				  new ItemStack(Items.rotten_flesh, 1, 0), 3, 8, 100));
		hook.addItem(new WeightedRandomChestContent(
				  new ItemStack(Items.bone, 1, 0), 3, 8, 100));
		hook.addItem(new WeightedRandomChestContent(
				  new ItemStack(Items.bread, 1, 0), 1, 5, 100));
		hook.addItem(new WeightedRandomChestContent(
				  new ItemStack(Items.arrow, 1, 0), 4, 16, 100));
		hook.addItem(new WeightedRandomChestContent(
				  new ItemStack(Blocks.torch, 1, 0), 6, 24, 100));
		hook.addItem(new WeightedRandomChestContent(
				  new ItemStack(Blocks.tnt, 1, 0), 1, 4, 20));
		
		hook.addItem(new WeightedRandomChestContent(
				  new ItemStack(Items.bow, 1, 0), 1, 1, 50));
		hook.addItem(new WeightedRandomChestContent(
				  new ItemStack(Items.stone_sword, 1, 0), 1, 1, 50));	
		hook.addItem(new WeightedRandomChestContent(
				  new ItemStack(Items.iron_sword, 1, 0), 1, 1, 20));	
		hook.addItem(new WeightedRandomChestContent(
				  new ItemStack(Items.diamond_sword, 1, 0), 1, 1, 5));	
		
		hook.addItem(new WeightedRandomChestContent(
				  new ItemStack(Items.chainmail_helmet, 1, 0), 1, 1, 35));	
		hook.addItem(new WeightedRandomChestContent(
				  new ItemStack(Items.chainmail_boots, 1, 0), 1, 1, 35));	
		hook.addItem(new WeightedRandomChestContent(
				  new ItemStack(Items.chainmail_leggings, 1, 0), 1, 1, 35));	
		hook.addItem(new WeightedRandomChestContent(
				  new ItemStack(Items.chainmail_chestplate, 1, 0), 1, 1, 35));	
		
		hook.addItem(new WeightedRandomChestContent(
				  new ItemStack(Items.iron_helmet, 1, 0), 1, 1, 10));	
		hook.addItem(new WeightedRandomChestContent(
				  new ItemStack(Items.iron_boots, 1, 0), 1, 1, 10));	
		hook.addItem(new WeightedRandomChestContent(
				  new ItemStack(Items.iron_leggings, 1, 0), 1, 1, 10));	
		hook.addItem(new WeightedRandomChestContent(
				  new ItemStack(Items.iron_chestplate, 1, 0), 1, 1, 10));
		
		hook.addItem(new WeightedRandomChestContent(
				  new ItemStack(Items.diamond_helmet, 1, 0), 1, 1, 2));	
		hook.addItem(new WeightedRandomChestContent(
				  new ItemStack(Items.diamond_boots, 1, 0), 1, 1, 2));	
		hook.addItem(new WeightedRandomChestContent(
				  new ItemStack(Items.diamond_leggings, 1, 0), 1, 1, 2));	
		hook.addItem(new WeightedRandomChestContent(
				  new ItemStack(Items.diamond_chestplate, 1, 0), 1, 1, 2));	
		
		hook.addItem(new WeightedRandomChestContent(
				  new ItemStack(ItemTerritory.addTerritory, 1, 0), 4, 16, 25));
		hook.addItem(new WeightedRandomChestContent(
				  new ItemStack(TerritoryMachine.instance, 1, 0), 1, 1, 5));
		hook.addItem(new WeightedRandomChestContent(
				  new ItemStack(Items.experience_bottle, 1, 0), 1, 12, 50));
		
		//"Creeper", "Skeleton", "Spider", "Zombie", "Slime", "CaveSpider", "Witch", "Blaze"
		int[] spawnMeta = {50, 51, 52, 54, 55, 59, 66, 61};
		int[] spawnRarities = {10, 10, 20, 20, 30, 15, 5, 5};
		int[] spawnMax = {2, 2, 4, 4, 3, 4, 1, 1};
		for(int i=0; i<spawnMeta.length; i++) {
			hook.addItem(new WeightedRandomChestContent(
					new ItemStack(Items.spawn_egg, 1, spawnMeta[i]), 1, spawnMax[i], spawnRarities[i]));
		}
	}
	
	public static void generateChest(World w, int chestAmount, ChestGenHooks hook){
		if (w.isRemote)
			return;
		
		int x,y,z;
		Random r = new Random();		
		ArrayList<BlockCoord> generatedPoints = new ArrayList<BlockCoord>();
		for(int i=0;i<chestAmount;i++){
			Point randomSpawnPoint = Util.getRandomLocation(w);
			x=randomSpawnPoint.x;
			z=randomSpawnPoint.y;			
			y=w.getTopSolidOrLiquidBlock(x, z);		//coordinates to generate chests
			
			w.setBlock(x, y, z, Blocks.chest);		//generate a chest
			TileEntity chest = w.getTileEntity(x, y, z);	
			if (chest instanceof IInventory)
				WeightedRandomChestContent.generateChestContents(r, hook.getItems(r), (IInventory)chest, hook.getCount(r));
			generatedPoints.add(new BlockCoord(x, y, z));			
		}		
		ClientPacketHandlerUtil.addTreasureAndSync(w.provider.dimensionId, generatedPoints);
	}
}

package com.contained.game.util;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraftforge.common.ChestGenHooks;

public class Resources {
	public static final String MOD_ID = "contained";
	public static final String NAME = "Contained";
	public static final String VERSION = "1.0";
	
	public static int wastelandPadding = 5; //Number of "transition" chunks between world and wasteland.
	
	// If true, debug messages will be printed to the in-game chat.
	public static final boolean DEBUG_ENABLED = true;
	
	// If true, data will be logged to the connected MySQL database.
	public static final boolean LOGGING_ENABLED = false;
	
	// If true, then the player will be forced to fill out the survey
	// before being able to play on the server.
	public static final boolean MANDATORY_SURVEY = false;
	
	public static final int MIN_PVP_DIMID = 2;
	public static final int MAX_PVP_DIMID = 5;
	public static final int MIN_TREASURE_DIMID = 10;
	public static final int MAX_TREASURE_DIMID = 13;
	public static final int MAX_PVP_GAMES = MAX_PVP_DIMID-MIN_PVP_DIMID+1;
	public static final int MAX_TREASURE_GAMES = MAX_TREASURE_DIMID-MIN_TREASURE_DIMID+1;
	
	public static final int NETHER = -1;
	public static final int OVERWORLD = 0;
	public static final int MINIGAME = 1;
	public static final int PVP = 1;
	public static final int TREASURE = 2;
	
	public static final int GLOBAL_CHAT = 0;
	public static final int TEAM_CHAT = 1;
	
	public static final int COAL = 0;
	public static final int IRON = 1;
	public static final int GOLD = 2;
	public static final int LAPIS = 3;
	public static final int REDSTONE = 4;
	public static final int DIAMOND = 5;
	public static final int EMERALD = 6;
	public static final int QUARTZ = 7;
	public static final int GLOWSTONE = 8;
	public static final int NUM_MINERALS = 9;
	
	public static Block[] oreTypes = {
		Blocks.iron_ore,
		Blocks.gold_ore,
		Blocks.coal_ore,
		Blocks.diamond_ore,
		Blocks.redstone_ore,
		Blocks.emerald_ore,
		Blocks.lapis_ore
	};
	
	public static void definePossibleChestLoot(ChestGenHooks hook) {
		hook.setMin(2);
		hook.setMax(4);
		
		hook.addItem(new WeightedRandomChestContent(
				  new ItemStack(Items.cooked_beef, 1, 0), 1, 3, 100));
		hook.addItem(new WeightedRandomChestContent(
				  new ItemStack(Items.rotten_flesh, 1, 0), 3, 8, 100));
		hook.addItem(new WeightedRandomChestContent(
				  new ItemStack(Items.bone, 1, 0), 3, 8, 100));
		hook.addItem(new WeightedRandomChestContent(
				  new ItemStack(Blocks.web, 1, 0), 2, 6, 50));
		hook.addItem(new WeightedRandomChestContent(
				  new ItemStack(Items.bread, 1, 0), 1, 3, 100));
		hook.addItem(new WeightedRandomChestContent(
				  new ItemStack(Items.arrow, 1, 0), 4, 8, 100));
		hook.addItem(new WeightedRandomChestContent(
				  new ItemStack(Blocks.torch, 1, 0), 4, 12, 100));
		hook.addItem(new WeightedRandomChestContent(
				  new ItemStack(Blocks.tnt, 1, 0), 1, 3, 20));
		
		hook.addItem(new WeightedRandomChestContent(
				  new ItemStack(Items.bow, 1, 0), 1, 1, 20));
		hook.addItem(new WeightedRandomChestContent(
				  new ItemStack(Items.stone_sword, 1, 0), 1, 1, 50));	
		hook.addItem(new WeightedRandomChestContent(
				  new ItemStack(Items.iron_sword, 1, 0), 1, 1, 15));	
		hook.addItem(new WeightedRandomChestContent(
				  new ItemStack(Items.diamond_sword, 1, 0), 1, 1, 5));	
		
		hook.addItem(new WeightedRandomChestContent(
				  new ItemStack(Items.chainmail_helmet, 1, 0), 1, 1, 15));	
		hook.addItem(new WeightedRandomChestContent(
				  new ItemStack(Items.chainmail_boots, 1, 0), 1, 1, 15));	
		hook.addItem(new WeightedRandomChestContent(
				  new ItemStack(Items.chainmail_leggings, 1, 0), 1, 1, 15));	
		hook.addItem(new WeightedRandomChestContent(
				  new ItemStack(Items.chainmail_chestplate, 1, 0), 1, 1, 15));	
		
		hook.addItem(new WeightedRandomChestContent(
				  new ItemStack(Items.iron_helmet, 1, 0), 1, 1, 5));	
		hook.addItem(new WeightedRandomChestContent(
				  new ItemStack(Items.iron_boots, 1, 0), 1, 1, 5));	
		hook.addItem(new WeightedRandomChestContent(
				  new ItemStack(Items.iron_leggings, 1, 0), 1, 1, 5));	
		hook.addItem(new WeightedRandomChestContent(
				  new ItemStack(Items.iron_chestplate, 1, 0), 1, 1, 5));
		
		hook.addItem(new WeightedRandomChestContent(
				  new ItemStack(Items.diamond_helmet, 1, 0), 1, 1, 1));	
		hook.addItem(new WeightedRandomChestContent(
				  new ItemStack(Items.diamond_boots, 1, 0), 1, 1, 1));	
		hook.addItem(new WeightedRandomChestContent(
				  new ItemStack(Items.diamond_leggings, 1, 0), 1, 1, 1));	
		hook.addItem(new WeightedRandomChestContent(
				  new ItemStack(Items.diamond_chestplate, 1, 0), 1, 1, 1));	
		
		hook.addItem(new WeightedRandomChestContent(
				  new ItemStack(Items.experience_bottle, 1, 0), 1, 12, 50));
		
		//"Creeper", "Skeleton", "Zombie", "Witch"
		int[] spawnMeta = {50, 51, 54, 66};
		int[] spawnRarities = {10, 10, 20, 5};
		int[] spawnMax = {2, 2, 4, 1};
		for(int i=0; i<spawnMeta.length; i++) {
			hook.addItem(new WeightedRandomChestContent(
					new ItemStack(Items.spawn_egg, 1, spawnMeta[i]), 1, spawnMax[i], spawnRarities[i]));
		}
	}
}

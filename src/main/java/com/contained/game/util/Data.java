package com.contained.game.util;

import java.util.Map;
import com.google.common.collect.ImmutableMap;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class Data {
	/*
	 *  Occupational item groups
	 *  (Useful data for classifying people's actions into certain "jobs")
	 *  
	 *  The "rank" value in the OccupationRanks below are the weights
	 *  of each of these items -- this number is how much ownership of the
	 *  item counts towards evidence of the player belonging to the 
	 *  respective occupational group.
	*/
	public static final int MINING = 0;
	public static final int COOKING = 1;
	public static final int FARMING = 2;
	public static final int FISHING = 3;
	public static final int LUMBER = 4;
	public static final int FIGHTER = 5;
	public static final int POTION = 6;
	public static final int BUILDING = 7;
	public static final int MACHINE = 8;
	public static final int TRANSPORT = 9;
	
	public static class OccupationRank {
		public int occupationID;
		public int rank;
		
		public OccupationRank(int occupationID, int rank) {
			this.occupationID = occupationID;
			this.rank = rank;
		}
	}
	
	public static final Map<DataItemStack, OccupationRank> occupationMap = 
			ImmutableMap.<DataItemStack, OccupationRank>builder()
			
	//Mining
	.put(new DataItemStack(Items.coal), 		   new OccupationRank(MINING, 1))
	.put(new DataItemStack(Items.redstone), 	   new OccupationRank(MINING, 1))
	.put(new DataItemStack(Items.diamond), 		   new OccupationRank(MINING, 8))
	.put(new DataItemStack(Blocks.iron_ore), 	   new OccupationRank(MINING, 1))
	.put(new DataItemStack(Blocks.gold_ore), 	   new OccupationRank(MINING, 3))
	.put(new DataItemStack(Items.emerald), 		   new OccupationRank(MINING, 4))
	.put(new DataItemStack(Items.dye, 1, 4), 	   new OccupationRank(MINING, 1)) //lapis lazuli
	.put(new DataItemStack(Items.wooden_pickaxe),  new OccupationRank(MINING, 16))
	.put(new DataItemStack(Items.stone_pickaxe),   new OccupationRank(MINING, 16))
	.put(new DataItemStack(Items.iron_pickaxe),	   new OccupationRank(MINING, 24))
	.put(new DataItemStack(Items.golden_pickaxe),  new OccupationRank(MINING, 16))
	.put(new DataItemStack(Items.diamond_pickaxe), new OccupationRank(MINING, 32))
	
	//Cooking
	.put(new DataItemStack(Items.apple), 		   new OccupationRank(COOKING, 2))
	.put(new DataItemStack(Items.baked_potato),    new OccupationRank(COOKING, 2))
	.put(new DataItemStack(Items.beef), 		   new OccupationRank(COOKING, 2))
	.put(new DataItemStack(Items.chicken), 		   new OccupationRank(COOKING, 2))
	.put(new DataItemStack(Items.bread), 		   new OccupationRank(COOKING, 2))
	.put(new DataItemStack(Items.cake), 		   new OccupationRank(COOKING, 8))
	.put(new DataItemStack(Items.cookie), 		   new OccupationRank(COOKING, 1))
	.put(new DataItemStack(Items.cooked_beef), 	   new OccupationRank(COOKING, 2))
	.put(new DataItemStack(Items.cooked_chicken),  new OccupationRank(COOKING, 2))
	.put(new DataItemStack(Items.cooked_porkchop), new OccupationRank(COOKING, 2))
	.put(new DataItemStack(Items.cooked_fished),   new OccupationRank(COOKING, 3))
	.put(new DataItemStack(Items.melon), 		   new OccupationRank(COOKING, 1))
	.put(new DataItemStack(Items.mushroom_stew),   new OccupationRank(COOKING, 3))
	.put(new DataItemStack(Items.golden_apple),    new OccupationRank(COOKING, 6))
	.put(new DataItemStack(Items.porkchop), 	   new OccupationRank(COOKING, 2))
	.put(new DataItemStack(Items.pumpkin_pie), 	   new OccupationRank(COOKING, 3))
	
	//Farming
	.put(new DataItemStack(Items.wheat), 		   new OccupationRank(FARMING, 1))
	.put(new DataItemStack(Blocks.pumpkin), 	   new OccupationRank(FARMING, 2))
	.put(new DataItemStack(Items.reeds), 		   new OccupationRank(FARMING, 1))
	.put(new DataItemStack(Items.carrot), 		   new OccupationRank(FARMING, 1))
	.put(new DataItemStack(Items.potato), 		   new OccupationRank(FARMING, 1))
	.put(new DataItemStack(Items.poisonous_potato),new OccupationRank(FARMING, 2))
	.put(new DataItemStack(Items.wheat_seeds), 	   new OccupationRank(FARMING, 1))
	.put(new DataItemStack(Items.melon_seeds), 	   new OccupationRank(FARMING, 2))
	.put(new DataItemStack(Items.pumpkin_seeds),   new OccupationRank(FARMING, 2))
	.put(new DataItemStack(Items.dye, 1, 15), 	   new OccupationRank(FARMING, 1)) //bone meal
	.put(new DataItemStack(Blocks.brown_mushroom), new OccupationRank(FARMING, 1))
	.put(new DataItemStack(Blocks.red_mushroom),   new OccupationRank(FARMING, 1))
	.put(new DataItemStack(Items.wooden_hoe), 	   new OccupationRank(FARMING, 16))
	.put(new DataItemStack(Items.stone_hoe), 	   new OccupationRank(FARMING, 16))
	.put(new DataItemStack(Items.iron_hoe), 	   new OccupationRank(FARMING, 24))
	.put(new DataItemStack(Items.golden_hoe), 	   new OccupationRank(FARMING, 16))
	.put(new DataItemStack(Items.diamond_hoe), 	   new OccupationRank(FARMING, 32))
			
	//Fishing
	.put(new DataItemStack(Items.fishing_rod), 	   new OccupationRank(FISHING, 32))
	.put(new DataItemStack(Items.fish, 1, 0), 	   new OccupationRank(FISHING, 4))  //raw fish
	.put(new DataItemStack(Items.fish, 1, 1), 	   new OccupationRank(FISHING, 8))  //salmon
	.put(new DataItemStack(Items.fish, 1, 2), 	   new OccupationRank(FISHING, 16)) //clownfish
	.put(new DataItemStack(Items.fish, 1, 3), 	   new OccupationRank(FISHING, 16)) //pufferfish
	
	//Lumberjacking
	.put(new DataItemStack(Blocks.sapling), 	   new OccupationRank(LUMBER, 4))
	.put(new DataItemStack(Blocks.leaves), 		   new OccupationRank(LUMBER, 1))
	.put(new DataItemStack(Blocks.leaves2), 	   new OccupationRank(LUMBER, 1))
	.put(new DataItemStack(Blocks.log), 		   new OccupationRank(LUMBER, 2))
	.put(new DataItemStack(Blocks.log2),		   new OccupationRank(LUMBER, 2))
	.put(new DataItemStack(Items.wooden_axe), 	   new OccupationRank(LUMBER, 16))
	.put(new DataItemStack(Items.stone_axe), 	   new OccupationRank(LUMBER, 16))
	.put(new DataItemStack(Items.iron_axe), 	   new OccupationRank(LUMBER, 24))
	.put(new DataItemStack(Items.golden_axe), 	   new OccupationRank(LUMBER, 16))
	.put(new DataItemStack(Items.diamond_axe), 	   new OccupationRank(LUMBER, 32))
	
	//Fighting
	.put(new DataItemStack(Items.arrow), 		   new OccupationRank(FIGHTER, 1))
	.put(new DataItemStack(Items.rotten_flesh),    new OccupationRank(FIGHTER, 1))
	.put(new DataItemStack(Items.bone), 		   new OccupationRank(FIGHTER, 1))
	.put(new DataItemStack(Items.string), 		   new OccupationRank(FIGHTER, 1))
	.put(new DataItemStack(Items.spider_eye), 	   new OccupationRank(FIGHTER, 1))
	.put(new DataItemStack(Items.gunpowder), 	   new OccupationRank(FIGHTER, 1))
	.put(new DataItemStack(Items.blaze_rod), 	   new OccupationRank(FIGHTER, 2))
	.put(new DataItemStack(Items.ghast_tear), 	   new OccupationRank(FIGHTER, 6))
	.put(new DataItemStack(Items.ender_pearl), 	   new OccupationRank(FIGHTER, 6))
	.put(new DataItemStack(Items.slime_ball), 	   new OccupationRank(FIGHTER, 1))
	.put(new DataItemStack(Items.magma_cream),	   new OccupationRank(FIGHTER, 1))
	.put(new DataItemStack(Items.wooden_sword),    new OccupationRank(FIGHTER, 16))
	.put(new DataItemStack(Items.stone_sword),	   new OccupationRank(FIGHTER, 16))
	.put(new DataItemStack(Items.iron_sword),	   new OccupationRank(FIGHTER, 24))
	.put(new DataItemStack(Items.golden_sword),    new OccupationRank(FIGHTER, 16))
	.put(new DataItemStack(Items.diamond_sword),   new OccupationRank(FIGHTER, 32))
	.put(new DataItemStack(Items.bow), 		   	   new OccupationRank(FIGHTER, 16))
	.put(new DataItemStack(Items.leather_helmet),  new OccupationRank(FIGHTER, 8))
	.put(new DataItemStack(Items.leather_boots),   new OccupationRank(FIGHTER, 8))
	.put(new DataItemStack(Items.leather_leggings),      new OccupationRank(FIGHTER, 16))
	.put(new DataItemStack(Items.leather_chestplate),    new OccupationRank(FIGHTER, 16))
	.put(new DataItemStack(Items.golden_helmet),   new OccupationRank(FIGHTER, 8))
	.put(new DataItemStack(Items.golden_boots),    new OccupationRank(FIGHTER, 8))
	.put(new DataItemStack(Items.golden_leggings),       new OccupationRank(FIGHTER, 8))
	.put(new DataItemStack(Items.golden_chestplate),     new OccupationRank(FIGHTER, 8))
	.put(new DataItemStack(Items.chainmail_helmet),  new OccupationRank(FIGHTER, 8))
	.put(new DataItemStack(Items.chainmail_boots),   new OccupationRank(FIGHTER, 8))
	.put(new DataItemStack(Items.chainmail_leggings),    new OccupationRank(FIGHTER, 8))
	.put(new DataItemStack(Items.chainmail_chestplate),  new OccupationRank(FIGHTER, 8))
	.put(new DataItemStack(Items.iron_helmet),     new OccupationRank(FIGHTER, 16))
	.put(new DataItemStack(Items.iron_boots),      new OccupationRank(FIGHTER, 16))
	.put(new DataItemStack(Items.iron_leggings),   new OccupationRank(FIGHTER, 24))
	.put(new DataItemStack(Items.iron_chestplate), new OccupationRank(FIGHTER, 24))
	.put(new DataItemStack(Items.diamond_helmet),  new OccupationRank(FIGHTER, 32))
	.put(new DataItemStack(Items.diamond_boots),   new OccupationRank(FIGHTER, 32))
	.put(new DataItemStack(Items.diamond_leggings),      new OccupationRank(FIGHTER, 48))
	.put(new DataItemStack(Items.diamond_chestplate),    new OccupationRank(FIGHTER, 48))
	
	//Brewing
	.put(new DataItemStack(Items.brewing_stand),   new OccupationRank(POTION, 32))
	.put(new DataItemStack(Items.glass_bottle),    new OccupationRank(POTION, 3))
	.put(new DataItemStack(Items.potionitem),      new OccupationRank(POTION, 5))
	.put(new DataItemStack(Items.nether_wart), 	   new OccupationRank(POTION, 3))
	.put(new DataItemStack(Items.blaze_powder),    new OccupationRank(POTION, 1))
	.put(new DataItemStack(Items.fermented_spider_eye),  new OccupationRank(POTION, 4))
	.put(new DataItemStack(Items.sugar), 		   new OccupationRank(POTION, 1))
	.put(new DataItemStack(Items.glowstone_dust),  new OccupationRank(POTION, 1))
	.put(new DataItemStack(Items.golden_carrot),   new OccupationRank(POTION, 1))
	.put(new DataItemStack(Items.speckled_melon),  new OccupationRank(POTION, 4))
	
	//Building
	.put(new DataItemStack(Blocks.fence), 		   new OccupationRank(BUILDING, 1))
	.put(new DataItemStack(Blocks.ladder), 		   new OccupationRank(BUILDING, 1))
	.put(new DataItemStack(Blocks.birch_stairs),   new OccupationRank(BUILDING, 1))
	.put(new DataItemStack(Blocks.brick_block),    new OccupationRank(BUILDING, 1))
	.put(new DataItemStack(Blocks.brick_stairs),   new OccupationRank(BUILDING, 1))
	.put(new DataItemStack(Blocks.carpet), 		   new OccupationRank(BUILDING, 1))
	.put(new DataItemStack(Blocks.cobblestone_wall),     new OccupationRank(BUILDING, 1))
	.put(new DataItemStack(Blocks.fence_gate), 	   new OccupationRank(BUILDING, 1))
	.put(new DataItemStack(Blocks.glass_pane), 	   new OccupationRank(BUILDING, 1))
	.put(new DataItemStack(Blocks.iron_bars), 	   new OccupationRank(BUILDING, 1))
	.put(new DataItemStack(Items.iron_door), 	   new OccupationRank(BUILDING, 1))
	.put(new DataItemStack(Items.wooden_door),     new OccupationRank(BUILDING, 1))
	.put(new DataItemStack(Blocks.jungle_stairs),  new OccupationRank(BUILDING, 1))
	.put(new DataItemStack(Blocks.mossy_cobblestone),    new OccupationRank(BUILDING, 1))
	.put(new DataItemStack(Blocks.nether_brick_fence),   new OccupationRank(BUILDING, 1))
	.put(new DataItemStack(Blocks.nether_brick_stairs),  new OccupationRank(BUILDING, 1))
	.put(new DataItemStack(Blocks.oak_stairs), 	   new OccupationRank(BUILDING, 1))
	.put(new DataItemStack(Blocks.stone_slab), 	   new OccupationRank(BUILDING, 1))
	.put(new DataItemStack(Blocks.acacia_stairs),  new OccupationRank(BUILDING, 1))
	.put(new DataItemStack(Blocks.wooden_slab),    new OccupationRank(BUILDING, 1))
	.put(new DataItemStack(Items.sign),	    	   new OccupationRank(BUILDING, 1))
	.put(new DataItemStack(Blocks.spruce_stairs),  new OccupationRank(BUILDING, 1))
	.put(new DataItemStack(Blocks.sandstone_stairs), 	 new OccupationRank(BUILDING, 1))
	.put(new DataItemStack(Blocks.quartz_stairs),  new OccupationRank(BUILDING, 1))
	.put(new DataItemStack(Blocks.quartz_block, 1, 1),   new OccupationRank(BUILDING, 1))
	.put(new DataItemStack(Blocks.quartz_block, 1, 2),   new OccupationRank(BUILDING, 1))
	.put(new DataItemStack(Blocks.quartz_block, 1, 3),   new OccupationRank(BUILDING, 1))
	.put(new DataItemStack(Blocks.quartz_block, 1, 4),   new OccupationRank(BUILDING, 1))
	.put(new DataItemStack(Blocks.trapdoor), 	   new OccupationRank(BUILDING, 1))
	
	//Engineering
	.put(new DataItemStack(Blocks.redstone_lamp),  new OccupationRank(MACHINE, 2))
	.put(new DataItemStack(Blocks.redstone_torch), new OccupationRank(MACHINE, 2))
	.put(new DataItemStack(Blocks.lever), 		   new OccupationRank(MACHINE, 4))
	.put(new DataItemStack(Blocks.wooden_button),  new OccupationRank(MACHINE, 4))
	.put(new DataItemStack(Blocks.wooden_pressure_plate),new OccupationRank(MACHINE, 4))
	.put(new DataItemStack(Blocks.stone_button),   new OccupationRank(MACHINE, 4))
	.put(new DataItemStack(Blocks.stone_pressure_plate), new OccupationRank(MACHINE, 4))
	.put(new DataItemStack(Blocks.anvil), 		   new OccupationRank(MACHINE, 2))
	.put(new DataItemStack(Blocks.enchanting_table), 	 new OccupationRank(MACHINE, 2))
	.put(new DataItemStack(Blocks.crafting_table), new OccupationRank(MACHINE, 2))
	.put(new DataItemStack(Blocks.furnace), 	   new OccupationRank(MACHINE, 4))
	.put(new DataItemStack(Blocks.dispenser), 	   new OccupationRank(MACHINE, 8))
	.put(new DataItemStack(Items.cauldron), 	   new OccupationRank(MACHINE, 2))
	.put(new DataItemStack(Blocks.hopper), 		   new OccupationRank(MACHINE, 4))
	.put(new DataItemStack(Blocks.beacon), 		   new OccupationRank(MACHINE, 4))
	.put(new DataItemStack(Blocks.noteblock), 	   new OccupationRank(MACHINE, 4))
	.put(new DataItemStack(Blocks.dropper), 	   new OccupationRank(MACHINE, 4))
	.put(new DataItemStack(Blocks.ender_chest),    new OccupationRank(MACHINE, 2))
	.put(new DataItemStack(Blocks.chest), 	       new OccupationRank(MACHINE, 2))
	.put(new DataItemStack(Blocks.trapped_chest),  new OccupationRank(MACHINE, 3))
	.put(new DataItemStack(Blocks.tnt), 		   new OccupationRank(MACHINE, 1))
	.put(new DataItemStack(Blocks.tripwire_hook),  new OccupationRank(MACHINE, 3))
	.put(new DataItemStack(Items.comparator), 	   new OccupationRank(MACHINE, 3))
	.put(new DataItemStack(Items.repeater), 	   new OccupationRank(MACHINE, 3))
	.put(new DataItemStack(Blocks.piston), 		   new OccupationRank(MACHINE, 4))
	.put(new DataItemStack(Blocks.sticky_piston),  new OccupationRank(MACHINE, 4))
	.put(new DataItemStack(Blocks.command_block),  new OccupationRank(MACHINE, 16))
	.put(new DataItemStack(Blocks.daylight_detector),    new OccupationRank(MACHINE, 4))
	
	//Transportation
	.put(new DataItemStack(Blocks.rail), 		   new OccupationRank(TRANSPORT, 1))
	.put(new DataItemStack(Blocks.detector_rail),  new OccupationRank(TRANSPORT, 3))
	.put(new DataItemStack(Blocks.golden_rail),    new OccupationRank(TRANSPORT, 3))
	.put(new DataItemStack(Items.minecart), 	   new OccupationRank(TRANSPORT, 16))
	.put(new DataItemStack(Items.chest_minecart),  new OccupationRank(TRANSPORT, 16))
	.put(new DataItemStack(Items.furnace_minecart),new OccupationRank(TRANSPORT, 16))
	.put(new DataItemStack(Items.tnt_minecart),    new OccupationRank(TRANSPORT, 16))
	.put(new DataItemStack(Items.hopper_minecart), new OccupationRank(TRANSPORT, 16))
	.put(new DataItemStack(Items.command_block_minecart), new OccupationRank(TRANSPORT, 16))
	.put(new DataItemStack(Items.boat), 		   new OccupationRank(TRANSPORT, 16))
	.build();
	
	public static final String[] occupationNames = {
		"Mining", "Cooking", "Farming", "Fishing", "Lumberjacking"
		, "Fighting", "Brewing", "Building", "Engineering", "Transporting" 
	};
	
	//Randomly return true/false, with true occurring with 'bias' probability.	
	public static boolean biasedCoinFlip(float bias) {
		if (Math.random() <= bias)
			return true;
		else
			return false;
	}
	
	public static byte biasedCoinFlipByte(float bias) {
		if (Math.random() <= bias)
			return 1;
		else
			return 0;
	}
	
	//Get a random value uniformly distributed around a fixed point.
	public static double randomBoth(double range) {
		return (Math.random()*range*2.0)-range;
	}
	
	//Set all values in the array to zero.
	public static void zero(int[] vals) {
		for(int i=0; i<vals.length; i++)
			vals[i] = 0;
	}
	
	public static void zero(double[] vals) {
		for(int i=0; i<vals.length; i++)
			vals[i] = 0;
	}
	
	public static void zero(float[] vals) {
		for(int i=0; i<vals.length; i++)
			vals[i] = 0;
	}
	
	//Translate first element of array to zero, and shift all other
	//elements in array by the same amount as the first element.
	public static int[] convertBaseline(int[] array) {		
		int[] retArray = new int[array.length];
		for(int i=0; i<array.length; i++)
			retArray[i] = array[i]-array[0];
		return retArray;
	}
	
	public static double[] convertBaseline(double[] array) {		
		double[] retArray = new double[array.length];
		for(int i=0; i<array.length; i++)
			retArray[i] = array[i]-array[0];
		return retArray;
	}
	
	public static Double[] convertBaseline(Double[] array) {		
		Double[] retArray = new Double[array.length];
		for(int i=0; i<array.length; i++)
			retArray[i] = array[i]-array[0];
		return retArray;
	}
	
	public static float[] convertBaseline(float[] array) {		
		float[] retArray = new float[array.length];
		for(int i=0; i<array.length; i++)
			retArray[i] = array[i]-array[0];
		return retArray;
	}
	
	
	//Starting index for a sliding window over an array.
	public static int swStart(int length, int windowSize) {
		return Math.max(0, length-windowSize);
	}
	
	//Terminating index for a sliding window over an array.
	public static int swEnd(int length, int windowSize) {
		return Math.min(length,  swStart(length, windowSize)+windowSize);
	}
	
	//Modified version of getTagCompound that will return an
	//empty tag compound instead of null if the item has no tag.
	public static NBTTagCompound getTagCompound(ItemStack stack) {
		NBTTagCompound itemData = stack.getTagCompound();
		if (itemData == null)
			itemData = new NBTTagCompound();
		return itemData;
	}
}

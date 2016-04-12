package com.contained.game.util;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;

public class Resources {
	public static final String MOD_ID = "contained";
	public static final String NAME = "Contained";
	public static final String VERSION = "1.0";
	
	public static int wastelandPadding = 5; //Number of "transition" chunks between world and wasteland.
	
	public static final boolean DEBUG_ENABLED = true;
	public static final boolean LOGGING_ENABLED = false;
	
	public static final int MIN_PVP_DIMID = 2;
	public static final int MAX_PVP_DIMID = 5;
	public static final int MIN_TREASURE_DIMID = 10;
	public static final int MAX_TREASURE_DIMID = 13;
	
	public static final int MAX_MINI_GAME_TEAM_SIZE = 5;
	public static final int MAX_PVP_GAMES = 5;
	public static final int MAX_TREASURE_GAMES = 5;
	
	public static final int FREE_PLAY = -1;
	public static final int PVP_MODE = 0;
	public static final int TREASURE_MODE = 1;
	
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
}

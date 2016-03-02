package com.contained.game.util;

public class Resources {
	public static final String MOD_ID = "contained";
	public static final String NAME = "Contained";
	public static final String VERSION = "1.0";
	
	public static int worldRadius;
	public static int numWorldChunks = 0; //Total number of chunks in the finite world.
	public static int wastelandPadding = 5; //Number of "transition" chunks between world and wasteland.
	
	public static final boolean DEBUG_ENABLED = false;
	public static final boolean LOGGING_ENABLED = false; //Should log to the SQL database?
	
	public static final int COAL = 0;
	public static final int IRON = 1;
	public static final int GOLD = 2;
	public static final int LAPIS = 3;
	public static final int REDSTONE = 4;
	public static final int DIAMOND = 5;
	public static final int EMERALD = 6;
	public static final int NUM_MINERALS = 7;
}

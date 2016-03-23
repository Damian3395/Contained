package com.contained.game.world.biome;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

import com.contained.game.util.Load;
import com.contained.game.util.Resources;
import com.contained.game.util.Save;
import com.contained.game.util.Util;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.config.Configuration;

/**
 * Controls the spawning of biomes in the finite world.
 * (Particularly which biomes will be forced to appear, and in what quantity)
 */
public class BiomeProperties {

	public static final String biomeGenCategory = "biomeGen";
	public HashMap<BiomeGenBase, Integer> numOccurences; //Maps chunks to the number of times they should occur
	public HashMap<Point, BiomeGenBase> biomeMapping;
	private ArrayList<BiomeGenBase> biomeGenOrdering;    //The order of which to generate the forced biomes.
	private int numBiomeGens = 0;	//Total number of biome occurrences that must be forced.
	private int untilNextBiome = 0; //How many chunks of this biome must be generated until moving on.
	
	public BiomeProperties(HashMap<BiomeGenBase, Integer> biomeOccurences) {	
		this.numOccurences = biomeOccurences;
		biomeMapping = new HashMap<Point, BiomeGenBase>();
	}
	
	// Generate the mapping of biomes as they should appear over the chunks of
	// the finite world.
	public void generateMapping(World w) {
		//Initialize generation
		biomeGenOrdering = new ArrayList<BiomeGenBase>();
		numBiomeGens = 0;
		for(Integer val : numOccurences.values())
			numBiomeGens += val;
		for(BiomeGenBase biome : numOccurences.keySet()) {
			if (numOccurences.get(biome) != 0)
				biomeGenOrdering.add(biome);
		}
		Collections.shuffle(biomeGenOrdering);
		untilNextBiome = 0;
		biomeGenOrdering.add(0, null); //dummy: will be removed by the until initialization
		updateUntil();
		
		//Start the recursive mapping
		int spawnX = w.getSpawnPoint().posX/16;
		int spawnZ = w.getSpawnPoint().posZ/16;
		Point probe = new Point(0,0);
		recursiveBiomeMap(w, spawnX, spawnZ, probe);
	}
	
	private void recursiveBiomeMap(World w, int chunkX, int chunkZ, Point probe) {
		// Select adjacent chunks for generation in a random order.
		Integer[] exploreOrder = {0,1,2,3};
		Point myPoint = new Point(chunkX, chunkZ);
		Collections.shuffle(Arrays.asList(exploreOrder));
		
		// Fill in the keys going down the stack, but wait to fill in the values
		// until coming back up the stack.
		biomeMapping.put(myPoint, null);
		
		for(int i=0; i<=3; i++) {
			if (exploreOrder[i] == 0) { probe.x = chunkX; probe.y = chunkZ+1; } // Up
			if (exploreOrder[i] == 1) { probe.x = chunkX; probe.y = chunkZ-1; } // Down
			if (exploreOrder[i] == 2) { probe.x = chunkX-1; probe.y = chunkZ; } // Left
			if (exploreOrder[i] == 3) { probe.x = chunkX+1; probe.y = chunkZ; } // Right
				
			// If the desired adjacent chunk has not been assigned a biome yet,
			// explore that direction for additional mapping.
			if (!biomeMapping.containsKey(probe)
						&& Util.isWasteland(w, probe.x, probe.y) < 1) 
				recursiveBiomeMap(w, probe.x, probe.y, probe);
		}
		
		biomeMapping.put(myPoint, getNextBiome());
	}
	
	//Returns the next biome that should be used for generation.
	private BiomeGenBase getNextBiome() {
		BiomeGenBase next = null;
		if (biomeGenOrdering.size() == 0)
			return BiomeGenBase.plains;
		else {
			next = biomeGenOrdering.get(0);
			updateUntil();
		}
		return next;
	}
	
	//Tracks the amount of generated chunks until the next biome type should be used.
	private void updateUntil() {
		untilNextBiome--;
		if (untilNextBiome <= 0) {
			biomeGenOrdering.remove(0);
			if (biomeGenOrdering.size() != 0)
				this.untilNextBiome = (Resources.numWorldChunks/numBiomeGens)
											*numOccurences.get(biomeGenOrdering.get(0));
		}
	}
	
	// 
	// === File Handling ===
	//
	public void loadFromFile(World w) {
		NBTTagCompound ntc = Load.loadNBTFile("biomeSpawning.dat");
		biomeMapping.clear();
		
		//Save file found, load data from storage.
		if (ntc != null) {
			//Pre-Compile a list of biomes by their IDs, so we can load quickly from file.
			HashMap<Integer, BiomeGenBase> biomeIDs = new HashMap<Integer, BiomeGenBase>();			
			for(BiomeGenBase b : BiomeGenBase.getBiomeGenArray()) {
				if (b != null)
					biomeIDs.put(b.biomeID, b);
			}

			int[] biomeMapX =   ntc.getIntArray("biomeMapX");
			int[] biomeMapZ =   ntc.getIntArray("biomeMapZ");
			int[] biomeMapIDs = ntc.getIntArray("biomeMapIDs");
			for(int i=0; i<biomeMapX.length; i++) {
				biomeMapping.put(new Point(biomeMapX[i], biomeMapZ[i]), 
									biomeIDs.get(biomeMapIDs[i]));
			}
		} 
		
		//Save file not found, setup a fresh BiomeProperties object.
		else 
			generateMapping(w);
	}
	
	public void saveToFile() {
		NBTTagCompound ntc = new NBTTagCompound();		
		
		int[] biomeMapX   = new int[this.biomeMapping.size()];
		int[] biomeMapZ   = new int[this.biomeMapping.size()];
		int[] biomeMapIDs = new int[this.biomeMapping.size()];
		int ind = 0;
		for(Point p : this.biomeMapping.keySet()) {
			biomeMapX[ind] = p.x;
			biomeMapZ[ind] = p.y;
			biomeMapIDs[ind] = this.biomeMapping.get(p).biomeID;
			ind++;
		}
		ntc.setIntArray("biomeMapX",   biomeMapX);
		ntc.setIntArray("biomeMapZ",   biomeMapZ);
		ntc.setIntArray("biomeMapIDs", biomeMapIDs);
		
		Save.saveNBTFile("biomeSpawning.dat", ntc);
	}
	
	// Loads from or generates a config file for all of the biome spawning properties.
	public static BiomeProperties generateFromConfig(Configuration c) {	
		HashMap<BiomeGenBase, Integer> biomeOccurences = new HashMap<BiomeGenBase, Integer>();
		for (BiomeGenBase biome : BiomeGenBase.getBiomeGenArray()) {
			if (biome != null) {
				int defaultNum = 1;
				if (biome.equals(BiomeGenBase.extremeHillsEdge)
						|| biome.equals(BiomeGenBase.birchForestHills)
						|| biome.equals(BiomeGenBase.coldTaigaHills)
						|| biome.equals(BiomeGenBase.desertHills)
						|| biome.equals(BiomeGenBase.extremeHillsPlus)
						|| biome.equals(BiomeGenBase.forestHills)
						|| biome.equals(BiomeGenBase.hell)
						|| biome.equals(BiomeGenBase.jungleEdge)
						|| biome.equals(BiomeGenBase.jungleHills)
						|| biome.equals(BiomeGenBase.mushroomIslandShore)
						|| biome.equals(BiomeGenBase.megaTaigaHills)
						|| biome.equals(BiomeGenBase.mesaPlateau_F)
						|| biome.equals(BiomeGenBase.deepOcean)
						|| biome.equals(BiomeGenBase.frozenOcean)
						|| biome.equals(BiomeGenBase.ocean)
						|| biome.equals(BiomeGenBase.taigaHills)
						|| biome.equals(BiomeGenBase.savannaPlateau)
						|| biome.equals(BiomeGenBase.sky)
						|| biome.equals(WastelandBiome.biome)
						|| biome.biomeName.endsWith(" M")
						|| biome.biomeName.endsWith(" F"))
					defaultNum = 0;
				int occurences = c.get(biomeGenCategory, biome.biomeName+".spawnWeight", defaultNum).getInt();
				biomeOccurences.put(biome, occurences);
			}
		}
		return new BiomeProperties(biomeOccurences);
	}
	
	public static void writeConfigComment(Configuration c) {
		
	}
	
}

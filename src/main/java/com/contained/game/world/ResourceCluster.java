package com.contained.game.world;

import java.awt.Point;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import com.contained.game.util.Range;
import com.contained.game.util.Util;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.block.Block;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.config.Configuration;

public class ResourceCluster {
public static final String oreGenCategory = "oreGen";
	
	public Block type;
	public Range<Integer> veinSize;
	public Range<Integer> veinCount;
	public Range<Integer> clusterSize;
	public Range<Integer> clusterCount;
	public Range<Integer> spawnHeight;
	public ArrayList<Point> spawnChunks; // Pre-selected world chunks where this
	
	/**
	 * @param type 		       Block to spawn
	 * @param minVein          Minimum amount of that block in a "vein"
	 * @param maxVein   	   Maximum amount of that block in a "vein"
	 * @param minVeinCount     Minimum number of veins in a chunk
	 * @param maxVeinCount     Maximum number of veins in a chunk
	 * @param minClusterSize   Minimum number of chunks this cluster occupies
	 * @param maxClusterSize   Maximum number of chunks this cluster occupies
	 * @param minClusterCount  Minimum number of these clusters in the finite map
	 * @param maxClusterCount  Maximum number of these clusters in the finite map
	 * @param minYValue 	   Minimum world height to spawn clusters at
	 * @param maxYValue        Maximum world height to spawn clusters at
	 */
	public ResourceCluster(Block type, int minVein, int maxVein, 
			int minVeinCount, int maxVeinCount, int minClusterSize,
			int maxClusterSize,	int minClusterCount, int maxClusterCount,
			int minYValue, int maxYValue) {
		this.type = type;
		this.veinSize = new Range<Integer>(minVein, maxVein);
		this.veinCount = new Range<Integer>(minVeinCount, maxVeinCount);
		this.clusterSize = new Range<Integer>(minClusterSize, maxClusterSize);
		this.clusterCount = new Range<Integer>(minClusterCount, maxClusterCount);
		this.spawnHeight = new Range<Integer>(minYValue, maxYValue);
		this.spawnChunks = new ArrayList<Point>();
	}
	
	public ResourceCluster(ResourceCluster copy) {
		this.type = copy.type;
		this.veinSize = new Range<Integer>(copy.veinSize);
		this.veinCount = new Range<Integer>(copy.veinCount);
		this.clusterSize = new Range<Integer>(copy.clusterSize);
		this.clusterCount = new Range<Integer>(copy.clusterCount);
		this.spawnHeight = new Range<Integer>(copy.spawnHeight);
		this.spawnChunks = new ArrayList<Point>(copy.spawnChunks);
	}
	
	/**
	 *  Adds all the chunk locations in the finite world where this ore cluster
	 *  should do generation. (This function will be ignored if the chunk locations
	 *  have already been defined)
	 */
	public void determineAllChunks(World w, int worldSizeRadius) {
		// Currently using a naive restart method, but would be better
		// to implement this using a connected components algorithm. 
		Point probe = new Point(0,0);
		int allowedRestarts = 10;
		
		if (spawnChunks.size() == 0) {
			int numClusters = Util.randomRange(this.clusterCount.min(), this.clusterCount.max());
			for(int i=0; i<numClusters; i++) {
				ArrayList<Point> bestFound = new ArrayList<Point>();
				for(int j=0; j<allowedRestarts; j++) {
					int clustSize = Util.randomRange(this.clusterSize.min(), this.clusterSize.max());
					double startAng = Math.random()*2D*Math.PI;
					double startDist = Math.random()*(double)worldSizeRadius;
					int startX = w.getSpawnPoint().posX/16;
					int startZ = w.getSpawnPoint().posZ/16;
					startX += (int)(startDist*Math.cos(startAng));
					startZ += (int)(startDist*Math.sin(startAng));
					
					ArrayList<Point> candidateCluster = 
							identifyCluster(startX, startZ, clustSize, probe
									, new ArrayList<Point>(), w, worldSizeRadius);
					
					if (candidateCluster != null) {
						if (candidateCluster.size() >= this.clusterSize.min()) {
							bestFound = candidateCluster;
							break;
						} else if (candidateCluster.size() >= bestFound.size())
							bestFound = candidateCluster;
					}				
				}
				
				// Take the best candidate cluster, and add it to the spawn chunk list.
				for(Point p : bestFound)
					spawnChunks.add(p);
			}
		}
	}
	
	/**
	 * Determine a connected series of adjacent chunks to use as a cluster
	 * for generation, centered around a given starting chunk. This method 
	 * will avoid selecting any chunks that have already been generated of this
	 * block type previously. It will select a cluster of chunks equal to the 
	 * requested size -- however, note that if the finite world is too small, or
	 * the number of adjacent ungenerated chunks is too small, this size may not
	 * be able to be reached. In that case, will return the largest cluster it
	 * can find. 
	 */
	private ArrayList<Point> identifyCluster(int x, int z, int clustSize, Point probe, 
			ArrayList<Point> used, World w, int worldRadius) 
	{		
		// Select adjacent chunks for generation in a random order.
		Integer[] exploreOrder = {0,1,2,3};
		Collections.shuffle(Arrays.asList(exploreOrder));
		
		// Add the current cell to the list of candidate cells explored so far.
		ArrayList<Point> current = new ArrayList<Point>(used);
		current.add(new Point(x,z));
		if (current.size() == clustSize)
			return current;
		
		// If the current generation point has no unoccupied clusters that are of 
		// clustSize, return the largest cluster we managed to find.
		ArrayList<Point> suboptimalBest = null; 
		
		for(int i=0; i<=3; i++) {
			if (exploreOrder[i] == 0) { probe.x = x; probe.y = z+1; } // Up
			if (exploreOrder[i] == 1) { probe.x = x; probe.y = z-1; } // Down
			if (exploreOrder[i] == 2) { probe.x = x-1; probe.y = z; } // Left
			if (exploreOrder[i] == 3) { probe.x = x+1; probe.y = z; } // Right
				
			// If the desired adjacent chunk has not been generated with this
			// block type yet, let's explore it as a potential candidate.
			ArrayList<Point> nextExplore = null;
			if (!used.contains(probe) && !spawnChunks.contains(probe)
						&& Util.isWasteland(w, probe.x, probe.y) == 0) 
			{
				nextExplore = identifyCluster(probe.x, probe.y, clustSize, probe, 
											current, w, worldRadius);
			}
				
			if (nextExplore != null) {
				if (nextExplore.size() == clustSize)
					return nextExplore;
				else if (suboptimalBest == null || nextExplore.size() > suboptimalBest.size())
					suboptimalBest = nextExplore;
			}
		}
		return suboptimalBest;
	}
	
	// Adds a single chunk location to the list of areas where this ore cluster
	// should do generation.
	private void addSpawnChunk(int chunkX, int chunkZ) {
		spawnChunks.add(new Point(chunkX, chunkZ));
	}
	
	// 
	// === File Handling ===
	//
	public void saveToFile() {
		if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) 
			return;
		
		try {
			File saveDir = new File(DimensionManager.getCurrentSaveRootDirectory(), "OreGen");
			if (!saveDir.exists())
				saveDir.mkdirs();
			File save = new File(saveDir, type.getUnlocalizedName()+".dat");
			if (!save.exists())
				save.createNewFile();
			
			NBTTagCompound ntc = new NBTTagCompound();
			int[] chunkXToSave = new int[spawnChunks.size()];
			int[] chunkZToSave = new int[spawnChunks.size()];
			for(int i=0; i<chunkXToSave.length; i++) {
				chunkXToSave[i] = spawnChunks.get(i).x;
				chunkZToSave[i] = spawnChunks.get(i).y;
			}
			ntc.setIntArray("chunkX", chunkXToSave);
			ntc.setIntArray("chunkZ", chunkZToSave);
			
			DataOutputStream data = new DataOutputStream(new FileOutputStream(save));
			CompressedStreamTools.writeCompressed(ntc, data);
			data.close();
		} catch (Exception e) {
			System.out.println("Failed to save oregen properties to file.");
		}
	}
	
	public void loadFromFile() {
		NBTTagCompound loadData = null;
		
		try {
			File saveDir = new File(DimensionManager.getCurrentSaveRootDirectory(), "OreGen");
			if (!saveDir.exists())
				saveDir.mkdirs();
			File save = new File(saveDir, type.getUnlocalizedName()+".dat");
			if (save.exists()) {
				DataInputStream data = new DataInputStream(new FileInputStream(save));
				loadData = CompressedStreamTools.readCompressed(data);
				data.close();
			}			
		} catch (Exception e) {
			System.out.println("Failed to load oregen properties from file.");
		}
		
		if (loadData != null) {
			spawnChunks.clear();
			int[] chunkXLoad = loadData.getIntArray("chunkX");
			int[] chunkZLoad = loadData.getIntArray("chunkZ");
			for(int i=0; i<chunkXLoad.length; i++) {
				addSpawnChunk(chunkXLoad[i], chunkZLoad[i]);
			}
		}
	}
	
	// Loads from or generates a config file for all of the ore generation
	// properties dealing with this specific cluster gen.
	public static ResourceCluster generateFromConfig(Configuration c,
		Block type, int defaultMinVein, int defaultMaxVein, int defaultMinVeinCount
		, int defaultMaxVeinCount, int defaultMinClusterSize, int defaultMaxClusterSize
		, int defaultMinClusterCount, int defaultMaxClusterCount, int defaultMinY
		, int defaultMaxY) {
		
		String blockName = type.getLocalizedName();
		return new ResourceCluster(
			type,
			c.get(oreGenCategory, blockName+".minVeinSize", defaultMinVein).getInt(),
			c.get(oreGenCategory, blockName+".maxVeinSize", defaultMaxVein).getInt(),
			c.get(oreGenCategory, blockName+".minVeinsPerChunk", defaultMinVeinCount).getInt(),
			c.get(oreGenCategory, blockName+".maxVeinsPerChunk", defaultMaxVeinCount).getInt(),
			c.get(oreGenCategory, blockName+".minChunksPerCluster", defaultMinClusterSize).getInt(),
			c.get(oreGenCategory, blockName+".maxChunksPerCluster", defaultMaxClusterSize).getInt(),
			c.get(oreGenCategory, blockName+".minClustersInWorld", defaultMinClusterCount).getInt(),
			c.get(oreGenCategory, blockName+".maxClustersInWorld", defaultMaxClusterCount).getInt(),
			c.get(oreGenCategory, blockName+".minClusterSpawnHeight", defaultMinY).getInt(),
			c.get(oreGenCategory, blockName+".maxClusterSpawnHeight", defaultMaxY).getInt()
		);
	}
	
	// Descriptions of the config file properties for the cluster gens.
	public static void writeConfigComment(Configuration c) {
		c.addCustomCategoryComment(oreGenCategory, 
				" VeinSize: The number of blocks that can appear together in a single vein."
				+ "\n SpawnHeight: The vertical range in which the veins can be spawned."
				+ "\n VeinsPerChunk: The number of veins that can appear within a 16x16 area of blocks."
				+ "\n ChunksPerCluster: The number of adjacent chunks that will spawn this type of block."
				+ "\n ClustersInWorld: The number of these chunk clusters that can appear within the entire finite world.");
	}
}

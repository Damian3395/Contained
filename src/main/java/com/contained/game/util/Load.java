package com.contained.game.util;

import java.awt.Point;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;

import com.contained.game.Contained;
import com.contained.game.user.PlayerTeam;
import com.contained.game.user.PlayerTeamIndividual;
import com.contained.game.user.PlayerTeamInvitation;
import com.contained.game.user.PlayerTrade;
import com.contained.game.world.GenerateWorld;

import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

public class Load {
	public static void loadWorldData(World w, int dimID){
		/**
		 * Finite World Generation Data
		 */
		NBTTagCompound ntc = loadNBTFile("worldProperties"+dimID+".dat");
		if (ntc != null)
			Resources.worldRadius = ntc.getInteger("worldRadius");
		int spawnX = w.getSpawnPoint().posX/16;
		int spawnZ = w.getSpawnPoint().posZ/16;
		Resources.numWorldChunks = 0;
		for(int chunkX=spawnX-(Resources.worldRadius+Resources.wastelandPadding); 
				chunkX<=spawnX+(Resources.worldRadius+Resources.wastelandPadding); chunkX++) {
			for(int chunkZ=spawnZ-(Resources.worldRadius+Resources.wastelandPadding); 
					chunkZ<=spawnZ+(Resources.worldRadius+Resources.wastelandPadding); chunkZ++) {
				if (Util.isWasteland(w, chunkX, chunkZ) != 1)
					Resources.numWorldChunks++;
			}
		}
		for(int i=0; i<GenerateWorld.defaultOreProperties.length; i++) {
			GenerateWorld.getOreProperties(dimID, i).loadFromFile();
			GenerateWorld.getOreProperties(dimID, i).determineAllChunks(w, Resources.worldRadius);	
		}
		
		GenerateWorld.getBiomeProperties(dimID).loadFromFile(w);
		
		/**
		 * Territory & Team Data
		 */
		Contained.territoryData.clear();
		Contained.teamData.clear();
		Contained.teamMemberData.clear();
		Contained.teamInvitations.clear();
		Contained.trades.clear();
		
		ntc = loadNBTFile("territoryInfo"+dimID+".dat");
		if (ntc != null) {			
			NBTTagList teamList = ntc.getTagList("teamList", (byte)10);
			for(int i=0; i<teamList.tagCount(); i++) {
				NBTTagCompound data = teamList.getCompoundTagAt(i);
				PlayerTeam team = new PlayerTeam(data);		
				Contained.teamData.add(team);
				if (data.hasKey("territoryX")) {
					int[] terrX = data.getIntArray("territoryX");
					int[] terrZ = data.getIntArray("territoryZ");
					for(int j=0; j<terrX.length; j++)
						Contained.territoryData.put(new Point(terrX[j], terrZ[j]), team.id);
				}
			}
			NBTTagList playerList = ntc.getTagList("playerList", (byte)10);
			for(int i=0; i<playerList.tagCount(); i++) {
				NBTTagCompound data = playerList.getCompoundTagAt(i);
				PlayerTeamIndividual player = new PlayerTeamIndividual(data);
				Contained.teamMemberData.add(player);
			}
			NBTTagList invitationList = ntc.getTagList("invitationList", (byte)10);
			for(int i=0; i<invitationList.tagCount(); i++) {
				NBTTagCompound data = invitationList.getCompoundTagAt(i);
				PlayerTeamInvitation invite = new PlayerTeamInvitation(data);
				Contained.teamInvitations.add(invite);
			}
			NBTTagList tradeList = ntc.getTagList("tradeList", (byte)10);
			for(int i=0; i<tradeList.tagCount(); i++){
				NBTTagCompound data = tradeList.getCompoundTagAt(i);
				PlayerTrade trade = new PlayerTrade(data);
				Contained.trades.add(trade);
			}
		}
	}
	
	/**
	 * Load a local file into an NBT compound.
	 */
	public static NBTTagCompound loadNBTFile(String fileName) {
		NBTTagCompound loadData = null;
		
		try {
			File saveDir = new File(DimensionManager.getCurrentSaveRootDirectory(), "FiniteWorldData");
			if (!saveDir.exists())
				saveDir.mkdirs();
			File save = new File(saveDir, fileName);
			if (save.exists()) {
				DataInputStream data = new DataInputStream(new FileInputStream(save));
				loadData = CompressedStreamTools.readCompressed(data);
				data.close();
			}			
		} catch (Exception e) {
			System.out.println("Failed to load NBT from file "+fileName+".");
		}
		return loadData;
	}
}

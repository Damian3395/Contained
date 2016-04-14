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
		if (ntc != null) {
			Contained.configs.setWorldRadius(dimID, ntc.getInteger("worldRadius"));
			if (ntc.hasKey("gameTime"))
				Contained.timeLeft[dimID] = ntc.getInteger("gameTime");
			if (ntc.hasKey("isActive"))
				Contained.gameActive[dimID] = ntc.getBoolean("isActive");
		}
		for(int i=0; i<GenerateWorld.defaultOreProperties.length; i++) {
			GenerateWorld.getOreProperties(dimID, i).loadFromFile(dimID);
			GenerateWorld.getOreProperties(dimID, i).determineAllChunks(w, Contained.configs.getWorldRadius(dimID));	
		}
		
		GenerateWorld.getBiomeProperties(dimID).loadFromFile(w, dimID);
		
		/**
		 * Territory & Team Data
		 */
		Contained.getTerritoryMap(dimID).clear();
		Contained.getTeamList(dimID).clear();
		Contained.teamMemberData.clear();
		Contained.teamInvitations.clear();
		Contained.getTradeList(dimID).clear();
		
		ntc = loadNBTFile("territoryInfo"+dimID+".dat");
		if (ntc != null) {		
			NBTTagList teamList = ntc.getTagList("teamList", (byte)10);
			for(int i=0; i<teamList.tagCount(); i++) {
				NBTTagCompound data = teamList.getCompoundTagAt(i);
				PlayerTeam team = new PlayerTeam(data);		
				Contained.getTeamList(dimID).add(team);
				if (data.hasKey("territoryX")) {
					int[] terrX = data.getIntArray("territoryX");
					int[] terrZ = data.getIntArray("territoryZ");
					for(int j=0; j<terrX.length; j++)
						Contained.getTerritoryMap(dimID).put(new Point(terrX[j], terrZ[j]), team.id);
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
				Contained.getTradeList(dimID).add(trade);
			}
			
			if(dimID == 0){
				NBTTagCompound gameCounts = ntc.getCompoundTag("GameCounts");
				Contained.GAME_COUNT = gameCounts.getInteger("count");
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

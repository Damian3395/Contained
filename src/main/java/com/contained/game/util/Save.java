package com.contained.game.util;

import java.awt.Point;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.DimensionManager;
import codechicken.lib.vec.BlockCoord;

import com.contained.game.Contained;
import com.contained.game.user.PlayerTeam;
import com.contained.game.user.PlayerTeamIndividual;
import com.contained.game.user.PlayerTeamInvitation;
import com.contained.game.user.PlayerTrade;
import com.contained.game.world.GenerateWorld;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;

public class Save {
	public static void saveWorldData(int dimID) {
		//Save world generation data
		NBTTagCompound ntc = new NBTTagCompound();
		ntc.setInteger("worldRadius", Contained.configs.getWorldRadius(dimID));
		if (MiniGameUtil.isPvP(dimID) || MiniGameUtil.isTreasure(dimID)) {
			ntc.setInteger("gameTime", Contained.timeLeft[dimID]);
			ntc.setBoolean("isActive", Contained.gameActive[dimID]);
		}
		
		//Active Treasure
		ArrayList<BlockCoord> activeTreasure = Contained.getActiveTreasures(dimID);
		if (activeTreasure.size() > 0) {
			int[] activeX = new int[activeTreasure.size()];
			int[] activeY = new int[activeTreasure.size()];
			int[] activeZ = new int[activeTreasure.size()];
			for (int i=0 ;i<activeTreasure.size(); i++) {
				activeX[i] = activeTreasure.get(i).x;
				activeY[i] = activeTreasure.get(i).y;
				activeZ[i] = activeTreasure.get(i).z;
			}
			ntc.setIntArray("treasureX", activeX);
			ntc.setIntArray("treasureY", activeY);
			ntc.setIntArray("treasureZ", activeZ);
		} else {
			if (ntc.hasKey("treasureX")) {
				ntc.removeTag("treasureX");
				ntc.removeTag("treasureY");
				ntc.removeTag("treasureZ");
			}
		}
		saveNBTFile("worldProperties"+dimID+".dat", ntc);
		
		for(int i=0; i<GenerateWorld.defaultOreProperties.length; i++)
			GenerateWorld.getOreProperties(dimID, i).saveToFile(dimID);
		GenerateWorld.getBiomeProperties(dimID).saveToFile(dimID);
		
		//Categorize owned territory by team
		ntc = new NBTTagCompound();
		HashMap<String, ArrayList<Integer>> terrX = new HashMap<String, ArrayList<Integer>>();
		HashMap<String, ArrayList<Integer>> terrZ = new HashMap<String, ArrayList<Integer>>();
		
		for (Point p : Contained.getTerritoryMap(dimID).keySet()) {
			String team = Contained.getTerritoryMap(dimID).get(p);
			if (!terrX.containsKey(team)) {
				terrX.put(team, new ArrayList<Integer>());
				terrZ.put(team, new ArrayList<Integer>());
			}
			terrX.get(team).add(p.x);
			terrZ.get(team).add(p.y);
		}
		
		//Save GameCount
		if(dimID == 0){
			NBTTagCompound gameCounts = new NBTTagCompound();
			gameCounts.setInteger("count", Contained.GAME_COUNT);
			ntc.setTag("GameCounts", gameCounts);
		}
		
		//Save team data
		NBTTagList teamList = new NBTTagList();
		for(PlayerTeam team : Contained.getTeamList(dimID)) {
			NBTTagCompound teamNBT = new NBTTagCompound();
			team.writeToNBT(teamNBT);
			
			if (terrX.containsKey(team.id)) {
				int[] teamOwnX = new int[terrX.get(team.id).size()];
				int[] teamOwnZ = new int[terrX.get(team.id).size()];
				for (int i=0; i<teamOwnX.length; i++) {
					teamOwnX[i] = terrX.get(team.id).get(i);
					teamOwnZ[i] = terrZ.get(team.id).get(i);
				}
				teamNBT.setIntArray("territoryX", teamOwnX);
				teamNBT.setIntArray("territoryZ", teamOwnZ);
			}
			
			teamList.appendTag(teamNBT);
		}
		ntc.setTag("teamList", teamList);		
		
		//Save player data
		NBTTagList playerList = new NBTTagList();
		for(PlayerTeamIndividual player : Contained.teamMemberData) {
			NBTTagCompound playerNBT = new NBTTagCompound();
			player.writeToNBT(playerNBT);
			playerList.appendTag(playerNBT);
		}
		ntc.setTag("playerList", playerList);
		
		//Save trades
		NBTTagList tradeList = new NBTTagList();
		for(PlayerTrade trade : Contained.getTradeList(dimID)){
			NBTTagCompound tradeNBT = new NBTTagCompound();
			trade.writeToNBT(tradeNBT);
			tradeList.appendTag(tradeNBT);
		}
		ntc.setTag("tradeList", tradeList);
		
		//Save invitations
		NBTTagList invitationList = new NBTTagList();
		for(PlayerTeamInvitation invite : Contained.teamInvitations) {
			NBTTagCompound inviteNBT = new NBTTagCompound();
			invite.writeToNBT(inviteNBT);
			invitationList.appendTag(inviteNBT);
		}
		ntc.setTag("invitationList", invitationList);
		
		saveNBTFile("territoryInfo"+dimID+".dat", ntc);
	}
	
	/**
	 * Save an NBT compound to a local file.
	 */
	public static boolean saveNBTFile(String fileName, NBTTagCompound ntc) {
		if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) 
			return false;
		
		try {
			File saveDir = new File(DimensionManager.getCurrentSaveRootDirectory(), "FiniteWorldData");
			if (!saveDir.exists())
				saveDir.mkdirs();
			File save = new File(saveDir, fileName);
			if (!save.exists())
				save.createNewFile();			
			DataOutputStream data = new DataOutputStream(new FileOutputStream(save));
			CompressedStreamTools.writeCompressed(ntc, data);
			data.close();
		} catch (Exception e) {
			System.out.println("Failed to save NBT to file "+fileName+".");
			return false;
		}
		return true;
	}
	
	public static void removeDimFiles(int dimID) {
		File saveDir = new File(DimensionManager.getCurrentSaveRootDirectory(), "FiniteWorldData");
		File territory = new File(saveDir, "territoryInfo"+dimID+".dat");
		if (territory.exists())
			territory.delete();
		File world = new File(saveDir, "worldProperties"+dimID+".dat");
		if (world.exists())
			world.delete();
		
		GenerateWorld.getBiomeProperties(dimID).deleteFile(dimID);
		for(int i=0; i<GenerateWorld.defaultOreProperties.length; i++)
			GenerateWorld.getOreProperties(dimID, i).deleteFile(dimID);
	}
}

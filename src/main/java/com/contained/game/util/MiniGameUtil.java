package com.contained.game.util;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;

import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.ChestGenHooks;
import net.minecraftforge.common.DimensionManager;
import codechicken.lib.packet.PacketCustom;
import codechicken.lib.vec.BlockCoord;

import com.contained.game.Contained;
import com.contained.game.data.DataLogger;
import com.contained.game.entity.ExtendedPlayer;
import com.contained.game.handler.games.PVPEvents;
import com.contained.game.handler.games.TreasureEvents;
import com.contained.game.item.TreasureGem;
import com.contained.game.network.ClientPacketHandlerUtil;
import com.contained.game.network.MiniGameHandler;
import com.contained.game.user.PlayerMiniGame;
import com.contained.game.user.PlayerTeam;
import com.contained.game.user.PlayerTeamIndividual;

public class MiniGameUtil {
	public static boolean isPvP(int dimID) {
		if (dimID >= Resources.MIN_PVP_DIMID && dimID <= Resources.MAX_PVP_DIMID)
			return true;
		return false;
	}
	
	public static boolean isTreasure(int dimID) {
		if (dimID >= Resources.MIN_TREASURE_DIMID && dimID <= Resources.MAX_TREASURE_DIMID)
			return true;
		return false;
	}
	
	public static boolean isDimensionInactive(int dim) {
		if(PlayerMiniGame.get(dim) == null)
			return true;
		return false;
	}
	
	public static int gameMode(int dimID) {
		if (isPvP(dimID))
			return Resources.PVP;
		else if (isTreasure(dimID))
			return Resources.TREASURE;
		else
			return Resources.OVERWORLD;
	}
	
	public static void teamWins(String teamID, int dimID, String winCondition) {
		// TODO: When this function is called, a team has won the game. If teamID
		// is null, then the game was a tie.
		//
		// The game should now be over. Display a message saying who won. Maybe wait 
		// a short period of time (~10 seconds?) and then terminate the dimension 
		// and send everyone back to the overworld.
		
		if (teamID == null){
			Util.dimensionMessage(dimID, "Game was a tie.");
			winCondition = "TIE";
			teamID = "NONE";
		}else {
			PlayerTeam t = PlayerTeam.get(teamID);
			Util.dimensionMessage(dimID, t.displayName+" wins!");
		}
		DataLogger.insertWinningTeam(Util.getServerID(), Util.getGameID(dimID), Util.getGameMode(dimID), teamID, winCondition, Util.getDate());
		PlayerMiniGame.get(dimID).endGame(teamID, winCondition);
	}
	
	public static void testStartGame(int dimID, EntityPlayer player) {
		int gameMode = Resources.OVERWORLD;
		if (MiniGameUtil.isPvP(dimID))
			gameMode = Resources.PVP;
		else if (MiniGameUtil.isTreasure(dimID))
			gameMode = Resources.TREASURE;
		PlayerMiniGame newGame = new PlayerMiniGame(dimID, gameMode);
		newGame.testLaunch(player);
		ArrayList<EntityPlayer> joining = new ArrayList<EntityPlayer>();
		joining.add(player);
		startGame(newGame, joining);
	}
	
	public static void startGame(PlayerMiniGame game, ArrayList<EntityPlayer> playersJoining) {
		int dimID = game.getGameDimension();
		
		int gameMode = game.getGameMode();		
		Contained.timeLeft[dimID] = Contained.configs.gameDuration[gameMode]*20;
		Contained.gameActive[dimID] = true;
		Contained.miniGames.add(game);
		
		HashMap<String, Point> teamSpawnLocations = null;
		if (gameMode == Resources.PVP)
			teamSpawnLocations = PVPEvents.initializePVPGame(dimID);
		else if (gameMode == Resources.TREASURE)
			teamSpawnLocations = TreasureEvents.initializeTreasureGame(dimID);
		
		WorldServer w = DimensionManager.getWorld(dimID);
		w.setWorldTime(0);
		
		for (EntityPlayer player: playersJoining) {
			ExtendedPlayer startMiniGame = ExtendedPlayer.get(player);
			startMiniGame.gameID = game.getGameID();
			startMiniGame.setGameMode(gameMode);
			
			if (gameMode == Resources.PVP) {
				startMiniGame.setLives(Contained.configs.pvpMaxLives);
				PacketCustom syncLifePacket = new PacketCustom(Resources.MOD_ID, ClientPacketHandlerUtil.SYNC_LIVES);
				syncLifePacket.writeInt(startMiniGame.lives);
				Contained.channel.sendTo(syncLifePacket.toPacket(), (EntityPlayerMP) player);
			}
			
			//Set the player to not be waiting for a mini-game anymore.
			MiniGameHandler.cancelMiniGame((EntityPlayerMP)player);
			
			//Send the player to the dimension, and set their spawn location correctly.
			Util.travelToDimension(dimID, player, false);
			
			PlayerTeamIndividual pdata = PlayerTeamIndividual.get(player);
			startMiniGame.setGame(true);
			
			//Update player's position to their team's territory.
			if (teamSpawnLocations == null || pdata.teamID == null || !teamSpawnLocations.containsKey(pdata.teamID)) {
				Point p = Util.getRandomLocation(w);
				player.setPositionAndUpdate(p.x, w.getTopSolidOrLiquidBlock(p.x, p.y)+1, p.y);
				if (teamSpawnLocations != null)
					Util.serverDebugMessage("[Error] Failed to get spawn location for "+player.getDisplayName()+"!");
			} else {
				Point spawnPos = teamSpawnLocations.get(pdata.teamID);
				spawnPos.x += Util.randomBoth(2);
				spawnPos.y += Util.randomBoth(2);
				startMiniGame.spawnMiniGameX = spawnPos.x;
				startMiniGame.spawnMiniGameZ = w.getTopSolidOrLiquidBlock(spawnPos.x, spawnPos.y)+1;
				startMiniGame.spawnMiniGameY = spawnPos.y;
				player.setPositionAndUpdate(spawnPos.x, w.getTopSolidOrLiquidBlock(spawnPos.x, spawnPos.y)+1, spawnPos.y);
			}
			Util.searchUpForLand(w, player);
			player.setSpawnChunk(new ChunkCoordinates((int)player.posX, (int)player.posY, (int)player.posZ), false);
			
			pdata.xp = player.experienceTotal;
			pdata.level = player.experienceLevel;
			pdata.armor = player.inventory.armorInventory.clone();
			pdata.inventory = player.inventory.mainInventory.clone();
			
			int invSize = 0;
			for(ItemStack item : pdata.inventory)
				if(item != null)
					invSize++;
			
			int armorSize = 0;
			for(ItemStack item : pdata.armor)
				if(item != null)
					armorSize++;
			
			PacketCustom miniGamePacket = new PacketCustom(Resources.MOD_ID, ClientPacketHandlerUtil.SAVE_PLAYER);
			miniGamePacket.writeInt(player.experienceTotal);
			miniGamePacket.writeInt(player.experienceLevel);
			miniGamePacket.writeInt(armorSize);
			int index = 0;
			for(ItemStack item : pdata.armor)
				if(item != null){
					miniGamePacket.writeInt(index);
					NBTTagCompound itemSave = new NBTTagCompound();
					item.writeToNBT(itemSave);
					miniGamePacket.writeNBTTagCompound(itemSave);
				}
			
			index = 0;
			miniGamePacket.writeInt(invSize);
			for(ItemStack item : pdata.inventory)
				if(item != null){
					miniGamePacket.writeInt(index);
					NBTTagCompound itemSave = new NBTTagCompound();
					item.writeToNBT(itemSave);
					miniGamePacket.writeNBTTagCompound(itemSave);
					index++;
				}
			Contained.channel.sendTo(miniGamePacket.toPacket(), (EntityPlayerMP)player);
			
			player.addExperience(-(player.experienceTotal));
			player.addExperienceLevel(-(player.experienceLevel));
			clearMainInventory(player);
			clearArmorInventory(player);
		}
		
		//Sync MiniGame & Teams
		PacketCustom miniGamePacket = new PacketCustom(Resources.MOD_ID, ClientPacketHandlerUtil.MINIGAME_STARTED);
		miniGamePacket.writeInt(gameMode);
		miniGamePacket.writeInt(game.getGameID());
		NBTTagCompound miniGameData = new NBTTagCompound();
		game.writeToNBT(miniGameData);
		miniGamePacket.writeNBTTagCompound(miniGameData);
		miniGamePacket.writeInt(dimID);
		miniGamePacket.writeInt(Contained.getTeamList(dimID).size());
		for(PlayerTeam team : Contained.getTeamList(dimID)){
			NBTTagCompound teamData = new NBTTagCompound();
			team.writeToNBT(teamData);
			miniGamePacket.writeNBTTagCompound(teamData);
		}
		Contained.channel.sendToDimension(miniGamePacket.toPacket(), dimID);
		
		//Sync Timer
		ClientPacketHandlerUtil.syncMinigameTime(dimID);
	}
	
	public static PlayerMiniGame findOrCreateGame(int pendingPlayers){
		// Check for pending games.
		for(PlayerMiniGame game : Contained.miniGames)
			if(!game.isGameReady() && game.getCapacity() <= pendingPlayers)
				return game;
		
		// If no pending games, try creating a new game.
		PlayerMiniGame newGame = new PlayerMiniGame(pendingPlayers);
		if(newGame.getGameMode() == -1){
			// Couldn't create new game, because all mini-game dimensions are
			// already full.
			return null;
		}
		Contained.miniGames.add(newGame);
		return newGame;
	}
	
	public static int getCapacity(int gameMode) {
		return Contained.configs.maxTeamSize[gameMode]*Contained.configs.gameNumTeams[gameMode];
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
			
			//Prevent Chests From Spawning Deep in the Ocean or in Lava
			while(w.getBlock(x, y, z).getMaterial() == Material.water
					|| w.getBlock(x, y, z).getMaterial() == Material.lava){
				x+=r.nextInt(100)-r.nextInt(100);
				z+=r.nextInt(100)-r.nextInt(100);
				y=w.getTopSolidOrLiquidBlock(x, z);
			}
			
			// Try to see if there's any caves underneath the chest's position,
			// and spawn the chest down there instead.
			boolean foundAir = false;
			for (int newY=y-1; newY>=20; newY--) {
				if (w.isAirBlock(x, newY, z))
					foundAir = true;
				else if (foundAir) {
					y = newY+1;
					break;
				}
			}
			
			w.setBlock(x, y, z, Blocks.chest);		//generate a chest
			TileEntity chest = w.getTileEntity(x, y, z);	
			if (chest instanceof IInventory) {
				IInventory chestInv = (IInventory)chest;
				WeightedRandomChestContent.generateChestContents(r, hook.getItems(r), chestInv, hook.getCount(r));
				for(int j=0; j<36; j++) {
					if (chestInv.getStackInSlot(j) == null) {
						chestInv.setInventorySlotContents(j, 
							new ItemStack((Item)Item.itemRegistry.getObject(
								 Resources.MOD_ID+":"+TreasureGem.getUnlocalizedName(
								 Util.choose(TreasureGem.GREEN, TreasureGem.BLUE, TreasureGem.WHITE, TreasureGem.RED)
								,Util.choose(TreasureGem.TOP, TreasureGem.BOTTOM, TreasureGem.LEFT, TreasureGem.RIGHT, TreasureGem.CENTER))), 1));
						break;
					}
				}
			}
			generatedPoints.add(new BlockCoord(x, y, z));			
		}		
		ClientPacketHandlerUtil.addTreasureAndSync(w.provider.dimensionId, generatedPoints);
	}
	
	public static void removeTerritoryBlock(Point p, int dimID) {
		Contained.getTerritoryMap(dimID).remove(p);
		
		if (isPvP(dimID)) {
			// Check if this removal caused a team to have lost the very last
			// of their territory, and if so, end the game.
			String winningTeam = null;
			boolean isGameOver = false;
			for(PlayerTeam team : Contained.getTeamList(dimID)) {
				if (!Contained.getTerritoryMap(dimID).containsValue(team.id))
					isGameOver = true;
				else
					winningTeam = team.id;	
			}
			if (isGameOver)
				teamWins(winningTeam, dimID, "TERRITORY");
			
			
		}
	}
	
	public static void clearMainInventory(EntityPlayer player){
		for(int i = 0; i < player.inventory.getSizeInventory(); i++)
			player.inventory.setInventorySlotContents(i, null);
	}
	
	public static void clearArmorInventory(EntityPlayer player){
		for(int i = 0; i < player.inventory.armorInventory.length; i++)
			player.inventory.armorInventory[i] = null;
	}
}

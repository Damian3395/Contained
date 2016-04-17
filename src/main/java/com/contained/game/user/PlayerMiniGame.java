package com.contained.game.user;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import codechicken.lib.packet.PacketCustom;

import com.contained.game.Contained;
import com.contained.game.Settings;
import com.contained.game.data.DataLogger;
import com.contained.game.entity.ExtendedPlayer;
import com.contained.game.network.ClientPacketHandlerUtil;
import com.contained.game.util.MiniGameUtil;
import com.contained.game.util.Resources;
import com.contained.game.util.Util;

public class PlayerMiniGame {
	private String[] intro = {"The", "League of", "Demons of"
			, " Avengers of", "Call of", "Warlords of", "Clan of"
			, "The Order of", "Gods of", "Knights of", "Guardians of"};
	
	private String[] words = {"Greater", "Lesser", "Beast", "Demon", "Your Mother", "My Mother", "His Mother"
			, "Your Father", "My Father", "Family Matters", "Nerds", "PvP", "Treasures", "His Father"
			, "Unforgiven", "Guards", "Oblivian", "Wrath", "Sin", "War", "Prophecy", "Creepers", "Notch"};
	
	private String[] combine = {"And", "Or", "With", "Rather Than", "In Contrast", "But", "Besides"
			, "Coupled With", "Beyond", "Under", "Above", "Nearly", "Aside From", "In Essence"};
	
	private int gameMode, gameID, dim;
	
	public PlayerMiniGame(){
		Random rand = new Random();
		if(Contained.PVP_GAMES < Resources.MAX_PVP_GAMES 
				&& Contained.TREASURE_GAMES < Resources.MAX_TREASURE_GAMES){
			if(rand.nextBoolean()){
				gameMode = Resources.PVP_MODE;
				Contained.PVP_GAMES++;
			}else{
				gameMode = Resources.TREASURE_MODE;
				Contained.TREASURE_GAMES++;
			}
		}else if(Contained.PVP_GAMES < Resources.MAX_PVP_GAMES){
			gameMode = Resources.PVP_MODE;
			Contained.PVP_GAMES++;
		}else if(Contained.TREASURE_GAMES < Resources.MAX_TREASURE_GAMES){
			gameMode = Resources.TREASURE_MODE;
			Contained.TREASURE_GAMES++;
		}
		
		dim = getEmptyWorld(gameMode);
		if(dim == -1)
			return;
		
		gameID = Contained.GAME_COUNT;
		Contained.GAME_COUNT++;
	}
	
	public PlayerMiniGame(int dimID){
		super();
		this.dim = dimID;
		if(MiniGameUtil.isPvP(dimID))
			this.gameMode = Resources.PVP_MODE;
		else if(MiniGameUtil.isTreasure(dimID))
			this.gameMode = Resources.TREASURE_MODE;
	}
	
	public PlayerMiniGame(NBTTagCompound ntc) {
		this.readFromNBT(ntc);
	}
	
	//Game Player To Random Team
	public void addPlayer(EntityPlayerMP player){		
		ArrayList<PlayerTeam> teams = Contained.getTeamList(dim);
		if (teams.size() < Contained.configs.gameNumTeams[Settings.getGameConfig(dim)])
			createTeam(player);
		else { //Randomize Teams
			ArrayList<Integer> candidateTeams = new ArrayList<Integer>();
			for(int i=0; i<teams.size(); i++) {
				if (teams.get(i).numMembers() < Contained.configs.maxTeamSize[Settings.getGameConfig(dim)])
					candidateTeams.add(i);
			}
			Collections.shuffle(candidateTeams);
			if (candidateTeams.size() == 0)
				Util.serverMessage("[ERROR] Failed to add player to mini-game team, because they were all already full!");
			else
				addPlayerToTeam(player, candidateTeams.get(0));
		}
	}
	
	private void addPlayerToTeam(EntityPlayerMP player, int team) {
		PlayerTeamIndividual pdata = PlayerTeamIndividual.get(player);
		pdata.joinMiniTeam(Contained.getTeamList(dim).get(team).displayName);
	}
	
	public void removePlayer(EntityPlayerMP player) {
		PlayerTeamIndividual pdata = PlayerTeamIndividual.get(player);
		if(getTeamID(pdata.playerName) != -1)
			pdata.revertMiniGameChanges();
	}
	
	public void launchGame(){
		if(isGameReady()){
			pickRandomTeamLeaders();
			teleportPlayers(0, dim);
			Contained.timeLeft[dim] = Contained.configs.gameDuration[Settings.getGameConfig(dim)]*20;	
			Contained.gameActive[this.dim] = true;
			ClientPacketHandlerUtil.syncMinigameTime(dim);
		}
	}
	
	public void endGame(){		
		teleportPlayers(dim, 0);
		Contained.gameActive[dim] = false;
		Contained.timeLeft[dim] = 0;
		ClientPacketHandlerUtil.syncMinigameTime(dim);
		for(int i = 0; i < Contained.getTeamList(dim).size(); i++)
			Contained.getTeamList(dim).remove(i);
		Contained.miniGames.remove(this);
	}
	
	private void teleportPlayers(int from, int to){
		WorldServer lobby = MinecraftServer.getServer().worldServers[from];
		for(PlayerTeamIndividual pdata : Contained.teamMemberData){
			if(getTeamID(pdata.playerName) != -1) {
				EntityPlayer player = lobby.getPlayerEntityByName(pdata.playerName);
				if(to == dim){ //Starting New MiniGame
					ExtendedPlayer properties = ExtendedPlayer.get(player);
					properties.setGameMode(gameMode);
					properties.setJoiningGame(false);
					properties.setGame(true);
					pdata.xp = player.experienceTotal;
					pdata.setInventory(player.inventoryContainer.inventoryItemStacks);
					pdata.armor = player.inventory.armorInventory;
					clearInventory(player);
					
					Util.travelToDimension(to, player);
					
					PacketCustom startGamePacket = new PacketCustom(Resources.MOD_ID, ClientPacketHandlerUtil.MINIGAME_STARTED);
					startGamePacket.writeInt(gameMode);
					NBTTagCompound miniGameData = new NBTTagCompound();
					this.writeToNBT(miniGameData);
					startGamePacket.writeNBTTagCompound(miniGameData);
					startGamePacket.writeInt(dim);
					startGamePacket.writeInt(Contained.getTeamList(dim).size());
					for(PlayerTeam team : Contained.getTeamList(dim)){
						NBTTagCompound teamData = new NBTTagCompound();
						team.writeToNBT(teamData);
						startGamePacket.writeNBTTagCompound(teamData);
					}
					Contained.channel.sendTo(startGamePacket.toPacket(), (EntityPlayerMP) player);
				}else{ //Ending MiniGame
					ExtendedPlayer properties = ExtendedPlayer.get(player);
					properties.setGameMode(Resources.FREE_PLAY);
					properties.setGame(false);
					player.inventoryContainer = (Container) pdata.inventory;
					player.experienceTotal = pdata.xp;
					player.inventory.armorInventory = pdata.armor;
					pdata.inventory = null;
					pdata.armor = null;
					
					Util.travelToDimension(to, player);
					
					//Sync MiniGames and Teams
					PacketCustom miniGamePacket = new PacketCustom(Resources.MOD_ID, ClientPacketHandlerUtil.MINIGAME_ENDED);
					miniGamePacket.writeInt(dim);
					Contained.channel.sendTo(miniGamePacket.toPacket(), (EntityPlayerMP) player);
					
					//Sync Game Stats
					if(gameMode == Resources.PVP_MODE){
						PacketCustom pvpStatsPacket = new PacketCustom(Resources.MOD_ID, ClientPacketHandlerUtil.SYNC_PVP_STATS);
						pvpStatsPacket.writeInt(properties.pvpWon);
						pvpStatsPacket.writeInt(properties.pvpLost);
						pvpStatsPacket.writeInt(properties.kills);
						pvpStatsPacket.writeInt(properties.deaths);
						Contained.channel.sendTo(pvpStatsPacket.toPacket(), (EntityPlayerMP) player);
					}else{
						PacketCustom treasureStatsPacket = new PacketCustom(Resources.MOD_ID, ClientPacketHandlerUtil.SYNC_TEASURE_STATS);
						treasureStatsPacket.writeInt(properties.treasureWon);
						treasureStatsPacket.writeInt(properties.treasureLost);
						treasureStatsPacket.writeInt(properties.treasuresOpened);
						Contained.channel.sendTo(treasureStatsPacket.toPacket(), (EntityPlayerMP) player);
					}
				}
			}
		}
	}
	
	public boolean isGameReady() {		
		int teamPlayerCount = 0;
		for(PlayerTeam team : Contained.getTeamList(dim))
			teamPlayerCount += team.numMembers();
		
		if (teamPlayerCount >= 
				Contained.configs.maxTeamSize[Settings.getGameConfig(dim)]
			   *Contained.configs.gameNumTeams[Settings.getGameConfig(dim)])
			return true;
		return false;
	}
	
	// Index of this player's team in this dimension's team arraylist, or -1
	// if the player does not currently belong to any of this dimension's teams.
	public int getTeamID(String player) {
		PlayerTeamIndividual pdata = PlayerTeamIndividual.get(player);
		for (int i=0; i<Contained.getTeamList(dim).size(); i++) {
			if (pdata.teamID.equals(Contained.getTeamList(dim).get(i).displayName))
				return i;
		}
		return -1;
	}
	
	public int getGameDimension(){
		return dim;
	}
	
	public int getGameID(){
		return gameID;
	}
	
	public int getGameMode(){
		return gameMode;
	}
	
	private void pickRandomTeamLeaders(){
		for(PlayerTeam team : Contained.getTeamList(dim)) {
			List<String> teamPlayers = team.getTeamPlayers();
			if (teamPlayers.size() != 0) {
				Collections.shuffle(teamPlayers);
				PlayerTeamIndividual pdata = PlayerTeamIndividual.get(teamPlayers.get(0));
				pdata.setTeamLeader();
			}
			else
				Util.serverMessage("[ERROR] Tried to set a leader for a team that had no members.");
		}			
	}
	
	private void clearInventory(EntityPlayer player){
		for(int i = 0; i < player.inventory.getSizeInventory(); i++){
			if(player.inventory.getStackInSlot(i) != null)
				player.inventory.setInventorySlotContents(i, null);
		}
	}
	
	private String generateName(){
		Random rand = new Random();
		String teamName = intro[rand.nextInt(intro.length)] + " " + words[rand.nextInt(words.length)];
		
		if(rand.nextBoolean())
			teamName += " " + combine[rand.nextInt(combine.length)] + " " + words[rand.nextInt(words.length)];
		
		return teamName;
	}
	
	private boolean teamExists(String teamName){
		WorldServer[] worlds = DimensionManager.getWorlds();
		for(WorldServer world : worlds)
			for(PlayerTeam team : Contained.getTeamList(world.provider.dimensionId))
				if (team.displayName.toLowerCase().equals(teamName.toLowerCase()))
					return true;
		
		return false;
	}
	
	private int getEmptyWorld(int gameMode){
		int dim = -1;
		
		ArrayList<Integer> pvpDims = new ArrayList<Integer>(Arrays.asList(2,3,4));
		ArrayList<Integer> treasureDims = new ArrayList<Integer>(Arrays.asList(10,11,12));
		
		for(PlayerMiniGame game : Contained.miniGames){
			if(game != null){
				if(game.gameMode == gameMode){
					if(gameMode == Resources.PVP_MODE)
						pvpDims.remove(game.dim);
					else if(gameMode == Resources.TREASURE_MODE)
						treasureDims.remove(game.dim);
				}
			}
		}
		
		if(gameMode == Resources.PVP_MODE && !pvpDims.isEmpty())
			return pvpDims.get(0);
		else if(!treasureDims.isEmpty())
			return treasureDims.get(0);
		
		return dim;
	}
	
	private void createTeam(EntityPlayer player){
		Random rand = new Random();
		String teamName = generateName();
		while(teamExists(teamName))
			teamName = generateName();
		
		PlayerTeam newTeam = new PlayerTeam(teamName, rand.nextInt(PlayerTeam.formatColors.length), dim);
		Contained.getTeamList(dim).add(newTeam);
		PlayerTeamIndividual pdata = PlayerTeamIndividual.get(player);
		pdata.joinMiniTeam(teamName);	
		
		String world = Util.getDimensionString(dim);
		DataLogger.insertCreateTeam(Util.getServerID(), pdata.playerName, world, newTeam.displayName, Util.getDate());
	}
	
	public void testLaunch(EntityPlayer player){
		createTeam(player);
		PlayerTeamIndividual pdata = PlayerTeamIndividual.get(player);
		pdata.setTeamLeader();
	}
	
	public void writeToNBT(NBTTagCompound ntc) {
		ntc.setInteger("dimID", this.dim);
		ntc.setInteger("gameID", this.gameID);
		ntc.setInteger("gameMode", this.gameMode);
	}
	
	public void readFromNBT(NBTTagCompound ntc) {
		ntc.getInteger("dimID");
		ntc.getInteger("gameID");
		ntc.getInteger("gameMode");
	}
	
	public static PlayerMiniGame get(int dim){
		for(PlayerMiniGame game : Contained.miniGames)
			if(game.dim == dim)
				return game;
		
		return null;
	}
	
	public static PlayerMiniGame get(String teamName){
		for(PlayerMiniGame game : Contained.miniGames)
			if(game.teamExists(teamName))
				return game;
		
		return null;
	}
}

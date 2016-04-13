package com.contained.game.user;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;

import com.contained.game.Contained;
import com.contained.game.Settings;
import com.contained.game.data.DataLogger;
import com.contained.game.network.ClientPacketHandlerUtil;
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
		Contained.miniGames.remove(this);
	}
	
	private void teleportPlayers(int from, int to){
		WorldServer lobby = MinecraftServer.getServer().worldServers[from];
		for(PlayerTeamIndividual pdata : Contained.teamMemberData){
			if(getTeamID(pdata.playerName) != -1) {
				EntityPlayer player = lobby.getPlayerEntityByName(pdata.playerName);
				if(to == dim){
					pdata.setInventory(player.inventoryContainer.inventoryItemStacks);
					Util.travelToDimension(to, player);
				}else{
					Util.travelToDimension(to, player);
					player.inventoryContainer = (Container) pdata.inventory;
					pdata.inventory = null;
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
			if (pdata.teamID.equals(Contained.getTeamList(dim).get(i).id))
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
	
	private void createTeam(EntityPlayerMP player){
		Random rand = new Random();
		String teamName = generateName();
		while(teamExists(teamName))
			teamName = generateName();
		
		PlayerTeam newTeam = new PlayerTeam(teamName, rand.nextInt(PlayerTeam.formatColors.length), dim);
		Contained.getTeamList(dim).add(newTeam);
		PlayerTeamIndividual pdata = PlayerTeamIndividual.get(player);
		pdata.joinMiniTeam(teamName);	
		
		ClientPacketHandlerUtil.packetSyncTeams(Contained.getTeamList(dim)).sendToClients();
		String world = Util.getDimensionString(dim);
		DataLogger.insertCreateTeam(Util.getServerID(), pdata.playerName, world, newTeam.displayName, Util.getDate());
	}
}

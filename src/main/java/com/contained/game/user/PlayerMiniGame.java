package com.contained.game.user;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;

import com.contained.game.Contained;
import com.contained.game.data.DataLogger;
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
	
	private int gameMode, gameID, dim, teamOneSize, teamTwoSize;
	private String teamOne, teamTwo;
	
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
		Random rand = new Random();
		
		ArrayList<PlayerTeam> teams = Contained.getTeamList(dim);
		
		if(teams.get(0) == null){ //Create First Team
			createTeam(player);
			teamOneSize++;
		}else if(teams.get(1) == null){ //Create Second Team
			createTeam(player);
			teamTwoSize++;
		}else{ //Randomize Teams
			if(teamOneSize < Resources.MAX_MINI_GAME_TEAM_SIZE 
					&& teamTwoSize < Resources.MAX_MINI_GAME_TEAM_SIZE){
				if(rand.nextBoolean())
					addPlayerToTeam(player, 0);
				else
					addPlayerToTeam(player, 1);
					
			}else if(teamOneSize < Resources.MAX_MINI_GAME_TEAM_SIZE)
				addPlayerToTeam(player, 0);
			else if(teamTwoSize < Resources.MAX_MINI_GAME_TEAM_SIZE)
				addPlayerToTeam(player, 1);
		}
	}
	
	private void addPlayerToTeam(EntityPlayerMP player, int team){
		ArrayList<PlayerTeam> teams = Contained.getTeamList(dim);
		PlayerTeamIndividual pdata = PlayerTeamIndividual.get(player);
		pdata.joinMiniTeam(Contained.getTeamList(dim).get(team).displayName);
		
		if(team == 0)
			teamOneSize++;
		else
			teamTwoSize++;
	}
	
	public void removePlayer(EntityPlayerMP player){
		for(PlayerTeamIndividual pdata : Contained.teamMemberData)
			if(pdata.playerName.equals(player.getDisplayName())){
				if(teamOne != null && pdata.teamID.equals(teamOne)){
					pdata.revertMiniGameChanges();
					teamOneSize--;
				}else if(teamTwo != null && pdata.teamID.equals(teamTwo)){
					pdata.revertMiniGameChanges();
					teamTwoSize--;
				}
			}
	}
	
	public void launchGame(){
		if(isGameReady()){
			pickRandomTeamLeaders();
			
			teleportPlayers(0, dim);
			
			if(MiniGameUtil.isPvP(dim))
				Contained.timeLeft[dim] = Contained.configs.pvpDuration*20;
			else if(MiniGameUtil.isTreasure(dim))
				Contained.timeLeft[dim] = Contained.configs.treasureDuration*20;
				
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
			if(pdata.teamID.equals(teamOne) 
					|| pdata.teamID.equals(teamTwo)){
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
	
	public boolean isGameReady(){
		int teamOneSize, teamTwoSize;
		ArrayList<PlayerTeam> teams = Contained.getTeamList(dim);
		
		if(teams.get(0) != null)
			teamOneSize = teams.get(0).getOnlinePlayers().size();
		else 
			return false;
		
		if(teams.get(1) != null)
			teamTwoSize = teams.get(1).getOnlinePlayers().size();
		else
			return false;
		
		if(teamOneSize == Resources.MAX_MINI_GAME_TEAM_SIZE && teamTwoSize == Resources.MAX_MINI_GAME_TEAM_SIZE)
			return true;
		
		return false;
	}
	
	public boolean hasPlayer(String player){
		for(PlayerTeamIndividual pdata : Contained.teamMemberData)
			if(pdata.playerName.equals(player)){
				if(teamOne != null && pdata.teamID.equals(teamOne))
					return true;
				else if(teamTwo != null && pdata.teamID.equals(teamTwo))
					return true;
			}
		
		return false;
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
		Random rand = new Random();
		boolean teamOneLeader = false;
		boolean teamTwoLeader = false;
		int teamOneCount = 0;
		int teamTwoCount = 0;
		
		for(PlayerTeamIndividual player : Contained.teamMemberData){
			if(teamOneLeader && teamTwoLeader){
				break;
			}else if(player.teamID.equals(teamOne)){
				if((rand.nextBoolean() || teamOneCount == 4) && !teamOneLeader){
					player.setTeamLeader();
					teamOneLeader = true;
				}else
					teamOneCount++;
			}else if(player.teamID.equals(teamTwo)){
				if((rand.nextBoolean() || teamTwoCount == 4) && !teamTwoLeader){
					player.setTeamLeader();
					teamTwoLeader = true;
				}else
					teamTwoCount++;
			}
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
		
		if(teamOne == null)
			teamOne = teamName;
		else
			teamTwo = teamName;
		
		ClientPacketHandlerUtil.packetSyncTeams(Contained.getTeamList(dim)).sendToClients();
		
		String world = Util.getDimensionString(dim);
		DataLogger.insertCreateTeam(Util.getServerID(), pdata.playerName, world, newTeam.displayName, Util.getDate());
	}
}

package com.contained.game.user;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;

import com.contained.game.Contained;
import com.contained.game.util.Resources;

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
	private ArrayList<PlayerTeam> teams;
	private ArrayList<PlayerTeamIndividual> pdata;
	
	private boolean gameStarted;
	
	public PlayerMiniGame(){
		Random rand = new Random();
		int gameMode = 0;
		int dim = 0;
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
		
		int gameID = Contained.GAME_COUNT;
		Contained.GAME_COUNT++;
	}
	
	public PlayerMiniGame(int gameMode, int gameID, int dim){
		this.gameMode = gameMode;
		this.gameID = gameID;
		this.dim = dim;
		teams = new ArrayList<PlayerTeam>(2);
		pdata = new ArrayList<PlayerTeamIndividual>(10);
		gameStarted = false;
	}

	public void addTeam(PlayerTeam team){
		if(teams.size() < 2)
			teams.add(team);
		
		
	}
	
	//Game Player To Random Team
	public void addPlayer(EntityPlayerMP player){
		boolean playerAdded = false;
		Random rand = new Random();
		
		if(teams.get(0) == null){ //Create First Team
			
		}else if(teams.get(1) == null){ //Create Second Team
			
		}else{ //Randomize Teams
			
		}
	}
	
	public void launchGame(){
		if(isGameReady()){
			
		}
	}
	
	public boolean isGameReady(){
		int teamOneSize, teamTwoSize;
		
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
		for(PlayerTeamIndividual data : pdata)
			if(data.playerName.equals(player))
				return true;
		
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
	
	public boolean waiting(){
		return gameStarted;
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
	
	//TODO: How To Find Dimension That Is Not Generated
	private int getEmptyWorld(){
		int dim = 0;
		
		WorldServer[] worlds = DimensionManager.getWorlds();
		for(WorldServer world : worlds);
		
		return dim;
	}
	
	/*
	private void createTeam(EntityPlayerMP player, int dim){
		Random rand = new Random();
		String teamName = generateName();
			
		while(nameExists(player, teamName))
			teamName = generateName();
		
		PlayerTeam newTeam = new PlayerTeam(teamName, rand.nextInt(PlayerTeam.formatColors.length), dim);
		Contained.getTeamList(dim).add(newTeam);
		
		PlayerTeamIndividual pdata = PlayerTeamIndividual.get(player);
		Contained.getTeamList(player.dimension).add(newTeam);
		System.out.println(pdata.joinTeam(newTeam.id, true).toString());
		ClientPacketHandlerUtil.packetSyncTeams(Contained.getTeamList(player.dimension)).sendToClients();
		
		String world = Util.getDimensionString(player.dimension);
		DataLogger.insertCreateTeam(Util.getServerID(), pdata.playerName, world, newTeam.displayName, Util.getDate());
	}
	*/
}

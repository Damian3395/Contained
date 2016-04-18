package com.contained.game.user;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import codechicken.lib.packet.PacketCustom;

import com.contained.game.Contained;
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

	public PlayerMiniGame(int playersPending) {
		this(playersPending >= MiniGameUtil.getCapacity(Resources.PVP)
				, playersPending >= MiniGameUtil.getCapacity(Resources.TREASURE));		
	}

	public PlayerMiniGame(boolean enablePvP, boolean enableTreasure){
		Random rand = new Random();
		gameMode = -1;
		if(Contained.PVP_GAMES < Resources.MAX_PVP_GAMES 
				&& Contained.TREASURE_GAMES < Resources.MAX_TREASURE_GAMES
				&& enablePvP && enableTreasure) {
			if(rand.nextBoolean())
				gameMode = Resources.PVP;
			else
				gameMode = Resources.TREASURE;
		} 
		else if (Contained.PVP_GAMES < Resources.MAX_PVP_GAMES && enablePvP)
			gameMode = Resources.PVP;
		else if(Contained.TREASURE_GAMES < Resources.MAX_TREASURE_GAMES && enableTreasure)
			gameMode = Resources.TREASURE;

		if (gameMode != -1) {
			dim = getEmptyWorld(gameMode);
			if(dim == -1)
				gameMode = -1;
		}

		if (gameMode == -1)
			return;
		else if (gameMode == Resources.TREASURE)
			Contained.TREASURE_GAMES++;
		else if (gameMode == Resources.PVP)
			Contained.PVP_GAMES++;

		gameID = Contained.GAME_COUNT;
		Contained.GAME_COUNT++;
	}
	
	public PlayerMiniGame(int dimID, int gameMode){
		super();
		this.dim = dimID;
		this.gameMode = gameMode;
	}

	public PlayerMiniGame(NBTTagCompound ntc) {
		this.readFromNBT(ntc);
	}

	//Game Player To Random Team
	public void addPlayer(EntityPlayer player){		
		ArrayList<PlayerTeam> teams = Contained.getTeamList(dim);
		if (teams.size() < Contained.configs.gameNumTeams[gameMode])
			createTeam(player);
		else { //Randomize Teams
			ArrayList<Integer> candidateTeams = new ArrayList<Integer>();
			for(int i=0; i<teams.size(); i++) {
				if (teams.get(i).numMembers() < Contained.configs.maxTeamSize[gameMode])
					candidateTeams.add(i);
			}
			Collections.shuffle(candidateTeams);
			if (candidateTeams.size() == 0)
				Util.serverDebugMessage("[ERROR] Failed to add player to mini-game team, because they were all already full!");
			else
				addPlayerToTeam(player, candidateTeams.get(0));
		}

		Util.serverDebugMessage(player.getDisplayName()+" is now on team "+PlayerTeam.get(PlayerTeamIndividual.get(player).teamID).displayName);
	}

	private void addPlayerToTeam(EntityPlayer player, int team) {
		PlayerTeamIndividual pdata = PlayerTeamIndividual.get(player);
		pdata.joinMiniTeam(Contained.getTeamList(dim).get(team).id);
	}

	public void removePlayer(EntityPlayerMP player) {
		PlayerTeamIndividual pdata = PlayerTeamIndividual.get(player);
		if(getTeamID(pdata) != -1)
			pdata.revertMiniGameChanges();
	}

	public void launchGame(){
		if(isGameReady()){
			pickRandomTeamLeaders();
			teleportPlayers(0, dim);
		}
	}

	public void endGame(){	
		Util.serverDebugMessage("Ending DIM"+dim+" game");
		
		PacketCustom miniGamePacket = new PacketCustom(Resources.MOD_ID, ClientPacketHandlerUtil.MINIGAME_ENDED);
		miniGamePacket.writeInt(dim);
		Contained.channel.sendToDimension(miniGamePacket.toPacket(), dim);		

		Contained.gameActive[dim] = false;
		Contained.timeLeft[dim] = 0;
		ClientPacketHandlerUtil.syncMinigameTime(dim);
		Contained.getActiveTreasures(dim).clear();
		Contained.getTeamList(dim).clear();
		for(PlayerMiniGame game : Contained.miniGames)
			if(game.getGameDimension() == dim){
				Contained.miniGames.remove(game);
				break;
			}
		for(int i = 0; i < Contained.gameScores[dim].length; i++)
			Contained.gameScores[dim][i] = 0;
		
		if(MiniGameUtil.isTreasure(dim))
			Contained.getActiveTreasures(dim).clear();
		
		teleportPlayers(dim, 0);	
	}

	private void teleportPlayers(int from, int to){
		WorldServer lobby = MinecraftServer.getServer().worldServers[from];
		for(PlayerTeamIndividual pdata : Contained.teamMemberData){
			if(getTeamID(pdata) != -1) {
				EntityPlayerMP player = (EntityPlayerMP) lobby.getPlayerEntityByName(pdata.playerName);
				if(to == dim){ //Starting New MiniGame
					MiniGameUtil.startGame(to, player);
				}else{ //Ending MiniGame
					MiniGameUtil.startGame(to, player);
				}
			}
		}
	}

	public boolean isGameReady() {		
		int teamPlayerCount = 0;
		for(PlayerTeam team : Contained.getTeamList(dim))
			teamPlayerCount += team.numMembers();

		if (teamPlayerCount >= getCapacity())
			return true;
		return false;
	}

	// Index of this player's team in this dimension's team arraylist, or -1
	// if the player does not currently belong to any of this dimension's teams.
	public int getTeamID(PlayerTeamIndividual pdata) {
		if (pdata.teamID == null)
			return -1;

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

	public int getCapacity(){
		return MiniGameUtil.getCapacity(this.gameMode);
	}

	public int numPlayers() {
		int count = 0;
		for(PlayerTeamIndividual pdata : Contained.teamMemberData)
			if(getTeamID(pdata) != -1)
				count++;
		return count;
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
				Util.serverDebugMessage("[ERROR] Tried to set a leader for a team that had no members.");
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
					if(gameMode == Resources.PVP)
						pvpDims.remove(game.dim);
					else if(gameMode == Resources.TREASURE)
						treasureDims.remove(game.dim);
				}
			}
		}

		if(gameMode == Resources.PVP && !pvpDims.isEmpty())
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
		pdata.joinMiniTeam(newTeam.id);	

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
		this.dim = ntc.getInteger("dimID");
		this.gameID =  ntc.getInteger("gameID");
		this.gameMode = ntc.getInteger("gameMode");
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
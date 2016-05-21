package com.contained.game.user;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import codechicken.lib.packet.PacketCustom;

import com.contained.game.Contained;
import com.contained.game.data.DataLogger;
import com.contained.game.entity.ExtendedPlayer;
import com.contained.game.item.DowsingRod;
import com.contained.game.item.ItemTerritory;
import com.contained.game.network.ClientPacketHandlerUtil;
import com.contained.game.util.MiniGameUtil;
import com.contained.game.util.Resources;
import com.contained.game.util.Util;
import com.contained.game.world.block.EmblemBlock;
import com.contained.game.world.block.TerritoryMachine;

public class PlayerMiniGame {
	private String[] intro = {"The", "League of", "Demons of"
			, " Avengers of", "Call of", "Warlords of", "Clan of"
			, "The Order of", "Gods of", "Knights of", "Guardians of"};

	private String[] words = {"Greater", "Lesser", "Beast", "Demon", "Your Mother", "My Mother", "His Mother"
			, "Your Father", "My Father", "Family Matters", "Nerds", "PvP", "Treasures", "His Father"
			, "Unforgiven", "Guards", "Oblivion", "Wrath", "Sin", "War", "Prophecy", "Creepers", "Notch"};

	private String[] combine = {"And", "Or", "With", "Rather Than", "In Contrast", "But", "Besides"
			, "Coupled With", "Beyond", "Under", "Above", "Nearly", "Aside From", "In Essence"};

	private int gameMode, gameID, dim;
	
	public static ItemStack[] firstPlace = {
			new ItemStack(Items.diamond_axe, 1),
			new ItemStack(Items.diamond_horse_armor, 1),
			new ItemStack(Items.diamond_pickaxe, 1),
			new ItemStack(Items.diamond_shovel, 1),
			new ItemStack(Items.diamond_sword, 1),
			new ItemStack(Items.diamond_boots, 1),
			new ItemStack(Items.diamond_chestplate, 1),
			new ItemStack(Items.diamond_helmet, 1),
			new ItemStack(Items.diamond_leggings, 1),
			new ItemStack(Items.diamond, 12),
			new ItemStack(Blocks.enchanting_table, 1),
			new ItemStack(Blocks.enchanting_table, 1),
			new ItemStack(Blocks.ender_chest, 2),
			new ItemStack(ItemTerritory.addTerritory, 64),
			new ItemStack(Items.emerald, 16),
			new ItemStack(Items.spawn_egg, 8, 51),
			new ItemStack(Items.spawn_egg, 8, 54),
			new ItemStack(DowsingRod.instanceFinite, 1),
			new ItemStack(DowsingRod.instanceFinite, 1),
			new ItemStack(DowsingRod.instanceFinite, 1),
			new ItemStack(DowsingRod.instanceFinite, 1),
	};
	
	public static ItemStack[] secondPlace = {
			new ItemStack(Items.golden_apple, 2),
			new ItemStack(Items.golden_carrot, 2),
			new ItemStack(Items.gold_ingot, 16),
			new ItemStack(Blocks.rail, 32),
			new ItemStack(Blocks.golden_rail, 16),
			new ItemStack(EmblemBlock.earthEmblemAct, 2),
			new ItemStack(EmblemBlock.fireEmblemAct, 2),
			new ItemStack(EmblemBlock.waterEmblemAct, 2),
			new ItemStack(EmblemBlock.windEmblemAct, 2),
			new ItemStack(Blocks.obsidian, 8),
			new ItemStack(TerritoryMachine.instance, 1),
			new ItemStack(Items.experience_bottle, 32),
	};
	
	public static ItemStack[] thirdPlace = {
			new ItemStack(Items.iron_axe, 1),
			new ItemStack(Items.iron_hoe, 1),
			new ItemStack(Items.iron_door, 1),
			new ItemStack(Items.iron_horse_armor, 1),
			new ItemStack(Items.iron_pickaxe, 1),
			new ItemStack(Items.iron_shovel, 1),
			new ItemStack(Items.iron_sword, 1),
			new ItemStack(Items.iron_boots, 1),
			new ItemStack(Items.iron_chestplate, 1),
			new ItemStack(Items.iron_helmet, 1),
			new ItemStack(Items.iron_leggings, 1),
			new ItemStack(Items.bucket, 1),
			new ItemStack(Items.shears, 1),
			new ItemStack(Items.compass, 1),
			new ItemStack(Items.iron_ingot, 16),
			new ItemStack(Blocks.nether_brick, 64),
			new ItemStack(Blocks.nether_brick_fence, 32),
			new ItemStack(Blocks.nether_brick_stairs, 32),
	};
	
	public static ItemStack[] fourthPlace = {
			new ItemStack(Items.stone_axe, 1),
			new ItemStack(Items.wooden_axe, 1),
			new ItemStack(Items.stone_hoe, 1),
			new ItemStack(Items.wooden_door, 2),
			new ItemStack(Items.stone_pickaxe, 1),
			new ItemStack(Items.wooden_hoe, 1),
			new ItemStack(Items.stone_shovel, 1),
			new ItemStack(Items.wooden_pickaxe, 1),
			new ItemStack(Items.stone_sword, 1),
			new ItemStack(Items.wooden_shovel, 1),
			new ItemStack(Items.wooden_sword, 1),
			new ItemStack(Items.leather_boots, 1),
			new ItemStack(Items.leather_chestplate, 1),
			new ItemStack(Items.leather_helmet, 1),
			new ItemStack(Items.leather_leggings, 1),
			new ItemStack(Items.leather, 16),
			new ItemStack(Items.fishing_rod, 1),
			new ItemStack(Items.boat, 1),
			new ItemStack(Items.bow, 1),
			new ItemStack(Items.arrow, 24),
			new ItemStack(Items.bed, 1),
			new ItemStack(Items.cake, 1),
			new ItemStack(Items.cookie, 16),
			new ItemStack(Items.clock, 1),
			new ItemStack(Items.saddle, 1)
	};
	
	public static ItemStack[] fifthPlace = {
			new ItemStack(Items.apple,8),
			new ItemStack(Items.record_11, 1),
			new ItemStack(Items.baked_potato, 8),
			new ItemStack(Items.record_13, 1),
			new ItemStack(Items.cooked_beef, 8),
			new ItemStack(Items.record_blocks, 1),
			new ItemStack(Items.bone, 16),
			new ItemStack(Items.record_cat, 1),
			new ItemStack(Items.bowl, 8),
			new ItemStack(Items.record_chirp, 1),
			new ItemStack(Items.bread, 8),
			new ItemStack(Items.record_far, 1),
			new ItemStack(Items.carrot, 8),
			new ItemStack(Items.record_mall, 1),
			new ItemStack(Items.cooked_chicken, 8),
			new ItemStack(Items.coal, 16),
			new ItemStack(Items.record_mellohi, 1),
			new ItemStack(Items.clay_ball, 16),
			new ItemStack(Items.record_stal, 1),
			new ItemStack(Items.cooked_fished, 8),
			new ItemStack(Items.record_strad, 1),
			new ItemStack(Items.cooked_porkchop, 8),
			new ItemStack(Items.record_wait, 1),
			new ItemStack(Items.egg, 16),
			new ItemStack(Items.record_ward, 1),
			new ItemStack(Items.feather, 16),
			new ItemStack(Items.flint, 32),
			new ItemStack(Items.sugar, 16),
			new ItemStack(Items.wheat, 32),
			new ItemStack(Items.string, 16),
	};

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
		this.dim = dimID;
		this.gameMode = gameMode;
		this.gameID = -1;
	}

	public PlayerMiniGame(NBTTagCompound ntc) {
		this.readFromNBT(ntc);
	}

	//Game Player To Random Team
	public void addPlayer(EntityPlayer player){
		if(player == null)
			return;

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
	}

	private void addPlayerToTeam(EntityPlayer player, int team) {
		PlayerTeamIndividual pdata = PlayerTeamIndividual.get(player);
		pdata.joinMiniTeam(Contained.getTeamList(dim).get(team).id);
		DataLogger.insertMiniGamePlayer(Util.getServerID(), gameID, gameMode, player.getDisplayName(), Contained.getTeamList(dim).get(team).displayName, Util.getDate());
	}

	public void removePlayer(EntityPlayerMP player) {
		PlayerTeamIndividual pdata = PlayerTeamIndividual.get(player);
		if(getTeamID(pdata) != -1){
			pdata.revertMiniGameChanges();
			DataLogger.deleteMiniGamePlayer(player.getDisplayName());
		}
	}

	public void launchGame(ArrayList<EntityPlayer> playersJoining){
		if (MiniGameUtil.isPvP(dim))
			gameMode = Resources.PVP;
		else if (MiniGameUtil.isTreasure(dim))
			gameMode = Resources.TREASURE;

		DataLogger.insertNewMiniGame(Util.getServerID(), gameID, gameMode, Util.getDate());

		if(isGameReady()){
			pickRandomTeamLeaders();
			MiniGameUtil.startGame(this, playersJoining);
		}
	}

	public void endGame(String winningTeamID, String winCondition){
		ArrayList<PlayerTeam> teams = Contained.getTeamList(dim);
		int winScore = 0;
		for(int i = 0; i < teams.size(); i++){
			if(teams.get(i).id.equals(winningTeamID)){
				winScore = Contained.gameScores[dim][i];
				DataLogger.insertGameScore(Util.getServerID(), 
					gameID, gameMode, teams.get(i).displayName, 
					Contained.gameScores[dim][i], Contained.timeLeft[dim], Util.getDate());
				break;
			}
		}

		Util.serverDebugMessage("Ending DIM"+dim+" game");

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

		for(EntityPlayer player : getOnlinePlayers()){
			PlayerTeamIndividual pdata = PlayerTeamIndividual.get(player);
			ExtendedPlayer properties = ExtendedPlayer.get(player);
			
			int playerScore = 0;
			if(MiniGameUtil.isPvP(dim) && pdata.teamID != null){
				playerScore = (properties.curKills*3) - (properties.curDeaths*3) + properties.curAntiTerritory;
				DataLogger.insertPVPScore(Util.getServerID(), gameID, player.getDisplayName(), 
						pdata.teamID, properties.curKills, properties.curDeaths, 
						properties.curAntiTerritory, Util.getDate());
				if(!winCondition.equals("TIE")){
					if(pdata.teamID.equalsIgnoreCase(winningTeamID))
						properties.pvpWon++;
					else if (!winningTeamID.equals("Debug") && !winningTeamID.equals("Kicked"))
						properties.pvpLost++;
				}
				
				properties.kills+=properties.curKills;
				properties.deaths+=properties.curDeaths;
				properties.antiTerritory+=properties.curAntiTerritory;
			}else if(MiniGameUtil.isTreasure(dim) && pdata.teamID != null){
				playerScore = properties.curTreasuresOpened + properties.curAltersActivated*3;
				DataLogger.insertTreasureScore(Util.getServerID(), 
						gameID, player.getDisplayName(), pdata.teamID, 
						properties.curTreasuresOpened, properties.curAltersActivated, Util.getDate());
				if(!winCondition.equals("TIE")){
					if(pdata.teamID.equalsIgnoreCase(winningTeamID))
						properties.treasureWon++;
					else if (!winningTeamID.equals("Debug") && !winningTeamID.equals("Kicked"))
						properties.treasureLost++;
				}
				
				properties.treasuresOpened+=properties.curTreasuresOpened;
				properties.altersActivated+=properties.curAltersActivated;
			}
			
			//Reward XP Points To Player
			String teamMiniGame = pdata.teamID;
			if(winningTeamID != null && winCondition != null){
				boolean emptySlot = false;
				int index = -1;
				for(int i = 0; i < pdata.inventory.length; i++){
					ItemStack item = pdata.inventory[i];
					if(item == null){
						emptySlot = true;
						index = i;
						break;
					}	
				}
				
				if(!emptySlot)
					rewardXP(player, pdata, properties.altersActivated, properties.antiTerritory, properties.kills, playerScore, winScore, winCondition, teamMiniGame.equals(winningTeamID));
			}
			
			Util.travelToDimension(0, player, false);
			
			//Reward Item To Player
			if(winningTeamID != null && teamMiniGame != null && winCondition != null){
				if(player.inventory.getFirstEmptyStack() > -1)
					rewardItem(player, properties.altersActivated, properties.antiTerritory, properties.kills, playerScore, winScore, winCondition, teamMiniGame.equals(winningTeamID));
			}
			
			if(MiniGameUtil.isPvP(dim)){
				PacketCustom syncScore = new PacketCustom(Resources.MOD_ID, ClientPacketHandlerUtil.SYNC_PVP_STATS);
				syncScore.writeInt(properties.pvpWon);
				syncScore.writeInt(properties.pvpLost);
				syncScore.writeInt(properties.kills);
				syncScore.writeInt(properties.deaths);
				syncScore.writeInt(properties.antiTerritory);
				Contained.channel.sendTo(syncScore.toPacket(), (EntityPlayerMP) player);
			}else if(MiniGameUtil.isTreasure(dim)){
				PacketCustom syncScore = new PacketCustom(Resources.MOD_ID, ClientPacketHandlerUtil.SYNC_TEASURE_STATS);
				syncScore.writeInt(properties.treasureWon);
				syncScore.writeInt(properties.treasureLost);
				syncScore.writeInt(properties.treasuresOpened);
				syncScore.writeInt(properties.altersActivated);
				Contained.channel.sendTo(syncScore.toPacket(), (EntityPlayerMP) player);
			}
		}

		PacketCustom miniGamePacket = new PacketCustom(Resources.MOD_ID, ClientPacketHandlerUtil.MINIGAME_ENDED);
		miniGamePacket.writeInt(dim);
		Contained.channel.sendToDimension(miniGamePacket.toPacket(), 0);
	}

	public ArrayList<PlayerTeamIndividual> getGamePlayers() {
		ArrayList<PlayerTeamIndividual> players = new ArrayList<PlayerTeamIndividual>();
		for(PlayerTeamIndividual pdata : Contained.teamMemberData)
			if(getTeamID(pdata) != -1) 
				players.add(pdata);	

		return players;
	}
	
	public List<EntityPlayer> getOnlinePlayers() {
		WorldServer w = DimensionManager.getWorld(dim);
		if (w != null && w.playerEntities != null)
			return new ArrayList<EntityPlayer>(w.playerEntities);
		else
			return new ArrayList<EntityPlayer>();
	}

	private static boolean ignoreWinCondition(String condition) {
		if (condition.equals("Debug") || condition.equals("Inactive") || condition.equals("Kicked"))
			return true;
		return false;
	}
	
	//Determine Your Reward Based on Your Contribution To The Teams' Total Score
	public static void rewardItem(EntityPlayer player, int alters, int territory, int kills, int score, int totalScore, String winCondition, boolean inWinningTeam){
		if (ignoreWinCondition(winCondition))
			return;
		
		double percentage = (double)((double) score / (double) totalScore);
		
		//Special Rewards
		if(winCondition.equals("EMBLEMS")){
			switch(alters){
				case 1: percentage+=0.1; break;
				case 2: percentage+=0.25; break;
				case 3: percentage+=0.5; break;
			}
		}else if(winCondition.equals("TERRITORY")){
			double anti = territory;
			double total = Math.pow((Contained.configs.pvpTerritorySize*2+1), 2.0);
			percentage+=(anti/total);
		}else if(winCondition.equals("MAX_KILLS")){
			double total = Contained.configs.pvpMaxLives*Contained.configs.maxTeamSize[Resources.PVP];
			percentage+=((double)kills/total);
		}
		
		ItemStack reward = null;
		Random rand = new Random();
		// If on the winning team, eligible for first, second, or third tier rewards.
		// If on the losing team, only eligible for fourth or fifth tier rewards.
		if (inWinningTeam && !winCondition.equals("TIE")) {
			if(percentage >= 0.3)
				reward = firstPlace[rand.nextInt(firstPlace.length)];
			else if(percentage >= 0.2)
				reward = secondPlace[rand.nextInt(secondPlace.length)];
			else
				reward = thirdPlace[rand.nextInt(thirdPlace.length)];
		}
		else {
			if(percentage >= 0.2)
				reward = fourthPlace[rand.nextInt(fourthPlace.length)];
			else 
				reward = fifthPlace[rand.nextInt(fifthPlace.length)];
		}
		
		player.inventory.addItemStackToInventory(reward);
		
		Util.displayMessage(player, Util.infoCode+reward.getDisplayName());
		
		PacketCustom rewardPacket = new PacketCustom(Resources.MOD_ID, ClientPacketHandlerUtil.ADD_ITEM);
		rewardPacket.writeItemStack(reward);
		Contained.channel.sendTo(rewardPacket.toPacket(), (EntityPlayerMP) player);
	}

	//Determine Your Reward Based On Your Contribution To The Teams' Total Score
	public static void rewardXP(EntityPlayer player, PlayerTeamIndividual pdata, 
			int alters, int territory, int kills,
			int score, int totalScore, String winCondition, boolean inWinningTeam){
		if (ignoreWinCondition(winCondition))
			return;
		
		double percentage = (double)((double) score / (double) totalScore);
		
		//Find XP needed to Reach Next Level
		int xpNeeded = 0;
		if(pdata.level >= 0 && pdata.level <= 16){
			xpNeeded = 2 * pdata.level + 7;
		}else if(pdata.level >= 17 && pdata.level <= 31){
			xpNeeded = 5 * pdata.level - 38;
		}else{
			xpNeeded = 9 * pdata.level - 158;
		}
		xpNeeded -= pdata.xp;
		
		//Special Rewards
		if(winCondition.equals("EMBLEMS")){
			switch(alters){
				case 1: percentage+=0.1; break;
				case 2: percentage+=0.25; break;
				case 3: percentage+=0.5; break;
			}
		}else if(winCondition.equals("TERRITORY")){
			double anti = territory;
			double total = Math.pow((Contained.configs.pvpTerritorySize*2+1), 2.0);
			percentage+=(anti/total);
		}else if(winCondition.equals("MAX_KILLS")){
			double total = Contained.configs.pvpMaxLives*Contained.configs.maxTeamSize[Resources.PVP];
			percentage+=((double)kills/total);
		}
		
		float multiplier = 20;
		if (!inWinningTeam || winCondition.equals("TIE"))
			multiplier = 4;
		if(percentage >= 0.5)
			xpNeeded*=0.9*multiplier;
		else if(percentage >= 0.4)
			xpNeeded*=0.75*multiplier;
		else if(percentage >= 0.3)
			xpNeeded*=0.5*multiplier;
		else if(percentage >= 0.2)
			xpNeeded*=0.25*multiplier;
		else if(percentage >= 0.1)
			xpNeeded*=0.1*multiplier;
		else
			xpNeeded*=0.05*multiplier;
		
		Util.displayMessage(player, Util.infoCode+xpNeeded);
		
		pdata.xp+=xpNeeded;
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
	
	public int numOnlinePlayers() {
		return Math.min(getOnlinePlayers().size(), numPlayers());
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

	private String generateName(){
		Random rand = new Random();
		String teamName = "";
		do {
			teamName = intro[rand.nextInt(intro.length)] + " " + words[rand.nextInt(words.length)];
			if(rand.nextBoolean())
				teamName += " " + combine[rand.nextInt(combine.length)] + " " + words[rand.nextInt(words.length)];
		} while(teamName.length() > 20);

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
	
	private boolean teamColorExists(int index){
		for(PlayerTeam team : Contained.getTeamList(dim))
			if(team.colorID == index)
				return true;
		
		return false;
	}

	private int getEmptyWorld(int gameMode){
		int dim = -1;

		ArrayList<Integer> pvpDims = new ArrayList<Integer>();
		ArrayList<Integer> treasureDims = new ArrayList<Integer>();
		for(int i=Resources.MIN_PVP_DIMID; i<=Resources.MAX_PVP_DIMID; i++)
			pvpDims.add(i);
		for(int i=Resources.MIN_TREASURE_DIMID; i<=Resources.MAX_TREASURE_DIMID; i++)
			treasureDims.add(i);

		for(PlayerMiniGame game : Contained.miniGames){
			if(game != null){
				if(game.gameMode == gameMode){
					if(gameMode == Resources.PVP)
						pvpDims.remove(new Integer(game.dim));
					else if(gameMode == Resources.TREASURE)
						treasureDims.remove(new Integer(game.dim));
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
		int colorID = rand.nextInt(PlayerTeam.formatColors.length);
		
		while(teamExists(teamName))
			teamName = generateName();
		
		while(teamColorExists(colorID))
			colorID = rand.nextInt(PlayerTeam.formatColors.length);

		PlayerTeam newTeam = new PlayerTeam(teamName, colorID, dim);
		Contained.getTeamList(dim).add(newTeam);
		PlayerTeamIndividual pdata = PlayerTeamIndividual.get(player);
		pdata.joinMiniTeam(newTeam.id);	

		DataLogger.insertMiniGamePlayer(Util.getServerID(), gameID, gameMode, player.getDisplayName(), teamName, Util.getDate());
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